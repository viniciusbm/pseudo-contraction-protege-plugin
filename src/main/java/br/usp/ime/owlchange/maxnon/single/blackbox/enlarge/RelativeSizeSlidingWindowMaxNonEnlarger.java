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

import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.semanticweb.owlapi.model.OWLAxiom;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class RelativeSizeSlidingWindowMaxNonEnlarger implements MaxNonEnlarger {

  private float relativeWindowSize;

  public RelativeSizeSlidingWindowMaxNonEnlarger(float relativeWindowSize) {
    setRelativeWindowSize(relativeWindowSize);
  }

  public float getRelativeWindowSize() {
    return relativeWindowSize;
  }

  public void setRelativeWindowSize(float relativeWindowSize) {
    this.relativeWindowSize = relativeWindowSize;
    if (this.relativeWindowSize > 1)
      this.relativeWindowSize = 1;
    else if (this.relativeWindowSize <= 0)
      this.relativeWindowSize = Float.MIN_VALUE;
  }

  @Override
  public Optional<Set<OWLAxiom>> enlarge(@Nullable Set<OWLAxiom> base,
      @Nullable Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    if (base == null || ontology == null) {
      return Optional.empty();
    }

    MaxNonEnlarger enlarger;
    int windowSize = (int) Math.ceil(relativeWindowSize * ontology.size());
    if (windowSize == 1) {
      enlarger = new ClassicalMaxNonEnlarger();
    } else {
      enlarger = new SlidingWindowMaxNonEnlarger(windowSize);
    }
    return enlarger.enlarge(base, ontology, checker);
  }
}
