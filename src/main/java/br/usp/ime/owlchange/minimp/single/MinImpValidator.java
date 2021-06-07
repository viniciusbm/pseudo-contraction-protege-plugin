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
package br.usp.ime.owlchange.minimp.single;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class MinImpValidator {

  public static boolean isSubset(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology) {

    return ontology.containsAll(candidate);
  }

  public static boolean isImplicant(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    if (checker.hasProperty(ontology)) {
      return checker.hasProperty(candidate);
    } else {
      return !checker.hasProperty(candidate);
    }
  }

  public static boolean isMinimal(Set<OWLAxiom> candidate, OntologyPropertyChecker checker) {

    Set<OWLAxiom> candidateCopy = Sets.newHashSet(candidate);
    List<OWLAxiom> collect = Lists.newArrayList(candidate);

    for (OWLAxiom axiom : collect) {
      candidateCopy.remove(axiom);
      if (checker.hasProperty(candidateCopy)) {
        candidateCopy.add(axiom);
        return false;
      }
      candidateCopy.add(axiom);
    }

    return true;
  }

  public static boolean isMinImp(Set<OWLAxiom> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    return isSubset(candidate, ontology) && isImplicant(candidate, ontology, checker) && isMinimal(
        candidate, checker);
  }


}
