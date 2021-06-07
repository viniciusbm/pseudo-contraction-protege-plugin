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

import static java.lang.Math.min;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class SlidingWindowMinImpEnlarger implements MinImpEnlarger {

  private int windowSize;

  public SlidingWindowMinImpEnlarger(int windowSize) {
    setWindowSize(windowSize);
  }

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
    if (this.windowSize < 1) {
      this.windowSize = 1;
    }
  }

  @Override
  public Optional<Set<OWLAxiom>> enlarge(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    Set<OWLAxiom> copyOntology = Sets.newHashSet();

    int start = 0;

    List<OWLAxiom> axioms = Lists.newLinkedList(ontology);

    while (start < axioms.size()) {
      List<OWLAxiom> windowAxioms = axioms.subList(start, min(axioms.size(), start + windowSize));
      copyOntology.addAll(windowAxioms);
      if (checker.hasProperty(copyOntology)) {
        return Optional.of(copyOntology);
      }
      start += windowSize;
    }
    return Optional.ofNullable(checker.hasProperty(copyOntology) ? copyOntology : null);
  }
}
