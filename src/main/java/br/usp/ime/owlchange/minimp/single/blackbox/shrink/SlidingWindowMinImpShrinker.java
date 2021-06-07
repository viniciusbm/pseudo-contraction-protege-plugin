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

import static java.lang.Math.min;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class SlidingWindowMinImpShrinker implements MinImpShrinker {

  private int windowSize;

  public SlidingWindowMinImpShrinker(int windowSize) {
    setWindowSize(windowSize);
  }

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
    if (windowSize < 1) {
      this.windowSize = 1;
    }
  }

  @Override
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    Set<OWLAxiom> copyOntology = Sets.newHashSet(ontology);

    // Fast window prunning
    prune(copyOntology, checker, this.windowSize);
    // Slow prunning (classical)
    if (this.windowSize > 1) {
      prune(copyOntology, checker, 1);
    }

    return Optional.of(copyOntology);
  }

  private void prune(Set<OWLAxiom> ontology, OntologyPropertyChecker checker, int windowSize) {
    int start = 0;

    List<OWLAxiom> axioms = Lists.newArrayList(ontology);

    while (start < axioms.size()) {
      List<OWLAxiom> windowAxioms = axioms.subList(start, min(axioms.size(), start + windowSize));
      ontology.removeAll(windowAxioms);
      if (!checker.hasProperty(ontology)) {
        ontology.addAll(windowAxioms);
      }
      start += windowSize;
    }
  }
}
