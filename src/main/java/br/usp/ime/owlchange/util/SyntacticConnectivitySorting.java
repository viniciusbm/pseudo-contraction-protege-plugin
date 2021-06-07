/*
 * Copyright 2018-2020 OWL2DL-Change Developers
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

package br.usp.ime.owlchange.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.google.common.collect.ImmutableSet;

public class SyntacticConnectivitySorting {

    private ImmutableSet<OWLEntity> initialSignature;

    private Order order;

    public enum Order {
        NONE, BINARY, COUNT
    }

    private class AxiomSyntacticConnectivityComparator
            implements Comparator<OWLAxiom> {

        private Map<OWLAxiom, Integer> cache = new HashMap<>();

        private int intersectionSize(OWLAxiom sentence) {
            if (!cache.containsKey(sentence))
                cache.put(sentence,
                        (int) sentence.getSignature().stream().filter(
                                entity -> initialSignature.contains(entity))
                                .count());
            return cache.get(sentence);
        }

        @Override
        public int compare(OWLAxiom s1, OWLAxiom s2) {
            // decreasing order
            return intersectionSize(s2) - intersectionSize(s1);
        }

    }

    public SyntacticConnectivitySorting(Set<OWLEntity> initialSignature,
            Order order) {
        this.initialSignature = ImmutableSet.copyOf(initialSignature);
        this.order = order;
    }

    public Set<OWLAxiom> sort(Set<OWLAxiom> ontology) {
        return order == Order.BINARY ? sortByIntersectionBoolean(ontology)
                : order == Order.COUNT ? sortByIntersectionSize(ontology)
                        : ontology;
    }

    private LinkedHashSet<OWLAxiom> sortByIntersectionSize(
            Set<OWLAxiom> ontology) {
        return ontology.stream()
                .sorted(new AxiomSyntacticConnectivityComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<OWLAxiom> sortByIntersectionBoolean(
            Set<OWLAxiom> ontology) {
        LinkedHashSet<OWLAxiom> sorted = ontology.stream()
                .filter(sentence -> sentence.getSignature().stream()
                        .anyMatch(initialSignature::contains))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        sorted.addAll(ontology);
        return sorted;
    }

}
