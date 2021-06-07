package br.usp.ime.pseudocontraction.protegeplugin;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.NoOpReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import com.google.common.collect.Sets;

import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.MaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.MaxNonShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.MinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.MinImpShrinker;
import br.usp.ime.owlchange.util.SyntacticConnectivitySorting;
import br.usp.ime.pseudocontraction.protegeplugin.container.ConsequenceOperatorPanel;
import br.usp.ime.pseudocontraction.protegeplugin.container.ConstructionPanel;
import br.usp.ime.pseudocontraction.protegeplugin.container.ConstructionPanel.Construction;
import br.usp.ime.pseudocontraction.protegeplugin.container.RunPanel;
import br.usp.ime.pseudocontraction.protegeplugin.container.SentencePanel;
import br.usp.ime.pseudocontraction.protegeplugin.container.StrategyPanel;
import br.usp.ime.pseudocontraction.protegeplugin.container.StrategyPanel.SortingOption;
import br.usp.ime.pseudocontraction.protegeplugin.window.IncisionFunctionDialogue;
import br.usp.ime.pseudocontraction.protegeplugin.window.SelectionFunctionDialogue;

public class PseudoContractionMainPanel extends JPanel {

    private static final long serialVersionUID = 5247802836967897289L;

    private final OWLModelManager manager;

    private final RunPanel runPanel;

    private final SentencePanel sentencePanel;

    private final StrategyPanel strategyPanel;

    private final ConstructionPanel constructionPanel;

    private final ConsequenceOperatorPanel cnStarPanel;

    public PseudoContractionMainPanel(OWLEditorKit editorKit,
            OWLModelManager manager) {
        this.manager = manager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        cnStarPanel = new ConsequenceOperatorPanel();
        JScrollPane cnStarPanelScrollPane = new JScrollPane(cnStarPanel);
        JPanel cnStarPanelContainer = new JPanel();
        cnStarPanelContainer.add(cnStarPanelScrollPane);
        cnStarPanelContainer.setMaximumSize(new Dimension(32767, 200));
        cnStarPanelContainer.setBorder(BorderFactory.createTitledBorder("Cn*"));
        cnStarPanelContainer.setLayout(
                new BoxLayout(cnStarPanelContainer, BoxLayout.X_AXIS));
        add(cnStarPanelContainer);

        constructionPanel = new ConstructionPanel();
        add(constructionPanel);

        strategyPanel = new StrategyPanel();
        JScrollPane strategyPanelScrollPane = new JScrollPane(strategyPanel);
        JPanel strategyPanelContainer = new JPanel();
        strategyPanelContainer.setMaximumSize(new Dimension(32767, 90));
        strategyPanelContainer.setLayout(
                new BoxLayout(strategyPanelContainer, BoxLayout.X_AXIS));
        strategyPanelContainer.add(strategyPanelScrollPane);
        add(strategyPanelContainer);
        strategyPanelContainer
                .setBorder(BorderFactory.createTitledBorder("Strategies"));

        sentencePanel = new SentencePanel(editorKit, manager);
        add(sentencePanel);

        runPanel = new RunPanel();
        add(runPanel);

        Component verticalGlue = Box.createVerticalGlue();
        add(verticalGlue);

        constructionPanel.addListener(selectedConstruction -> {
            strategyPanel.showOnly(selectedConstruction);
        });

        init();
    }

