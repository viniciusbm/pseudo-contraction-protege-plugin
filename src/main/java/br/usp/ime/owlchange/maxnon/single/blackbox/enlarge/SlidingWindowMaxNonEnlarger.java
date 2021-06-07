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
package br.usp.ime.owlchange.maxnon.single.blackbox.enlarge;

import static java.lang.Math.min;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class SlidingWindowMaxNonEnlarger implements MaxNonEnlarger {

  private int windowSize;

  public SlidingWindowMaxNonEnlarger(int windowSize) {
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
  public Optional<Set<OWLAxiom>> enlarge(@Nullable Set<OWLAxiom> base,
      @Nullable Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    if (base == null || ontology == null) {
      return Optional.empty();
    }

    int start = 0;

    Set<OWLAxiom> copy = Sets.newHashSet(base);
    List<OWLAxiom> axioms = ImmutableList.copyOf(Sets.difference(ontology, base));

    while (start < axioms.size()) {
      List<OWLAxiom> windowAxioms = axioms.subList(start, min(axioms.size(), start + windowSize));
      copy.addAll(windowAxioms);
      if (!checker.hasProperty(copy)) {
        start += windowAxioms.size();
      } else {
        int offset = windowAxioms.size() - 1;
        do {
          copy.remove(axioms.get(start + offset));
          offset--;
        } while (checker.hasProperty(copy));
        start += offset + 2;
      }
    }

    return Optional.ofNullable(!checker.hasProperty(copy) ? copy : null);
  }
}
