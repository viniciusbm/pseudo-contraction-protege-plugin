package br.usp.ime.pseudocontraction.protegeplugin.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import br.usp.ime.owlchange.util.SentenceFormatter;
import br.usp.ime.pseudocontraction.protegeplugin.container.RemainderElementPresenter;

public class SelectionFunctionDialogue extends JDialog {

    public static final boolean USE_TABS = false;

    private static final long serialVersionUID = 8368119405400626372L;

    private static final Color DEFAULT_COLOUR = new JLabel().getForeground();
    private static final Color DELETED_COLOUR = Color.RED;

    List<Set<OWLAxiom>> remainderSet;

    private Set<Integer> result = null;

    private JButton runBtn;

    private JComponent remainderSetContainer;

    private JCheckBox[] remainderCheckboxes;

    private Set<Integer> chosen;

    private Map<JCheckBox, Integer> checkBoxToRemainderNum;

    private Map<OWLAxiom, List<JLabel>> sentenceToLabels;

    private Map<OWLAxiom, Integer> numChosenRemaindersWithSentence;

    public SelectionFunctionDialogue(Window w,
            List<Set<OWLAxiom>> remainderSet) {
        super(w);
        this.remainderSet = remainderSet;

        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        setMinimumSize(new Dimension(300, 300));
        setPreferredSize(new Dimension(600, 600));
        setLocationByPlatform(true);
        setTitle("Selection function");

        JPanel topPanel = new JPanel();
        getContentPane().add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel instrLabel = new JLabel(
                "Choose at least one remainder set. Only sentences that are in ALL of the selected sets will be kept.");
        topPanel.add(instrLabel);

        JLabel declarationsOmittedLabel = new JLabel(
                "For brevity, Declaration sentences and obvious tautologies are not shown.");
        topPanel.add(declarationsOmittedLabel);

        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        runBtn = new JButton("Execute operation");
        bottomPanel.add(runBtn);

        if (USE_TABS) {
            remainderSetContainer = new JTabbedPane(JTabbedPane.LEFT);
            getContentPane().add(remainderSetContainer, BorderLayout.CENTER);
        } else {
            remainderSetContainer = new JPanel();
            remainderSetContainer.setLayout(
                    new BoxLayout(remainderSetContainer, BoxLayout.Y_AXIS));
            JScrollPane scroll = new JScrollPane();
            scroll.setViewportView(remainderSetContainer);
            getContentPane().add(scroll, BorderLayout.CENTER);
        }

        init();

        pack();
        setVisible(true);
    }