    protected void init() {
        runPanel.getRun().addActionListener(e -> {
            OWLAxiom sentenceToContract = sentencePanel.getSentence();
            if (sentenceToContract == null) {
                JOptionPane.showMessageDialog(this,
                        "Sentence is empty or invalid.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            PseudoContractionWorker worker = new PseudoContractionWorker(
                    manager);
            OWLOntology ontology = manager.getActiveOntology();
            if (ontology == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not get current ontology.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Set<OWLAxiom> ontologySentences = ontology.getAxioms();

            OWLOntologyManager ontoManager = OWLManager
                    .createOWLOntologyManager();
            OWLOntology o;
            try {
                o = ontoManager.createOntology(ontologySentences);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Could not create a temporary ontology.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            OWLReasonerFactory reasonerFactory = manager.getOWLReasonerManager()
                    .getCurrentReasonerFactory().getReasonerFactory();
            if (reasonerFactory instanceof NoOpReasonerFactory) {
                JOptionPane.showMessageDialog(this,
                        "Select a reasoner on the menu.", "No reasoner",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            OWLReasoner reasoner = reasonerFactory.createReasoner(o);
            if (!reasoner.isConsistent()) {
                JOptionPane.showMessageDialog(this,
                        "The ontology is inconsistent.",
                        "Inconsistent ontology", JOptionPane.ERROR_MESSAGE);
                return;
            }
            InferredOntologyGenerator gen = cnStarPanel.getCnStar(reasoner);
            gen.fillOntology(ontoManager.getOWLDataFactory(), o);

            Set<OWLAxiom> consequences = o.getAxioms();

            Construction construction = constructionPanel.getConstruction();
            if (construction == null) {
                JOptionPane.showMessageDialog(this,
                        "No construction has been selected.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            SortingOption sort;

            Set<OWLAxiom> resultingOntology = null;
            strategyPanel.setSentence(sentenceToContract);
            switch (construction) {
            case KERNEL:
                sort = strategyPanel.getKernelSortingOption();
                if (sort != SortingOption.NONE) {
                    consequences = new SyntacticConnectivitySorting(
                            sentenceToContract.getSignature(),
                            SyntacticConnectivitySorting.Order
                                    .valueOf(sort.name())).sort(consequences);
                }
                MinImpEnlarger kerEnlarger = strategyPanel.getKernelEnlarger();
                MinImpShrinker kerShrinker = strategyPanel.getKernelShrinker();
                Set<Set<OWLAxiom>> kernelSet = worker.computeKernelSet(
                        consequences, sentenceToContract, kerEnlarger,
                        kerShrinker);
                if (kernelSet.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "The ontology probably does not entail the chosen sentence.",
                            "Sentence not entailed",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (kernelSet.size() == 1
                        && kernelSet.stream().findFirst().get().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "The sentence to be contracted is probably a tautology and cannot be contracted.",
                            "Tautology", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    IncisionFunctionDialogue inc = new IncisionFunctionDialogue(
                            SwingUtilities.getWindowAncestor(this), kernelSet);
                    Set<OWLAxiom> incisionResult = inc.getIncisionResult();
                    if (incisionResult != null)
                        resultingOntology = Sets.difference(consequences,
                                incisionResult);
                }
                break;
            case PARTIAL_MEET:
                sort = strategyPanel.getRemainderSortingOption();
                if (sort != SortingOption.NONE) {
                    consequences = new SyntacticConnectivitySorting(
                            sentenceToContract.getSignature(),
                            SyntacticConnectivitySorting.Order
                                    .valueOf(sort.name())).sort(consequences);
                }
                MaxNonShrinker remShrinker = strategyPanel
                        .getRemainderShrinker();
                MaxNonEnlarger remEnlarger = strategyPanel
                        .getRemainderEnlarger();
                Set<Set<OWLAxiom>> remainderSet = worker.computeRemainderSet(
                        consequences, sentenceToContract, remShrinker,
                        remEnlarger);
                if (remainderSet.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "The sentence to be contracted is probably a tautology and cannot be contracted.",
                            "Tautology", JOptionPane.INFORMATION_MESSAGE);
                } else if (remainderSet.size() == 1 && remainderSet.stream()
                        .findFirst().get().size() == consequences.size()) {
                    JOptionPane.showMessageDialog(this,
                            "The ontology probably does not entail the chosen sentence.",
                            "Sentence not entailed",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    List<Set<OWLAxiom>> remainderSetAsList = remainderSet
                            .stream().collect(Collectors.toList());
                    SelectionFunctionDialogue sel = new SelectionFunctionDialogue(
                            SwingUtilities.getWindowAncestor(this),
                            remainderSetAsList);
                    Set<Integer> selectionResult = sel.getSelectionResult();
                    if (selectionResult != null && !selectionResult.isEmpty()) {
                        Iterator<Integer> it = selectionResult.iterator();
                        resultingOntology = new HashSet<>(
                                remainderSetAsList.get(it.next()));
                        while (it.hasNext())
                            resultingOntology.retainAll(
                                    remainderSetAsList.get(it.next()));
                    }
                }
                break;
            default:
                break;
            }
            if (resultingOntology != null) {
                List<OWLOntologyChange> changes = new LinkedList<>();
                Sets.difference(resultingOntology, ontologySentences)
                        .forEach(s -> changes.add(new AddAxiom(ontology, s)));
                Sets.difference(ontologySentences, resultingOntology).forEach(
                        s -> changes.add(new RemoveAxiom(ontology, s)));
                if (!changes.isEmpty())
                    manager.applyChanges(changes);
                JOptionPane.showMessageDialog(this,
                        "The pseudo-contraction operation has been completed successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

}
