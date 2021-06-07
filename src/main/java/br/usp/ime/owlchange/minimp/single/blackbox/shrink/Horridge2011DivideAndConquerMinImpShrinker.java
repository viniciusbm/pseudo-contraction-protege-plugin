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

package br.usp.ime.owlchange.minimp.single.blackbox.shrink;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class Horridge2011DivideAndConquerMinImpShrinker implements MinImpShrinker {

  private Set<OWLAxiom> divideAndConquer(Set<OWLAxiom> support, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    if (ontology.size() == 1) {
      return ontology;
    }

    List<OWLAxiom> axioms = Lists.newArrayList(ontology);

    int middle = axioms.size() / 2;
    List<OWLAxiom> firstHalf = axioms.subList(0, middle);
    List<OWLAxiom> secondHalf = axioms.subList(middle, axioms.size());

    Set<OWLAxiom> firstHalfOntology = Sets.newHashSet(firstHalf);

    if (checker.hasProperty(Sets.union(support, firstHalfOntology))) {
      return divideAndConquer(support, firstHalfOntology, checker);
    }

    Set<OWLAxiom> secondHalfOntology = Sets.newHashSet(secondHalf);
    if (checker.hasProperty(Sets.union(support, secondHalfOntology))) {
      return divideAndConquer(support, secondHalfOntology, checker);
    }

    Set<OWLAxiom> firstRecombined = divideAndConquer(Sets.union(support, secondHalfOntology),
        firstHalfOntology, checker);
    Set<OWLAxiom> secondRecombined = divideAndConquer(Sets.union(support, firstRecombined),
        secondHalfOntology, checker);

    return Sets.union(firstRecombined, secondRecombined);
  }

  @Override
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    if (!checker.hasProperty(ontology)) {
      return Optional.empty();
    }

    Set<OWLAxiom> result = divideAndConquer(Collections.emptySet(), ontology, checker);
    return Optional.of(result);
  }
}