    private void init() {
        remainderCheckboxes = new JCheckBox[remainderSet.size()];
        checkBoxToRemainderNum = new HashMap<>();
        numChosenRemaindersWithSentence = new HashMap<>();
        chosen = new HashSet<>();
        sentenceToLabels = new HashMap<>();

        ActionListener listener = (ActionEvent e) -> {
            JCheckBox c = ((JCheckBox) e.getSource());
            int i = checkBoxToRemainderNum.get(c);
            if (c.isSelected())
                select(i);
            else
                deselect(i);
        };

        int i = 0;
        for (Set<OWLAxiom> remainder : remainderSet) {
            JCheckBox c = new JCheckBox("Select this remainder");
            remainderCheckboxes[i] = c;
            c.addActionListener(listener);
            checkBoxToRemainderNum.put(c, i);
            List<JLabel> labelsForThisRemainder = new LinkedList<>();
            for (OWLAxiom sentence : remainder) {
                if (!numChosenRemaindersWithSentence.containsKey(sentence)) {
                    numChosenRemaindersWithSentence.put(sentence, 0);
                    sentenceToLabels.put(sentence, new LinkedList<>());
                }

                JLabel label = new JLabel(
                        SentenceFormatter.humanReadable(sentence));
                labelsForThisRemainder.add(label);
                label.setVisible(!(isObviouslyTautological(sentence)));
                sentenceToLabels.get(sentence).add(label);
            }
            String remTitle = "Remainder " + (++i);
            RemainderElementPresenter presenter = new RemainderElementPresenter(
                    c, labelsForThisRemainder);
            remainderSetContainer.add(remTitle, presenter);
            if (remainderSetContainer instanceof JPanel) {
                presenter.setBorder(BorderFactory.createTitledBorder(remTitle));
                remainderSetContainer.add(Box.createVerticalStrut(25));
            }
        }
        updateLabelColours();
        runBtn.addActionListener(e -> {
            if (chosen.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Make sure at least one remainder is selected.",
                        "Invalid selection result", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = Collections.unmodifiableSet(chosen);
            setVisible(false);
            dispose();
        });
    }

    private synchronized void select(int i) {
        if (chosen.add(i)) {
            for (OWLAxiom sentence : remainderSet.get(i)) {
                int n = numChosenRemaindersWithSentence.get(sentence);
                numChosenRemaindersWithSentence.put(sentence, n + 1);
            }
            updateLabelColours();
        }
    }

    private synchronized void deselect(int i) {
        if (chosen.remove(i)) {
            for (OWLAxiom sentence : remainderSet.get(i)) {
                int n = numChosenRemaindersWithSentence.get(sentence);
                numChosenRemaindersWithSentence.put(sentence, n - 1);
            }
            updateLabelColours();
        }
    }

    private void updateLabelColours() {
        for (Entry<OWLAxiom, List<JLabel>> entry : sentenceToLabels
                .entrySet()) {
            int n = numChosenRemaindersWithSentence.get(entry.getKey());
            boolean kept = n > 0 && n == chosen.size();
            for (JLabel l : entry.getValue())
                l.setForeground(kept ? DEFAULT_COLOUR : DELETED_COLOUR);
        }
    }

    public Set<Integer> getSelectionResult() {
        return result;
    }

    protected boolean isObviouslyTautological(OWLAxiom sentence) {
        if (sentence instanceof OWLDeclarationAxiom)
            return true;
        if (sentence instanceof OWLSubClassOfAxiom) {
            return (((OWLSubClassOfAxiom) sentence).getSuperClass()
                    .isOWLThing())
                    || (((OWLSubClassOfAxiom) sentence).getSubClass()
                            .isOWLNothing());
        }
        if (sentence instanceof OWLSubObjectPropertyOfAxiom) {
            return (((OWLSubObjectPropertyOfAxiom) sentence).getSuperProperty()
                    .isTopEntity())
                    || (((OWLSubObjectPropertyOfAxiom) sentence)
                            .getSubProperty().isBottomEntity());
        }
        if (sentence instanceof OWLSubDataPropertyOfAxiom) {
            return (((OWLSubDataPropertyOfAxiom) sentence).getSuperProperty()
                    .isTopEntity())
                    || (((OWLSubDataPropertyOfAxiom) sentence).getSubProperty()
                            .isBottomEntity());
        }
        if (sentence instanceof OWLDisjointClassesAxiom) {
            List<OWLClassExpression> expr = ((OWLDisjointClassesAxiom) sentence)
                    .getClassExpressionsAsList();
            return expr.size() == 2
                    && expr.stream().anyMatch(OWLClassExpression::isOWLNothing);
        }
        if (sentence instanceof OWLDisjointObjectPropertiesAxiom) {
            List<OWLObjectPropertyExpression> expr = new ArrayList<OWLObjectPropertyExpression>(
                    ((OWLDisjointObjectPropertiesAxiom) sentence)
                            .getProperties());
            return expr.size() == 2 && expr.stream().anyMatch(
                    OWLObjectPropertyExpression::isOWLBottomObjectProperty);
        }
        if (sentence instanceof OWLDisjointDataPropertiesAxiom) {
            List<OWLDataPropertyExpression> expr = new ArrayList<OWLDataPropertyExpression>(
                    ((OWLDisjointDataPropertiesAxiom) sentence)
                            .getProperties());
            return expr.size() == 2 && expr.stream().anyMatch(
                    OWLDataPropertyExpression::isOWLBottomDataProperty);
        }
        if (sentence instanceof OWLClassAssertionAxiom) {
            return ((OWLClassAssertionAxiom) sentence).getClassExpression()
                    .isOWLThing();
        }
        if (sentence instanceof OWLObjectPropertyAssertionAxiom) {
            return ((OWLObjectPropertyAssertionAxiom) sentence).getProperty()
                    .isTopEntity();
        }
        if (sentence instanceof OWLDataPropertyAssertionAxiom) {
            return ((OWLDataPropertyAssertionAxiom) sentence).getProperty()
                    .isTopEntity();
        }
        return false;
    }

}
