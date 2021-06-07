package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.protege.editor.owl.ui.action.export.inferred.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

public class ConsequenceOperatorPanel extends JPanel {

    private static final long serialVersionUID = -7554075169576560246L;

    private static class Item {
        Class<? extends InferredAxiomGenerator<?>> c;
        JCheckBox checkBox;

        Item(Class<? extends InferredAxiomGenerator<?>> c, JCheckBox checkBox) {
            this.c = c;
            this.checkBox = checkBox;
            try {
                checkBox.setText(c.newInstance().getLabel());
            } catch (InstantiationException | IllegalAccessException e) {
            }
        }

        public InferredAxiomGenerator<?> getAxiomGeneratorInstance() {
            try {
                return c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }

    private List<Item> items = new LinkedList<>();

    public ConsequenceOperatorPanel() {
        JPanel classesSentenceTypePanel = new JPanel();
        add(classesSentenceTypePanel);
        classesSentenceTypePanel.setLayout(
                new BoxLayout(classesSentenceTypePanel, BoxLayout.PAGE_AXIS));

        JCheckBox cb;

        cb = new JCheckBox();
        classesSentenceTypePanel.add(cb);
        items.add(new Item(InferredClassAssertionAxiomGenerator.class, cb));

        cb = new JCheckBox();
        classesSentenceTypePanel.add(cb);
        items.add(new Item(InferredSubClassAxiomGenerator.class, cb));

        cb = new JCheckBox();
        classesSentenceTypePanel.add(cb);
        items.add(new Item(InferredEquivalentClassAxiomGenerator.class, cb));

        cb = new JCheckBox();
        classesSentenceTypePanel.add(cb);
        items.add(new Item(InferredDisjointClassesAxiomGenerator.class, cb));

        JPanel propertySentenceTypePanel1 = new JPanel();
        add(propertySentenceTypePanel1);
        propertySentenceTypePanel1.setLayout(
                new BoxLayout(propertySentenceTypePanel1, BoxLayout.Y_AXIS));

        JPanel propertySentenceTypePanel2 = new JPanel();
        add(propertySentenceTypePanel2);
        propertySentenceTypePanel2.setLayout(
                new BoxLayout(propertySentenceTypePanel2, BoxLayout.Y_AXIS));

        cb = new JCheckBox();
        propertySentenceTypePanel1.add(cb);
        items.add(new Item(InferredSubObjectPropertyAxiomGenerator.class, cb));

        cb = new JCheckBox();
        propertySentenceTypePanel1.add(cb);
        items.add(new Item(InferredSubDataPropertyAxiomGenerator.class, cb));

        cb = new JCheckBox();
        propertySentenceTypePanel1.add(cb);
        items.add(new Item(InferredEquivalentObjectPropertyAxiomGenerator.class,
                cb));

        cb = new JCheckBox();
        propertySentenceTypePanel1.add(cb);
        items.add(new Item(InferredEquivalentDataPropertiesAxiomGenerator.class,
                cb));

        cb = new JCheckBox();
        propertySentenceTypePanel2.add(cb);
        items.add(new Item(InferredPropertyAssertionGenerator.class, cb));

        cb = new JCheckBox();
        propertySentenceTypePanel2.add(cb);
        items.add(new Item(InferredInverseObjectPropertiesAxiomGenerator.class,
                cb));

        cb = new JCheckBox();
        propertySentenceTypePanel2.add(cb);
        items.add(new Item(
                InferredObjectPropertyCharacteristicAxiomGenerator.class, cb));

        cb = new JCheckBox();
        propertySentenceTypePanel2.add(cb);
        items.add(new Item(
                InferredDataPropertyCharacteristicAxiomGenerator.class, cb));
    }

    public InferredOntologyGenerator getCnStar(OWLReasoner reasoner) {
        return new InferredOntologyGenerator(reasoner,
                items.stream().filter(i -> i.checkBox.isSelected())
                        .map(i -> i.getAxiomGeneratorInstance())
                        .filter(g -> g != null).collect(Collectors.toList()));
    }
}
