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
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ClassicalMaxNonShrinker implements MaxNonShrinker {

  @Override
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker,
      Set<OWLAxiom> lowerBound) {

    Set<OWLAxiom> result = Sets.newHashSet(lowerBound);

    if (checker.hasProperty(result)) {
      return Optional.empty();
    }

    result.addAll(ontology);
    Iterator<OWLAxiom> axiomIterator = Sets.difference(ontology, lowerBound).iterator();

    while (axiomIterator.hasNext() && checker.hasProperty(result)) {
      result.remove(axiomIterator.next());
    }

    return Optional.ofNullable(checker.hasProperty(result) ? null : result);
  }
}
