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
package br.usp.ime.owlchange;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import com.google.common.collect.Sets;

public class GeneralisedPackageEntailmentChecker
        implements OntologyPropertyChecker {

    private static final int DEFAULT_TIMEOUT = 50000;
    protected OWLOntology ontology;
    protected OWLReasoner reasoner;
    private Set<OWLAxiom> mustEntail;
    private Set<OWLAxiom> mustNotEntail;

    protected static OWLOntologyManager manager = OWLManager
            .createOWLOntologyManager();
    private long calls = 0;

    public GeneralisedPackageEntailmentChecker(
            OWLReasonerFactory reasonerFactory, Set<OWLAxiom> mustNotEntail,
            Set<OWLAxiom> mustEntail) throws OWLOntologyCreationException {
        this(reasonerFactory, DEFAULT_TIMEOUT, mustNotEntail, mustEntail);

    }

    public GeneralisedPackageEntailmentChecker(
            OWLReasonerFactory reasonerFactory, int timeout,
            Set<OWLAxiom> mustNotEntail, Set<OWLAxiom> mustEntail)
            throws OWLOntologyCreationException {
        this(reasonerFactory, new SimpleConfiguration(timeout), mustNotEntail,
                mustEntail);
    }

    public GeneralisedPackageEntailmentChecker(
            OWLReasonerFactory reasonerFactory,
            OWLReasonerConfiguration reasonerConfiguration,
            Set<OWLAxiom> mustNotEntail, Set<OWLAxiom> mustEntail)
            throws OWLOntologyCreationException {
        this.ontology = manager.createOntology();
        this.reasoner = reasonerFactory.createReasoner(ontology,
                reasonerConfiguration);
        this.mustNotEntail = mustNotEntail;
        this.mustEntail = mustEntail;
    }

    public Set<OWLAxiom> getAxioms() {
        return ontology.getAxioms();
    }

    @Override
    public void setAxioms(Set<OWLAxiom> axioms) {
        manager.removeAxioms(ontology,
                Sets.difference(ontology.getAxioms(), axioms));
        manager.addAxioms(ontology,
                Sets.difference(axioms, ontology.getAxioms()));
    }

    @Override
    public boolean hasProperty(Set<OWLAxiom> axioms)
            throws OWLRuntimeException {
        setAxioms(axioms);
        reasoner.flush();
        boolean ok = true;
        calls += 1;

        if (this.mustNotEntail != null) {
            ok = !reasoner.isEntailed(mustNotEntail);
        }
        if (this.mustEntail != null) {
            ok &= reasoner.isEntailed(mustEntail);
        }
        return ok;
    }

    public long getCalls() {
        return calls;
    }
}
