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

package br.usp.ime.owlchange.minimp.single.blackbox.enlarge;

import java.util.Optional;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import br.usp.ime.owlchange.OntologyPropertyChecker;

public class SyntacticConnectivityMinImpEnlarger implements MinImpEnlarger {

    private ImmutableSet<OWLEntity> initialSignature;

    public SyntacticConnectivityMinImpEnlarger(
            Set<OWLEntity> initialSignature) {
        this.initialSignature = ImmutableSet.copyOf(initialSignature);
    }

    @Override
    public Optional<Set<OWLAxiom>> enlarge(Set<OWLAxiom> ontology,
            OntologyPropertyChecker checker) {

        Set<OWLAxiom> enlarged = Sets.newHashSet();
        Set<OWLEntity> signature = Sets.newHashSet(initialSignature);

        for (OWLAxiom axiom : ontology) {
            Set<OWLEntity> axiomSignature = axiom.getSignature();
            if (axiomSignature.stream().anyMatch(signature::contains)) {
                axiomSignature.forEach(signature::add);
                enlarged.add(axiom);
                if (checker.hasProperty(enlarged)) {
                    return Optional.of(enlarged);
                }
            }
        }
        TrivialMinImpEnlarger trivialMinImpEnlarger = new TrivialMinImpEnlarger();
        return trivialMinImpEnlarger.enlarge(ontology, checker);
    }
}
