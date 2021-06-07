package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RemainderElementPresenter extends JPanel {

    public RemainderElementPresenter(JCheckBox remainderSelCbox,
            List<JLabel> labels) {

        setLayout(new BorderLayout(0, 0));

        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);

        topPanel.add(remainderSelCbox);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        scrollPane.setViewportView(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (JLabel l : labels)
            panel.add(l);

    }

    private static final long serialVersionUID = 677763331551019324L;

}
