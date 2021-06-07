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
package br.usp.ime.owlchange.maxnon.single.blackbox;

import br.usp.ime.owlchange.OntologyPropertyChecker;
import br.usp.ime.owlchange.maxnon.single.MaxNonBuilder;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.MaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.MaxNonShrinker;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class BlackBoxMaxNonBuilder implements MaxNonBuilder {

  protected final MaxNonShrinker maxNonShrinker;
  protected final MaxNonEnlarger maxNonEnlarger;

  public BlackBoxMaxNonBuilder(MaxNonShrinker maxNonShrinker,
      MaxNonEnlarger maxNonEnlarger) {
    this.maxNonShrinker = maxNonShrinker;
    this.maxNonEnlarger = maxNonEnlarger;
  }

  @Override
  public Optional<Set<OWLAxiom>> maxNon(Set<OWLAxiom> ontology, OntologyPropertyChecker checker,
      Set<OWLAxiom> lowerBound) {
    Optional<Set<OWLAxiom>> result = maxNonShrinker.shrink(ontology, checker, lowerBound);
    return result.isPresent() ? maxNonEnlarger.enlarge(result.get(), ontology, checker)
        : Optional.empty();
  }
}
