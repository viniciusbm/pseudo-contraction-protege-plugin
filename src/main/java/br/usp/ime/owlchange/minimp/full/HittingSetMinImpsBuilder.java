/*
 *    Copyright 2018-2019 OWL2DL-Change Developers
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package br.usp.ime.owlchange.minimp.full;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import br.usp.ime.owlchange.hst.HSDeque;
import br.usp.ime.owlchange.hst.HittingSetCalculator;
import br.usp.ime.owlchange.hst.HittingSetCalculator.RepairResult;
import br.usp.ime.owlchange.minimp.single.MinImpBuilder;

/* Computes the MinImps using a stratified HST implementation. The property must be monotonic */
public class HittingSetMinImpsBuilder implements MinImpsBuilder {

    protected final MinImpBuilder minImpBuilder;
    protected final Supplier<? extends HSDeque<ImmutableSet<OWLAxiom>>> hsDequeSupplier;

    public HittingSetMinImpsBuilder(MinImpBuilder minImpBuilder,
            Supplier<? extends HSDeque<ImmutableSet<OWLAxiom>>> hsDequeSupplier) {
        this.minImpBuilder = minImpBuilder;
        this.hsDequeSupplier = Objects.requireNonNull(hsDequeSupplier);
    }

    @Override
    public RepairResult<OWLAxiom> minImps(Set<OWLAxiom> ontology,
            OntologyPropertyChecker checker) {

        SimpleMinImpHST hst = new SimpleMinImpHST(hsDequeSupplier.get(),
                minImpBuilder, ontology, checker);
        RepairResult<OWLAxiom> result = hst.hittingSet();
        return result;
    }

    public static class SimpleMinImpHST extends HittingSetCalculator<OWLAxiom> {

        protected final MinImpBuilder minImpBuilder;
        protected final Set<OWLAxiom> ontology;
        protected final OntologyPropertyChecker checker;

        public SimpleMinImpHST(HSDeque<ImmutableSet<OWLAxiom>> queue,
                MinImpBuilder minImpBuilder, Set<OWLAxiom> ontology,
                OntologyPropertyChecker checker) {
            super(queue);
            this.minImpBuilder = minImpBuilder;
            this.ontology = ontology;
            this.checker = checker;
        }

        @Override
        public RepairResult<OWLAxiom> hittingSet() {
            if (deque.isEmpty()) {
                deque.addNew(ImmutableSet.copyOf(ontology.stream()
                        .filter(e -> e instanceof OWLDeclarationAxiom)
                        .collect(Collectors.toSet())));
            }
            return super.hittingSet();
        }

        @Override
        protected Optional<Set<OWLAxiom>> reusable(
                ImmutableSet<OWLAxiom> hittingPath) {
            return this.nodes.parallelStream()
                    .filter(e -> Sets.intersection(e, hittingPath).isEmpty())
                    .findAny();
        }

        // TODO: be careful of concurrency and side effects of changes in the
        // ontology
        @Override
        protected Optional<Set<OWLAxiom>> getNode(
                ImmutableSet<OWLAxiom> hittingPath) {
            return this.minImpBuilder
                    .minImp(Sets.difference(ontology, hittingPath), checker);
        }

        @Override
        protected Stream<ImmutableSet<OWLAxiom>> successors(
                ImmutableSet<OWLAxiom> hittingPath, Set<OWLAxiom> node) {
            return node.stream().map(Sets::newHashSet)
                    .map(set -> Sets.union(hittingPath, set))
                    .map(ImmutableSet::copyOf);
        }
    }
}
