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
package br.usp.ime.owlchange.hst;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

public abstract class HittingSetCalculator<ItemType> {

    protected HSDeque<ImmutableSet<ItemType>> deque;
    protected Set<Set<ItemType>> nodes;
    protected Set<ImmutableSet<ItemType>> closedPaths;

    public HittingSetCalculator(HSDeque<ImmutableSet<ItemType>> deque) {
        this.deque = deque;
        this.nodes = new HashSet<>();
        this.closedPaths = new HashSet<>();
    }

    public RepairResult<ItemType> hittingSet() {
        if (deque.isEmpty()) {
            deque.addNew(ImmutableSet.copyOf((Collections.emptySet())));
        }
        hittingSetStep();
        return new RepairResult<>(nodes, closedPaths);
    }

    protected final void hittingSetStep() {

        while (!deque.isEmpty() && !earlyReturn()) {

            boolean newNode = false;

            ImmutableSet<ItemType> hittingPath = deque.removeNext();

            Optional<Set<ItemType>> optNode = this.reusable(hittingPath);

            // Requires a new node
            if (!optNode.isPresent()) {
                newNode = true;
                optNode = this.getNode(hittingPath);
            }
            if (optNode.isPresent()) {
                if (newNode) {
                    this.nodes.add(optNode.get());
                }
                successors(hittingPath, optNode.get())
                        .filter(this::shouldNotTerminate)
                        .forEach(deque::addNew);
            } else {
                this.close(hittingPath);
            }
        }
    }

    protected abstract Optional<Set<ItemType>> reusable(
            ImmutableSet<ItemType> hittingPath);

    /*
     * Returns whether this path should not be terminated (by saturation, early
     * termination or score). This "default" implementation only considers
     * saturation and early path termination, and must be overriden if other
     * aspects should be taken into account.
     */
    protected boolean shouldNotTerminate(ImmutableSet<ItemType> hittingPath) {
        return closedPaths.parallelStream().noneMatch(hittingPath::containsAll);
    }

    /* Returns a node for this hitting path and entailments */
    protected abstract Optional<Set<ItemType>> getNode(
            ImmutableSet<ItemType> hittingPath);

    /* Closes the hitting path (keeps only the minimal closed paths) */
    protected void close(ImmutableSet<ItemType> hittingPath) {
        /*
         * If no currently closed is a subset of the path under test than we
         * want to add it
         */
        if (closedPaths.stream().noneMatch(hittingPath::containsAll)) {
            /* Since we are adding a new path, we remove potential supersets */
            closedPaths.removeIf(
                    closedPath -> closedPath.containsAll(hittingPath));
            closedPaths.add(ImmutableSet.copyOf(hittingPath));
        }
    }

    /* Generate the successors of the hitting path using the node */
    protected abstract Stream<ImmutableSet<ItemType>> successors(
            ImmutableSet<ItemType> hittingPath, Set<ItemType> node);

    /* Determines a condition for early return */
    protected boolean earlyReturn() {
        return false;
    }

    public static class RepairResult<ItemType> {

        private Set<Set<ItemType>> nodes;
        private Set<ImmutableSet<ItemType>> paths;

        public RepairResult(Set<Set<ItemType>> nodes,
                Set<ImmutableSet<ItemType>> paths) {
            this.nodes = nodes;
            this.paths = paths;
        }

        public Set<Set<ItemType>> getNodes() {
            return nodes;
        }

        public Set<ImmutableSet<ItemType>> getPaths() {
            return paths;
        }
    }

}
