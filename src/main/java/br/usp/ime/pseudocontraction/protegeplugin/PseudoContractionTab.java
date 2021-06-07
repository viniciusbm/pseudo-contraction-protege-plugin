package br.usp.ime.pseudocontraction.protegeplugin;

import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PseudoContractionTab extends OWLWorkspaceViewsTab {

    private static final long serialVersionUID = -8279226371109164400L;
    private static final Logger log = LoggerFactory
            .getLogger(PseudoContractionTab.class);

    public PseudoContractionTab() {
        setToolTipText("Pseudo-contraction operations");
    }

    @Override
    public void initialise() {
        super.initialise();
        log.info("Pseudo-contraction tab: initialised");
    }

    @Override
    public void dispose() {
        super.dispose();
        log.info("Pseudo-contraction tab: disposed");
    }
}
