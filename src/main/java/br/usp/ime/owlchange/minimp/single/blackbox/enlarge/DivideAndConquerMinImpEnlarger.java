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
package br.usp.ime.owlchange.minimp.single.blackbox.enlarge;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class DivideAndConquerMinImpEnlarger implements MinImpEnlarger {

  @Override
  public Optional<Set<OWLAxiom>> enlarge(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    // OWLOntology copyOntology = manager.createOntology(ontology.axioms());

    Set<OWLAxiom> copyOntology = Sets.newHashSet(ontology);

    Set<OWLAxiom> enlarged = divideAndConquer(copyOntology, checker);

    return Optional.ofNullable(checker.hasProperty(enlarged) ? enlarged : null);
  }

  private Set<OWLAxiom> divideAndConquer(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    List<OWLAxiom> axioms = Lists.newArrayList(ontology);

    if (axioms.size() == 0) {
      return ontology;
    }

    int start = 0;
    int end = axioms.size();
    int middle = start + (end - start) / 2;

    List<OWLAxiom> firstHalf = axioms.subList(0, middle);
    List<OWLAxiom> secondHalf = axioms.subList(middle, axioms.size());

    Set<OWLAxiom> firstHalfOntology = Sets.newHashSet(firstHalf);
    Set<OWLAxiom> secondHalfOntology = Sets.newHashSet(secondHalf);

    if (checker.hasProperty(firstHalfOntology)) {
      return firstHalfOntology;
    }

    if (checker.hasProperty(secondHalfOntology)) {
      return secondHalfOntology;
    }

    firstHalfOntology.addAll(secondHalf.subList(0, secondHalf.size() / 2));
    if (checker.hasProperty(firstHalfOntology)) {
      return firstHalfOntology;
    }

    secondHalfOntology.addAll(firstHalf.subList(0, firstHalf.size() / 2));
    if (checker.hasProperty(secondHalfOntology)) {
      return secondHalfOntology;
    }

    firstHalfOntology.removeAll(secondHalf.subList(0, secondHalf.size() / 2));
    firstHalfOntology.addAll(secondHalf.subList(secondHalf.size() / 2, secondHalf.size()));
    if (checker.hasProperty(firstHalfOntology)) {
      return firstHalfOntology;
    }

    secondHalfOntology.removeAll(firstHalf.subList(0, firstHalf.size() / 2));
    secondHalfOntology.addAll(firstHalf.subList(firstHalf.size() / 2, firstHalf.size()));
    if (checker.hasProperty(secondHalfOntology)) {
      return secondHalfOntology;
    }

    return ontology;
  }
}
