package br.usp.ime.pseudocontraction.protegeplugin.container;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class RunPanel extends JPanel {

    private static final long serialVersionUID = -7554075169576560246L;
    private JButton runBtn;

    public RunPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        runBtn = new JButton("Run");
        runBtn.setAlignmentX(0.5f);
        add(runBtn);
    }

    public JButton getRun() {
        return runBtn;
    }
}
