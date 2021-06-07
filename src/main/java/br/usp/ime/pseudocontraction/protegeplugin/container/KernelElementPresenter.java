package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import br.usp.ime.pseudocontraction.protegeplugin.window.IncisionFunctionDialogue;

public class KernelElementPresenter extends JPanel {

    public KernelElementPresenter(List<JCheckBox> checkboxes) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (IncisionFunctionDialogue.USE_TABS) {
            setLayout(new BorderLayout(0, 0));
            JScrollPane scrollPane = new JScrollPane();
            add(scrollPane, BorderLayout.CENTER);
            scrollPane.setViewportView(panel);
        } else {
            add(panel, BorderLayout.CENTER);
        }

        for (JCheckBox cb : checkboxes)
            panel.add(cb);

    }

    private static final long serialVersionUID = 677763331551019324L;

}
