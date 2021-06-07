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
package br.usp.ime.owlchange.maxnon.single.blackbox.enlarge;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ClassicalMaxNonEnlarger implements MaxNonEnlarger {

  @Override
  public Optional<Set<OWLAxiom>> enlarge(@Nullable Set<OWLAxiom> base,
      @Nullable Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    if (base == null || ontology == null) {
      return Optional.empty();
    }

    Set<OWLAxiom> start = Sets.newHashSet(base);

    Sets.difference(ontology, base)
        .forEach(axiom -> addIfNotEntails(start, axiom, checker));

    return Optional.ofNullable(!checker.hasProperty(start) ? start : null);
  }

  private void addIfNotEntails(Set<OWLAxiom> ontology, OWLAxiom axiom,
      OntologyPropertyChecker checker) {
    ontology.add(axiom);
    if (checker.hasProperty(ontology)) {
      ontology.remove(axiom);
    }
  }
}
