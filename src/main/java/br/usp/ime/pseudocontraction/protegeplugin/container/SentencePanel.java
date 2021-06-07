package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;

import br.usp.ime.owlchange.util.SentenceFormatter;

@SuppressWarnings("deprecation")
public class SentencePanel extends JPanel {

    private static final long serialVersionUID = -7554075169576560246L;
    private JTextComponent editor;
    private OWLModelManager manager;

    public static class GenericAxiomChecker
            implements OWLExpressionChecker<OWLAxiom> {

        private OWLModelManager manager;

        public GenericAxiomChecker(OWLModelManager manager) {
            this.manager = manager;
        }

        @Override
        public void check(String text) throws OWLExpressionParserException {
            createObject(text);
        }

        @Override
        public OWLAxiom createObject(String text)
                throws OWLExpressionParserException {
            if (text == null || text.isEmpty()) {
                return null;
            }
            ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                    manager.getOWLDataFactory(), text);
            parser.setOWLEntityChecker(
                    new ProtegeOWLEntityChecker(manager.getOWLEntityFinder()));
            try {
                OWLAxiom ax = parser.parseAxiom();
                return ax;
            } catch (ParserException e) {
                throw ParserUtil.convertException(e);
            } catch (Exception e) {
                throw new OWLExpressionParserException(e);
            }
        }

    }

    public SentencePanel(OWLEditorKit editorKit, OWLModelManager manager) {
        this.manager = manager;
        setPreferredSize(new Dimension(32767, 70));
        setMaximumSize(new Dimension(32767, 400));
        setBorder(BorderFactory
                .createTitledBorder("Sentence (Manchester Syntax)"));

        editor = createEditor(editorKit, manager);
        add(editor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JLabel dlNotationTitleLabel = new JLabel("DL Notation: ");
        dlNotationTitleLabel
                .setFont(dlNotationTitleLabel.getFont().deriveFont(Font.BOLD));
        panel.add(dlNotationTitleLabel);

        JLabel sentenceLabel = new JLabel();
        sentenceLabel.setText(" ");
        panel.add(sentenceLabel);

        Component horizontalGlue = Box.createHorizontalGlue();
        panel.add(horizontalGlue);

        editor.getDocument().addDocumentListener(new DocumentListener() {

            GenericAxiomChecker parser = new GenericAxiomChecker(manager);

            @Override
            public void insertUpdate(DocumentEvent e) {
                changed(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed(e);
            }

            public void changed(DocumentEvent e) {
                Document d = e.getDocument();
                String txt;
                try {
                    txt = d.getText(0, d.getLength());
                } catch (BadLocationException ex) {
                    return;
                }
                OWLAxiom sentence;
                try {
                    sentence = parser.createObject(txt);
                } catch (Exception exc) {
                    sentence = null;
                }
                sentenceLabel.setText(sentence == null ? " "
                        : SentenceFormatter.humanReadable(sentence));
            }

        });
    }

    protected JTextComponent createEditor(OWLEditorKit editorKit,
            OWLModelManager manager) {
        try {
            return new ExpressionEditor<>(editorKit,
                    new GenericAxiomChecker(manager));
        } catch (Exception e) {
            return new JTextField();
        }
    }

    @SuppressWarnings("unchecked")
    public OWLAxiom getSentence() {
        try {
            if (editor instanceof ExpressionEditor)
                return ((ExpressionEditor<OWLAxiom>) editor).createObject();
            return new GenericAxiomChecker(manager)
                    .createObject(editor.getText());
        } catch (OWLException e) {
            return null;
        }
    }

}
