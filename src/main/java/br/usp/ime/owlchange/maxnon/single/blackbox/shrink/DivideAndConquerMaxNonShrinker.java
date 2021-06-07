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
package br.usp.ime.owlchange.maxnon.single.blackbox.shrink;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import br.usp.ime.owlchange.OntologyPropertyChecker;

// TODO: Replace hardcoded values with constants
public class DivideAndConquerMaxNonShrinker implements MaxNonShrinker {

  @Override
  public Optional<Set<OWLAxiom>> shrink(Set<OWLAxiom> ontology, OntologyPropertyChecker checker,
      Set<OWLAxiom> lowerBound) {

    Set<OWLAxiom> result = Sets.newHashSet(lowerBound);

    if (checker.hasProperty(result)) {
      return Optional.empty();
    }

    result.addAll(ontology);

    return divideAndConquer(result, checker, lowerBound);
  }

  private Optional<Set<OWLAxiom>> divideAndConquer(Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker, Set<OWLAxiom> lowerBound) {

    Queue<Range<Integer>> rangeQueue = new LinkedList<>();
    List<OWLAxiom> axioms = Lists.newArrayList(Sets.difference(ontology, lowerBound));
    Range<Integer> full = Range.closed(0, axioms.size() - 1);

    int start = full.lowerEndpoint();
    int end = full.upperEndpoint();
    int middle = start + (end - start) / 2;

    rangeQueue.add(Range.closed(start, middle));
    rangeQueue.add(Range.closed(middle + 1, end));
    Set<OWLAxiom> result = Sets.newHashSet(lowerBound);

    while (!rangeQueue.isEmpty()) {

      Range<Integer> range = rangeQueue.remove();

      start = range.lowerEndpoint();
      end = range.upperEndpoint();

      result.addAll(axioms.subList(start, end + 1));
      if (!checker.hasProperty(Sets.union(lowerBound, result))) {
        return Optional.of(result);
      }
      result.retainAll(lowerBound);

      if (end - start > axioms.size() / 16) {
        middle = start + (end - start) / 2;
        rangeQueue.add(Range.closed(start, middle));
        rangeQueue.add(Range.closed(middle + 1, end));
      }
    }
    return classicalShrink(result, checker, lowerBound);
  }

  private Optional<Set<OWLAxiom>> classicalShrink(Set<OWLAxiom> ontology,
      OntologyPropertyChecker checker, Set<OWLAxiom> lowerBound) {
    ClassicalMaxNonShrinker classicalMaxNonShrinker = new ClassicalMaxNonShrinker();
    return classicalMaxNonShrinker.shrink(ontology, checker, lowerBound);
  }
}
