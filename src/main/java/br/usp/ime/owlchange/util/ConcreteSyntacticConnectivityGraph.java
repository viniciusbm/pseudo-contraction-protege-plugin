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
package br.usp.ime.owlchange.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

/* Bipartite graph linking terms (OWLEntity) to axioms (OWLAxioms) */
public class ConcreteSyntacticConnectivityGraph implements TermToAxiomsMapper {

    private final MutableGraph<OWLObject> graph;

    public ConcreteSyntacticConnectivityGraph(Set<OWLAxiom> ontology) {
        graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        ontology.forEach(
                ax -> ax.getSignature().forEach(en -> graph.putEdge(en, ax)));
    }

    @Override
    public Set<OWLAxiom> termToAxioms(OWLEntity term) {
        return graph.adjacentNodes(term).stream().map(ax -> (OWLAxiom) ax)
                .collect(Collectors.toSet());
    }

    public MutableGraph<OWLObject> getGraph() {
        return graph;
    }
}
