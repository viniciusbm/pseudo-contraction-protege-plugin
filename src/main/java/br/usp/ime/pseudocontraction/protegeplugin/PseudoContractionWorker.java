package br.usp.ime.pseudocontraction.protegeplugin;

import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import br.usp.ime.owlchange.GeneralisedPackageEntailmentChecker;
import br.usp.ime.owlchange.OntologyPropertyChecker;
import br.usp.ime.owlchange.hst.HSQueue;
import br.usp.ime.owlchange.hst.HittingSetCalculator.RepairResult;
import br.usp.ime.owlchange.maxnon.full.HittingSetMaxNonsBuilder;
import br.usp.ime.owlchange.maxnon.full.MaxNonsBuilder;
import br.usp.ime.owlchange.maxnon.single.MaxNonBuilder;
import br.usp.ime.owlchange.maxnon.single.blackbox.BlackBoxMaxNonBuilder;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.MaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.MaxNonShrinker;
import br.usp.ime.owlchange.minimp.full.HittingSetMinImpsBuilder;
import br.usp.ime.owlchange.minimp.full.MinImpsBuilder;
import br.usp.ime.owlchange.minimp.single.MinImpBuilder;
import br.usp.ime.owlchange.minimp.single.blackbox.BlackBoxMinImpBuilder;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.MinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.MinImpShrinker;

public class PseudoContractionWorker {

    private final OWLModelManager manager;

    public PseudoContractionWorker(OWLModelManager manager) {
        this.manager = manager;
    }

    public boolean isEntailed(Set<OWLAxiom> ontologySentences,
            OWLAxiom sentence) {
        OntologyPropertyChecker checker;
        OWLReasonerFactory reasonerFactory = manager.getOWLReasonerManager()
                .getCurrentReasonerFactory().getReasonerFactory();
        try {
            checker = new GeneralisedPackageEntailmentChecker(reasonerFactory,
                    null, Collections.singleton(sentence));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return false;
        }
        return checker.hasProperty(ontologySentences);
    }

    public boolean isTautological(OWLAxiom sentence) {
        return isEntailed(Collections.emptySet(), sentence);
    }

    public Set<Set<OWLAxiom>> computeRemainderSet(
            Set<OWLAxiom> ontologySentences, OWLAxiom sentenceToContract,
            MaxNonShrinker shrinker, MaxNonEnlarger enlarger) {
        MaxNonBuilder maxNonBuilder = new BlackBoxMaxNonBuilder(shrinker,
                enlarger);
        MaxNonsBuilder maxNonsBuilder = new HittingSetMaxNonsBuilder(
                maxNonBuilder, HSQueue::new);
        OntologyPropertyChecker checker;
        OWLReasonerFactory reasonerFactory = manager.getOWLReasonerManager()
                .getCurrentReasonerFactory().getReasonerFactory();
        try {
            checker = new GeneralisedPackageEntailmentChecker(reasonerFactory,
                    null, Collections.singleton(sentenceToContract));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return null;
        }
        RepairResult<OWLAxiom> result = maxNonsBuilder
                .maxNons(ontologySentences, checker);
        return result == null ? null : result.getNodes();
    }

    public Set<Set<OWLAxiom>> computeKernelSet(Set<OWLAxiom> ontologySentences,
            OWLAxiom sentenceToContract, MinImpEnlarger enlarger,
            MinImpShrinker shrinker) {
        MinImpBuilder minImpBuilder = new BlackBoxMinImpBuilder(enlarger,
                shrinker);
        MinImpsBuilder minImpsBuilder = new HittingSetMinImpsBuilder(
                minImpBuilder, HSQueue::new);
        OWLReasonerFactory reasonerFactory = manager.getOWLReasonerManager()
                .getCurrentReasonerFactory().getReasonerFactory();
        OntologyPropertyChecker checker;
        try {
            checker = new GeneralisedPackageEntailmentChecker(reasonerFactory,
                    null, Collections.singleton(sentenceToContract));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return null;
        }
        RepairResult<OWLAxiom> result = minImpsBuilder
                .minImps(ontologySentences, checker);
        return result == null ? null : result.getNodes();
    }

}
