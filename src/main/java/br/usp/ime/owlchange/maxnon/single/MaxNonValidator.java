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
package br.usp.ime.owlchange.maxnon.single;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class MaxNonValidator {

  public static boolean isSubset(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology) {

    return ontology.containsAll(candidate);
  }

  public static boolean nonImplying(Set<OWLAxiom> candidate, OntologyPropertyChecker checker) {

    return !checker.hasProperty(candidate);
  }

  public static boolean isMaximal(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    HashSet<OWLAxiom> candidateCopy = Sets.newHashSet(candidate);
    Set<OWLAxiom> complement = Sets.difference(ontology, candidateCopy);

    return complement.isEmpty() || complement.stream()
        .allMatch(axiom -> additionImplies(candidateCopy, axiom, checker));

  }

  private static boolean additionImplies(Set<OWLAxiom> candidate, OWLAxiom axiom,
      OntologyPropertyChecker checker) {

    boolean result;

    candidate.add(axiom);
    result = checker.hasProperty(candidate);
    candidate.remove(axiom);
    return result;
  }

  public static boolean isMaxNon(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    return isSubset(candidate, ontology) && nonImplying(candidate, checker) && isMaximal(candidate,
        ontology, checker);
  }
}
