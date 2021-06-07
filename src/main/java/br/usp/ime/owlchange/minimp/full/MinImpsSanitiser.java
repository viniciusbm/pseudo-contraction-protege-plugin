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

import br.usp.ime.owlchange.OntologyPropertyChecker;
import br.usp.ime.owlchange.minimp.single.MinImpValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLAxiom;

public class MinImpsSanitiser {

  public static boolean hasOnlyMinImps(Set<Set<OWLAxiom>> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {
    return candidate.stream()
        .allMatch(minImp -> MinImpValidator.isMinImp(minImp, ontology, checker));
  }

  public static boolean hasAllMinImps(Set<Set<OWLAxiom>> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    try {
      Set<List<OWLAxiom>> cuts = Sets.cartesianProduct(Lists.newArrayList(candidate));
      return cuts.stream()
          .noneMatch(cut -> checker.hasProperty(Sets.difference(ontology, Sets.newHashSet(cut))));
    } catch (IllegalArgumentException e) {
      Set<OWLAxiom> union = candidate.stream().flatMap(Collection::stream)
          .collect(Collectors.toSet());
      return !checker.hasProperty(Sets.difference(ontology, union));
    }
  }

  public static boolean sanitise(Set<Set<OWLAxiom>> candidate, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {
    return hasOnlyMinImps(candidate, ontology, checker) && hasAllMinImps(candidate, ontology,
        checker);
  }

}
