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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nullable;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

public class DivideAndConquerMaxNonEnlarger implements MaxNonEnlarger {

  @Override
  public Optional<Set<OWLAxiom>> enlarge(@Nullable Set<OWLAxiom> base,
      @Nullable Set<OWLAxiom> ontology, OntologyPropertyChecker checker) {

    if (base == null || ontology == null) {
      return Optional.empty();
    }

    Set<OWLAxiom> start = Sets.newHashSet(base);

    if (divideAndConquer(start, ontology, checker)) {
      return Optional.of(start);
    } else {
      return classicalEnlarge(start, ontology, checker);
    }
  }

  private boolean divideAndConquer(Set<OWLAxiom> base, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {

    Queue<Range<Integer>> rangeQueue = new LinkedList<>();
    List<OWLAxiom> axioms = Lists.newArrayList(ontology);
    Range<Integer> full = Range.closed(0, axioms.size() - 1);

    rangeQueue.add(full);

    while (!rangeQueue.isEmpty()) {

      Range<Integer> range = rangeQueue.remove();

      int start = range.lowerEndpoint();
      int end = range.upperEndpoint();

      Set<OWLAxiom> diff =
          Sets.difference(Sets.newHashSet(axioms.subList(start, end + 1)), base).immutableCopy();
      base.addAll(diff);
      if (checker.hasProperty(base)) {
        base.removeAll(diff);
        if (end > start) {
          int middle = start + (end - start) / 2;
          rangeQueue.add(Range.closed(start, middle));
          rangeQueue.add(Range.closed(middle + 1, end));
        }
      }
    }
    return true;
  }

  private Optional<Set<OWLAxiom>> classicalEnlarge(Set<OWLAxiom> base, Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker) {
    ClassicalMaxNonEnlarger classicalMaxNonEnlarger = new ClassicalMaxNonEnlarger();
    return classicalMaxNonEnlarger.enlarge(base, ontology, checker);
  }
}
