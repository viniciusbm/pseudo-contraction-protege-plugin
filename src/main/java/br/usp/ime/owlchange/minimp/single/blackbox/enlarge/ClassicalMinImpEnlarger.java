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

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class ClassicalMinImpEnlarger implements MinImpEnlarger {

  @Override
  public Optional<Set<OWLAxiom>> enlarge(Set<OWLAxiom> ontology,
      OntologyPropertyChecker reasonerWrapper) {

    Set<OWLAxiom> enlarged = Sets.newHashSet();

    Iterator<OWLAxiom> axiomIterator = ontology.iterator();

    while (axiomIterator.hasNext() && !reasonerWrapper.hasProperty(enlarged)) {
      enlarged.add(axiomIterator.next());
    }

    return Optional.ofNullable(reasonerWrapper.hasProperty(enlarged) ? (enlarged) : null);
  }
}
