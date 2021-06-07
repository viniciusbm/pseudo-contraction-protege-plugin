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
package br.usp.ime.owlchange.maxnon.single.blackbox.shrink;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public interface MaxNonShrinker {

  /**
   * Returns a subset of the ontology that is a superset of the lower bound that does not have the
   * the property defined by the checker.
   *
   * @param ontology a set of axioms.
   * @param checker a property checker.
   * @param lowerBound a subset of the ontology.
   * @return a subset of the ontology that is a superset of the lower bound that does not have the *
   * the property defined by the checker if it exists, Optional.empty() otherwise.
   */
  Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker,
      Set<OWLAxiom> lowerBound);
}
