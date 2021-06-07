package br.usp.ime.pseudocontraction.protegeplugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

public class MainView extends AbstractOWLViewComponent {
    public MainView() {
    }

    private static final long serialVersionUID = 1505057428784911280L;
    private final Logger logger = Logger.getLogger(MainView.class);
    private JPanel pcMainPanel;

    @Override
    protected void initialiseOWLView() throws Exception {
        logger.info("Pseudo-contraction view: initialised");
        setLayout(new BorderLayout());
        pcMainPanel = new PseudoContractionMainPanel(getOWLEditorKit(),
                getOWLModelManager());
        add(pcMainPanel, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {
        logger.info("Pseudo-contraction view: disposed");
    }

}
