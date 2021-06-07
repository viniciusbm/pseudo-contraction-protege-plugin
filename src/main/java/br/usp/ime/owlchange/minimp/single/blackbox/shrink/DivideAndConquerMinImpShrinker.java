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
package br.usp.ime.owlchange.minimp.single.blackbox.shrink;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class DivideAndConquerMinImpShrinker implements MinImpShrinker {

  @Override
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    return divideAndConquer(Sets.newHashSet(ontology), checker);
  }

  private Optional<Set<OWLAxiom>> divideAndConquer(Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    List<OWLAxiom> axioms = Lists.newArrayList(ontology);

    if (axioms.size() == 0) {
      return Optional.of(ontology);
    }

    int middle = axioms.size() / 2;
    List<OWLAxiom> firstHalf = axioms.subList(0, middle);
    List<OWLAxiom> secondHalf = axioms.subList(middle, axioms.size());

    Set<OWLAxiom> firstHalfOntology = Sets.newHashSet(firstHalf);
    if (checker.hasProperty(firstHalfOntology)) {
      return divideAndConquer(firstHalfOntology, checker);
    }

    Set<OWLAxiom> secondHalfOntology = Sets.newHashSet(secondHalf);
    if (checker.hasProperty(secondHalfOntology)) {
      return divideAndConquer(secondHalfOntology, checker);
    }

    firstHalfOntology.addAll(secondHalf.subList(0, secondHalf.size() / 2));
    if (checker.hasProperty(firstHalfOntology)) {
      return divideAndConquer(firstHalfOntology, checker);
    }

    secondHalfOntology.addAll(firstHalf.subList(0, firstHalf.size() / 2));
    if (checker.hasProperty(secondHalfOntology)) {
      return divideAndConquer(secondHalfOntology, checker);
    }

    if (secondHalf.size() > 1) {
      firstHalfOntology.removeAll(secondHalf.subList(0, secondHalf.size() / 2));
      firstHalfOntology.addAll(secondHalf.subList(secondHalf.size() / 2, secondHalf.size()));
      if (checker.hasProperty(firstHalfOntology)) {
        return divideAndConquer(firstHalfOntology, checker);
      }
    }

    if (firstHalf.size() > 1) {
      secondHalfOntology.removeAll(firstHalf.subList(0, firstHalf.size() / 2));
      secondHalfOntology.addAll(firstHalf.subList(firstHalf.size() / 2, firstHalf.size()));
      if (checker.hasProperty(secondHalfOntology)) {
        return divideAndConquer(secondHalfOntology, checker);
      }
    }

    ClassicalMinImpShrinker classicalMinImpShrinker = new ClassicalMinImpShrinker();

    return classicalMinImpShrinker.shrink(ontology, checker);
  }
}
