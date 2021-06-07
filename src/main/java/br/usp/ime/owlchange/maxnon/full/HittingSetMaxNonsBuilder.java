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
package br.usp.ime.owlchange.maxnon.full;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAxiom;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import br.usp.ime.owlchange.hst.HSDeque;
import br.usp.ime.owlchange.hst.HittingSetCalculator;
import br.usp.ime.owlchange.hst.HittingSetCalculator.RepairResult;
import br.usp.ime.owlchange.maxnon.single.MaxNonBuilder;

/* Computes the MaxNons using an abstract HST implementation. The property must be monotonic */
public class HittingSetMaxNonsBuilder implements MaxNonsBuilder {

    protected MaxNonBuilder maxNonBuilder;
    protected final Supplier<? extends HSDeque<ImmutableSet<OWLAxiom>>> hsDequeSupplier;

    public HittingSetMaxNonsBuilder(MaxNonBuilder maxNonBuilder,
            Supplier<? extends HSDeque<ImmutableSet<OWLAxiom>>> hsDequeSupplier) {
        this.maxNonBuilder = maxNonBuilder;
        this.hsDequeSupplier = Objects.requireNonNull(hsDequeSupplier);
    }

    @Override
    public RepairResult<OWLAxiom> maxNons(Set<OWLAxiom> ontology,
            OntologyPropertyChecker checker) {
        SimpleMaxNonHST hst = new SimpleMaxNonHST(hsDequeSupplier.get(),
                maxNonBuilder, ontology, checker);
        RepairResult<OWLAxiom> result = hst.hittingSet();
        return result;
    }

    public static class SimpleMaxNonHST extends HittingSetCalculator<OWLAxiom> {

        protected final MaxNonBuilder maxNonBuilder;
        protected final Set<OWLAxiom> ontology;
        protected final OntologyPropertyChecker checker;

        public SimpleMaxNonHST(HSDeque<ImmutableSet<OWLAxiom>> queue,
                MaxNonBuilder maxNonBuilder, Set<OWLAxiom> ontology,
                OntologyPropertyChecker checker) {
            super(queue);
            this.maxNonBuilder = maxNonBuilder;
            this.ontology = ontology;
            this.checker = checker;
        }

        @Override
        protected Optional<Set<OWLAxiom>> reusable(
                ImmutableSet<OWLAxiom> hittingPath) {
            return this.nodes.parallelStream()
                    .filter(e -> e.containsAll(hittingPath)).findAny();
        }

        @Override
        protected Optional<Set<OWLAxiom>> getNode(
                ImmutableSet<OWLAxiom> hittingPath) {
            return this.maxNonBuilder.maxNon(this.ontology, checker,
                    ImmutableSet.copyOf(hittingPath));
        }

        @Override
        protected Stream<ImmutableSet<OWLAxiom>> successors(
                ImmutableSet<OWLAxiom> hittingPath, Set<OWLAxiom> node) {
            return ontology.stream()
                    .filter(((Predicate<OWLAxiom>) node::contains).negate())
                    .map(ImmutableSet::of)
                    .map(set -> Sets.union(hittingPath, set))
                    .map(ImmutableSet::copyOf);
        }
    }
}
