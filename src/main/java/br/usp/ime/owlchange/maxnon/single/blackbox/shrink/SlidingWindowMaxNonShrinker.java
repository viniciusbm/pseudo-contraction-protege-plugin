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

import static java.lang.Math.min;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class SlidingWindowMaxNonShrinker implements MaxNonShrinker {

  private int windowSize;

  public SlidingWindowMaxNonShrinker(int windowSize) {
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
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker,
      Set<OWLAxiom> lowerBound) {
    Set<OWLAxiom> result = Sets.newHashSet(lowerBound);

    if (checker.hasProperty(result)) {
      return Optional.empty();
    }

    result.addAll(ontology);

    prune(result, checker, getWindowSize(), lowerBound);

    return Optional.of(result);
  }

  private void prune(Set<OWLAxiom> ontology, OntologyPropertyChecker checker, int windowSize,
      Set<OWLAxiom> lowerBound) {

    ImmutableList<OWLAxiom> axioms = ImmutableList.copyOf(Sets.difference(ontology, lowerBound));

    for (int start = 0; start < axioms.size(); start += windowSize) {
      List<OWLAxiom> windowAxioms = axioms.subList(start, min(axioms.size(), start + windowSize));
      ontology.removeAll(windowAxioms);
      if (!checker.hasProperty(ontology)) {
        return;
      }
    }
    ontology.retainAll(lowerBound);
  }
}
