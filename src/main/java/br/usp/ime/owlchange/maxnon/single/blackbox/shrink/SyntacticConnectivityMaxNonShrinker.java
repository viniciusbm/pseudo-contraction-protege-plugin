/*
 * Copyright 2018-2019 OWL2DL-Change Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.usp.ime.owlchange.maxnon.single.blackbox.shrink;

import java.util.Optional;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import br.usp.ime.owlchange.OntologyPropertyChecker;

public class SyntacticConnectivityMaxNonShrinker implements MaxNonShrinker {

    private ImmutableSet<OWLEntity> initialSignature;

    public SyntacticConnectivityMaxNonShrinker(
            Set<OWLEntity> initialSignature) {
        this.initialSignature = ImmutableSet.copyOf(initialSignature);
    }

    @Override
    public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology,
            OntologyPropertyChecker checker, Set<OWLAxiom> lowerBound) {

        Set<OWLAxiom> result = Sets.newHashSet(ontology);

        if (checker.hasProperty(lowerBound)) {
            return Optional.empty();
        }

        Set<OWLEntity> signature = Sets.newHashSet(initialSignature);
        for (OWLAxiom axiom : ontology) {
            Set<OWLEntity> axiomSignature = axiom.getSignature();
            if (!lowerBound.contains(axiom) && initialSignature.stream()
                    .anyMatch(e -> axiomSignature.contains(e))) {
                signature.forEach(signature::add);
                result.remove(axiom);
                if (!checker.hasProperty(result)) {
                    return Optional.of(result);
                }
            }
        }

        TrivialMaxNonShrinker trivialMaxNonShrinker = new TrivialMaxNonShrinker();
        return trivialMaxNonShrinker.shrink(ontology, checker, lowerBound);
    }
}
