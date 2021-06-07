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

import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class RelativeSizeSlidingWindowMinImpEnlarger implements MinImpEnlarger {

  private float relativeWindowSize;

  public RelativeSizeSlidingWindowMinImpEnlarger(float windowSize) {
    setRelativeWindowSize(windowSize);
  }

  public float getRelativeWindowSize() {
    return relativeWindowSize;
  }

  public void setRelativeWindowSize(float relativeWindowSize) {
    this.relativeWindowSize = relativeWindowSize;
    if (this.relativeWindowSize > 1) {
      this.relativeWindowSize = 1;
    } else if (this.relativeWindowSize <= 0) {
      this.relativeWindowSize = Float.MIN_VALUE;
    }
  }

  @Override
  public Optional<Set<OWLAxiom>> enlarge(Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {
    int setSize = ontology.size();
    int windowSize = (int) Math.ceil(relativeWindowSize * setSize);
    MinImpEnlarger enlarger;
    if (windowSize == 1)
      enlarger = new ClassicalMinImpEnlarger();
    else if (windowSize == setSize)
      enlarger = new TrivialMinImpEnlarger();
    else
      enlarger = new SlidingWindowMinImpEnlarger(windowSize);
    return enlarger.enlarge(ontology, checker);
  }
}
