package br.usp.ime.pseudocontraction.protegeplugin.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
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

import br.usp.ime.owlchange.util.SentenceFormatter;
import br.usp.ime.pseudocontraction.protegeplugin.container.KernelElementPresenter;

public class IncisionFunctionDialogue extends JDialog {

    public static final boolean USE_TABS = false;

    private static final long serialVersionUID = 8368119405400626372L;

    private static final Color DEFAULT_COLOUR = new JLabel().getForeground();
    private static final Color DELETED_COLOUR = Color.RED;

    private Map<OWLAxiom, List<JCheckBox>> sentenceToCheckBoxes;
    private Map<JCheckBox, OWLAxiom> checkBoxToAxiom;
    private Map<OWLAxiom, List<Integer>> sentenceToKernelNumbers;
    private Set<OWLAxiom> chosen;
    private int[] numChosenInKernel;
    private JComponent kernelSetContainer; // JPanel or JTabbedPane

    private Set<OWLAxiom> result = null;

    private JButton runBtn;

    public IncisionFunctionDialogue(Window w, Set<Set<OWLAxiom>> kernelSet) {
        super(w);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        setMinimumSize(new Dimension(300, 300));
        setPreferredSize(new Dimension(600, 600));
        setLocationByPlatform(true);
        setTitle("Incision function");

        JPanel topPanel = new JPanel();
        getContentPane().add(topPanel, BorderLayout.NORTH);

        JLabel instrLabel = new JLabel(
                "From each kernel, choose at least one sentence to be removed.");
        topPanel.add(instrLabel);

        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        runBtn = new JButton("Execute operation");
        bottomPanel.add(runBtn);

        if (USE_TABS) {
            kernelSetContainer = new JTabbedPane(JTabbedPane.LEFT);
            getContentPane().add(kernelSetContainer, BorderLayout.CENTER);
        } else {
            kernelSetContainer = new JPanel();
            kernelSetContainer.setLayout(
                    new BoxLayout(kernelSetContainer, BoxLayout.Y_AXIS));
            JScrollPane scroll = new JScrollPane();
            scroll.setViewportView(kernelSetContainer);
            getContentPane().add(scroll, BorderLayout.CENTER);
        }

        init(kernelSet);

        pack();
        setVisible(true);
    }

    private void init(Set<Set<OWLAxiom>> kernelSet) {
        sentenceToCheckBoxes = new HashMap<>();
        checkBoxToAxiom = new HashMap<>();
        sentenceToKernelNumbers = new HashMap<>();
        chosen = new HashSet<>();
        numChosenInKernel = new int[kernelSet.size()];

        ActionListener listener = (ActionEvent e) -> {
            JCheckBox c = ((JCheckBox) e.getSource());
            OWLAxiom sentence = checkBoxToAxiom.get(c);
            if (c.isSelected())
                select(sentence);
            else
                deselect(sentence);
        };

        int i = 0;
        for (Set<OWLAxiom> kernel : kernelSet) {
            List<JCheckBox> checkBoxesForThisKernel = new LinkedList<>();
            for (OWLAxiom sentence : kernel) {
                if (!sentenceToCheckBoxes.containsKey(sentence)) {
                    sentenceToCheckBoxes.put(sentence, new LinkedList<>());
                    sentenceToKernelNumbers.put(sentence, new LinkedList<>());
                }
                JCheckBox c = new JCheckBox(
                        SentenceFormatter.humanReadable(sentence));
                checkBoxesForThisKernel.add(c);
                sentenceToCheckBoxes.get(sentence).add(c);
                sentenceToKernelNumbers.get(sentence).add(i);
                numChosenInKernel[i] = 0;
                checkBoxToAxiom.put(c, sentence);
                c.addActionListener(listener);
            }
            KernelElementPresenter presenter = new KernelElementPresenter(
                    checkBoxesForThisKernel);
            String kerTitle = "Kernel " + (++i);
            kernelSetContainer.add(kerTitle, presenter);
            if (kernelSetContainer instanceof JPanel) {
                presenter.setBorder(BorderFactory.createTitledBorder(kerTitle));
            }
        }

        runBtn.addActionListener(e -> {
            int min = Arrays.stream(numChosenInKernel).min().getAsInt();
            if (min == 0) {
                JOptionPane.showMessageDialog(this,
                        "Make sure at least one sentence from each kernel is selected.",
                        "Invalid incision result", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = Collections.unmodifiableSet(chosen);
            setVisible(false);
            dispose();
        });
    }

    private synchronized void select(OWLAxiom sentence) {
        if (chosen.add(sentence)) {
            for (JCheckBox c : sentenceToCheckBoxes.get(sentence)) {
                c.setSelected(true);
                c.setForeground(DELETED_COLOUR);
            }
            for (int i : sentenceToKernelNumbers.get(sentence))
                numChosenInKernel[i]++;
        }
    }

    private synchronized void deselect(OWLAxiom sentence) {
        if (chosen.remove(sentence)) {
            for (JCheckBox c : sentenceToCheckBoxes.get(sentence)) {
                c.setSelected(false);
                c.setForeground(DEFAULT_COLOUR);
            }
            for (int i : sentenceToKernelNumbers.get(sentence))
                numChosenInKernel[i]--;
        }
    }

    public Set<OWLAxiom> getIncisionResult() {
        return result;
    }

}
