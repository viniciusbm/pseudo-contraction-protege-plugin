package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.google.common.collect.Iterators;

public class ConstructionPanel extends JPanel {

    private static final long serialVersionUID = -7554075169576560246L;
    private JRadioButton partialMeetRadio;
    private JRadioButton kernelRadio;
    private ButtonGroup group;

    public enum Construction {
        PARTIAL_MEET("Partial meet"), KERNEL("Kernel");

        String displayName;

        Construction(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public static Construction fromDisplayName(String name) {
            for (Construction c : Construction.values())
                if (c.displayName.equals(name))
                    return c;
            return null;
        }
    }

    public ConstructionPanel() {
        setPreferredSize(new Dimension(32767, 60));
        setMaximumSize(new Dimension(32767, 60));
        setBorder(BorderFactory.createTitledBorder("Construction"));
        setLayout(new GridLayout(0, 2, 0, 0));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        Component verticalGlue_2 = Box.createVerticalGlue();
        panel.add(verticalGlue_2);
        group = new ButtonGroup();

        partialMeetRadio = new JRadioButton("Partial meet");
        partialMeetRadio.setAlignmentX(0.5f);
        partialMeetRadio
                .setActionCommand(Construction.PARTIAL_MEET.displayName);
        panel.add(partialMeetRadio);
        group.add(partialMeetRadio);

        Component verticalGlue_3 = Box.createVerticalGlue();
        panel.add(verticalGlue_3);

        JPanel panel_1 = new JPanel();
        add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));

        Component verticalGlue_1 = Box.createVerticalGlue();
        panel_1.add(verticalGlue_1);

        kernelRadio = new JRadioButton("Kernel");
        kernelRadio.setAlignmentX(0.5f);
        kernelRadio.setActionCommand(Construction.KERNEL.displayName);
        panel_1.add(kernelRadio);
        group.add(kernelRadio);

        Component verticalGlue = Box.createVerticalGlue();
        panel_1.add(verticalGlue);

    }

    public Construction getConstruction() {
        ButtonModel m = group.getSelection();
        if (m == null)
            return null;
        String ac = m.getActionCommand();
        if (ac == null)
            return null;
        return Construction.fromDisplayName(ac);
    }

    public void addListener(Consumer<Construction> callback) {
        Iterators.forEnumeration(group.getElements())
                .forEachRemaining(r -> r.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                        callback.accept(Construction.fromDisplayName(
                                ((AbstractButton) e.getSource()).getText()));
                }));
    }
}
