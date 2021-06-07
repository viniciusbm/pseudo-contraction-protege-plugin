package br.usp.ime.pseudocontraction.protegeplugin.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.semanticweb.owlapi.model.OWLAxiom;

import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.ClassicalMaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.DivideAndConquerMaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.MaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.RelativeSizeSlidingWindowMaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.enlarge.SlidingWindowMaxNonEnlarger;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.ClassicalMaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.DivideAndConquerMaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.MaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.RelativeSizeSlidingWindowMaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.SlidingWindowMaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.SyntacticConnectivityMaxNonShrinker;
import br.usp.ime.owlchange.maxnon.single.blackbox.shrink.TrivialMaxNonShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.ClassicalMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.DivideAndConquerMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.MinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.RelativeSizeSlidingWindowMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.SlidingWindowMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.SyntacticConnectivityMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.enlarge.TrivialMinImpEnlarger;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.ClassicalMinImpShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.DivideAndConquerMinImpShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.MinImpShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.RelativeSizeSlidingWindowMinImpShrinker;
import br.usp.ime.owlchange.minimp.single.blackbox.shrink.SlidingWindowMinImpShrinker;
import br.usp.ime.pseudocontraction.protegeplugin.container.ConstructionPanel.Construction;

public class StrategyPanel extends JPanel {

    private static final long serialVersionUID = -7554075169576560246L;
    OWLAxiom sentence;
    private final JComboBox<Strategy<MinImpShrinker>> kerShrinkingStrategyCbox;
    private final JComboBox<Strategy<MinImpEnlarger>> kerEnlargingStrategyCbox;
    private final JComboBox<SortingOption> kerEnlargingSortCbox;
    private final JComboBox<Strategy<MaxNonEnlarger>> remEnlargingStrategyCbox;
    private final JComboBox<SortingOption> remShrinkingSortCbox;
    private final JComboBox<Strategy<MaxNonShrinker>> remShrinkingStrategyCbox;
    private final JPanel remainderStrategyPanel, kernelStrategyPanel;

    private JLabel remEnlargingLbl;
    private JPanel remEnlargingArgPanel;
    private JSpinner remEnlargingFixedSWSizeSpinner;
    private JSpinner remEnlargingRelativeSWSizeSpinner;
    private JLabel remShrinkLbl;
    private JSpinner remShrinkingRelativeSWSizeSpinner;
    private JPanel remShrinkingArgPanel;
    private JSpinner remShrinkingFixedSWSizeSpinner;

    private JLabel kerShrinkLbl;
    private JPanel kerEnlargingArgPanel;
    private JSpinner kerEnlargingFixedSWSizeSpinner;
    private JSpinner kerEnlargingRelativeSWSizeSpinner;
    private JLabel kerEnlargingLbl;
    private JPanel kerShrinkingArgPanel;
    private JSpinner kerShrinkingFixedSWSizeSpinner;
    private JSpinner kerShrinkingRelativeSWSizeSpinner;

    private Map<Construction, JPanel> panelOfConstruction;
    private JPanel kernelEmptyPanel;
    private JPanel remainderEmptyPanel;
    private Component verticalGlue;

    public enum SortingOption {
        NONE("Do not sort"), BINARY("Sort (binary)"), COUNT("Sort (count)");

        private String displayText;

        SortingOption(String displayText) {
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public enum StrategyType {
        TRIVIAL("Trivial"), CLASSICAL("Classical"),
        DIVIDE_AND_CONQUER("Divide and conquer"),
        SLIDING_WINDOW_FIXED_SIZE("Sliding window (fixed size)"),
        SLIDING_WINDOW_RELATIVE_SIZE("Sliding window (relative size)"),
        SYNTACTIC_CONNECTIVITY("Syntactic connectivity");

        private String displayText;

        StrategyType(String displayText) {
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    private abstract class Strategy<T> {

        private StrategyType strategyType;

        public Strategy(StrategyType strategyType, Construction contruction) {
            this.strategyType = strategyType;
        }

        @Override
        public String toString() {
            return strategyType.toString();
        }

        public abstract T getStrategyInstance();

        public StrategyType getStrategyType() {
            return strategyType;
        }

    }

    public StrategyPanel() {
        setPreferredSize(new Dimension(600, 100));
        setMaximumSize(new Dimension(32767, 100));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        remainderStrategyPanel = new JPanel();
        remainderStrategyPanel.setLayout(new GridLayout(0, 4, 5, 5));
        add(remainderStrategyPanel);

        remShrinkLbl = new JLabel("Shrinking phase");
        remShrinkLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        remainderStrategyPanel.add(remShrinkLbl);

        remShrinkingSortCbox = new JComboBox<>();
        remainderStrategyPanel.add(remShrinkingSortCbox);

        remShrinkingStrategyCbox = new JComboBox<>();
        remainderStrategyPanel.add(remShrinkingStrategyCbox);

        remShrinkingArgPanel = new JPanel();
        remainderStrategyPanel.add(remShrinkingArgPanel);
        remShrinkingArgPanel.setLayout(
                new BoxLayout(remShrinkingArgPanel, BoxLayout.X_AXIS));

        remShrinkingFixedSWSizeSpinner = new JSpinner();
        remShrinkingFixedSWSizeSpinner.setVisible(false);
        remShrinkingFixedSWSizeSpinner
                .setModel(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1));
        remShrinkingArgPanel.add(remShrinkingFixedSWSizeSpinner);

        remShrinkingRelativeSWSizeSpinner = new JSpinner();
        remShrinkingRelativeSWSizeSpinner.setVisible(false);
        remShrinkingRelativeSWSizeSpinner
                .setModel(new SpinnerNumberModel(0.3, 0, 1.0, 0.001));
        remShrinkingArgPanel.add(remShrinkingRelativeSWSizeSpinner);
        remShrinkingRelativeSWSizeSpinner.setEditor(new JSpinner.NumberEditor(
                remShrinkingRelativeSWSizeSpinner, "0.000"));

        remEnlargingLbl = new JLabel("Enlarging phase");
        remEnlargingLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        remainderStrategyPanel.add(remEnlargingLbl);

        remainderEmptyPanel = new JPanel();
        remainderStrategyPanel.add(remainderEmptyPanel);

        remEnlargingStrategyCbox = new JComboBox<>();
        remainderStrategyPanel.add(remEnlargingStrategyCbox);

        remEnlargingArgPanel = new JPanel();
        remainderStrategyPanel.add(remEnlargingArgPanel);
        remEnlargingArgPanel.setLayout(
                new BoxLayout(remEnlargingArgPanel, BoxLayout.X_AXIS));

        remEnlargingFixedSWSizeSpinner = new JSpinner();
        remEnlargingFixedSWSizeSpinner.setVisible(false);
        remEnlargingFixedSWSizeSpinner
                .setModel(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1));
        remEnlargingArgPanel.add(remEnlargingFixedSWSizeSpinner);

        remEnlargingRelativeSWSizeSpinner = new JSpinner();
        remEnlargingRelativeSWSizeSpinner.setVisible(false);
        remEnlargingRelativeSWSizeSpinner
                .setModel(new SpinnerNumberModel(0.3, 0, 1.0, 0.001));
        remEnlargingArgPanel.add(remEnlargingRelativeSWSizeSpinner);
        remEnlargingRelativeSWSizeSpinner.setEditor(new JSpinner.NumberEditor(
                remEnlargingRelativeSWSizeSpinner, "0.000"));

        kernelStrategyPanel = new JPanel();
        add(kernelStrategyPanel);
        kernelStrategyPanel.setLayout(new GridLayout(0, 4, 5, 5));
        kerEnlargingLbl = new JLabel("Enlarging phase");
        kerEnlargingLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        kernelStrategyPanel.add(kerEnlargingLbl);

        kerEnlargingSortCbox = new JComboBox<>();
        kernelStrategyPanel.add(kerEnlargingSortCbox);

        kerEnlargingStrategyCbox = new JComboBox<>();
        kernelStrategyPanel.add(kerEnlargingStrategyCbox);

        kerEnlargingArgPanel = new JPanel();
        kernelStrategyPanel.add(kerEnlargingArgPanel);
        kerEnlargingArgPanel.setLayout(
                new BoxLayout(kerEnlargingArgPanel, BoxLayout.X_AXIS));

        kerEnlargingFixedSWSizeSpinner = new JSpinner();
        kerEnlargingFixedSWSizeSpinner.setVisible(false);
        kerEnlargingFixedSWSizeSpinner
                .setModel(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1));
        kerEnlargingArgPanel.add(kerEnlargingFixedSWSizeSpinner);

        kerEnlargingRelativeSWSizeSpinner = new JSpinner();
        kerEnlargingRelativeSWSizeSpinner.setVisible(false);
        kerEnlargingRelativeSWSizeSpinner
                .setModel(new SpinnerNumberModel(0.3, 0, 1.0, 0.001));
        kerEnlargingArgPanel.add(kerEnlargingRelativeSWSizeSpinner);
        kerEnlargingRelativeSWSizeSpinner.setEditor(new JSpinner.NumberEditor(
                kerEnlargingRelativeSWSizeSpinner, "0.000"));

        kerShrinkLbl = new JLabel("Shrinking phase");
        kerShrinkLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        kernelStrategyPanel.add(kerShrinkLbl);

        kernelEmptyPanel = new JPanel();
        kernelStrategyPanel.add(kernelEmptyPanel);

        kerShrinkingStrategyCbox = new JComboBox<>();
        kernelStrategyPanel.add(kerShrinkingStrategyCbox);

        kerShrinkingArgPanel = new JPanel();
        kernelStrategyPanel.add(kerShrinkingArgPanel);
        kerShrinkingArgPanel.setLayout(
                new BoxLayout(kerShrinkingArgPanel, BoxLayout.X_AXIS));

        kerShrinkingFixedSWSizeSpinner = new JSpinner();
        kerShrinkingFixedSWSizeSpinner.setVisible(false);
        kerShrinkingFixedSWSizeSpinner
                .setModel(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1));
        kerShrinkingArgPanel.add(kerShrinkingFixedSWSizeSpinner);

        kerShrinkingRelativeSWSizeSpinner = new JSpinner();
        kerShrinkingRelativeSWSizeSpinner.setVisible(false);
        kerShrinkingRelativeSWSizeSpinner
                .setModel(new SpinnerNumberModel(0.3, 0, 1.0, 0.001));
        kerShrinkingArgPanel.add(kerShrinkingRelativeSWSizeSpinner);
        kerShrinkingRelativeSWSizeSpinner.setEditor(new JSpinner.NumberEditor(
                kerShrinkingRelativeSWSizeSpinner, "0.000"));

        verticalGlue = Box.createVerticalGlue();
        add(verticalGlue);

        fill();

        SwingUtilities.invokeLater(() -> showOnly(null));
    }

    protected void fill() {
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.CLASSICAL, Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                return new ClassicalMaxNonShrinker();
            }
        });
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.TRIVIAL, Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                return new TrivialMaxNonShrinker();
            }
        });
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.SLIDING_WINDOW_FIXED_SIZE,
                Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                return new SlidingWindowMaxNonShrinker(
                        (int) remShrinkingFixedSWSizeSpinner.getValue());
            }
        });
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE,
                Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                return new RelativeSizeSlidingWindowMaxNonShrinker(
                        ((Double) remShrinkingRelativeSWSizeSpinner.getValue())
                                .floatValue());
            }
        });
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.DIVIDE_AND_CONQUER, Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                return new DivideAndConquerMaxNonShrinker();
            }
        });
        remShrinkingStrategyCbox.addItem(new Strategy<MaxNonShrinker>(
                StrategyType.SYNTACTIC_CONNECTIVITY,
                Construction.PARTIAL_MEET) {

            @Override
            public MaxNonShrinker getStrategyInstance() {
                if (sentence == null)
                    return null;
                return new SyntacticConnectivityMaxNonShrinker(
                        sentence.getSignature());
            }
        });

        remEnlargingStrategyCbox.addItem(new Strategy<MaxNonEnlarger>(
                StrategyType.CLASSICAL, Construction.PARTIAL_MEET) {

            @Override
            public MaxNonEnlarger getStrategyInstance() {
                return new ClassicalMaxNonEnlarger();
            }
        });
        remEnlargingStrategyCbox.addItem(new Strategy<MaxNonEnlarger>(
                StrategyType.SLIDING_WINDOW_FIXED_SIZE,
                Construction.PARTIAL_MEET) {

            @Override
            public MaxNonEnlarger getStrategyInstance() {
                return new SlidingWindowMaxNonEnlarger(
                        (int) remEnlargingFixedSWSizeSpinner.getValue());
            }
        });
        remEnlargingStrategyCbox.addItem(new Strategy<MaxNonEnlarger>(
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE,
                Construction.PARTIAL_MEET) {

            @Override
            public MaxNonEnlarger getStrategyInstance() {
                return new RelativeSizeSlidingWindowMaxNonEnlarger(
                        ((Double) remEnlargingRelativeSWSizeSpinner.getValue())
                                .floatValue());
            }
        });
        remEnlargingStrategyCbox.addItem(new Strategy<MaxNonEnlarger>(
                StrategyType.DIVIDE_AND_CONQUER, Construction.PARTIAL_MEET) {

            @Override
            public MaxNonEnlarger getStrategyInstance() {
                return new DivideAndConquerMaxNonEnlarger();
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.CLASSICAL, Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                return new ClassicalMinImpEnlarger();
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.TRIVIAL, Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                return new TrivialMinImpEnlarger();
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.SLIDING_WINDOW_FIXED_SIZE, Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                return new SlidingWindowMinImpEnlarger(
                        (int) kerEnlargingFixedSWSizeSpinner.getValue());
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE,
                Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                return new RelativeSizeSlidingWindowMinImpEnlarger(
                        ((Double) kerEnlargingRelativeSWSizeSpinner.getValue())
                                .floatValue());
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.DIVIDE_AND_CONQUER, Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                return new DivideAndConquerMinImpEnlarger();
            }
        });

        kerEnlargingStrategyCbox.addItem(new Strategy<MinImpEnlarger>(
                StrategyType.SYNTACTIC_CONNECTIVITY, Construction.KERNEL) {

            @Override
            public MinImpEnlarger getStrategyInstance() {
                if (sentence == null)
                    return null;
                return new SyntacticConnectivityMinImpEnlarger(
                        sentence.getSignature());
            }
        });

        kerShrinkingStrategyCbox.addItem(new Strategy<MinImpShrinker>(
                StrategyType.CLASSICAL, Construction.KERNEL) {

            @Override
            public MinImpShrinker getStrategyInstance() {
                return new ClassicalMinImpShrinker();
            }
        });
        kerShrinkingStrategyCbox.addItem(new Strategy<MinImpShrinker>(
                StrategyType.SLIDING_WINDOW_FIXED_SIZE, Construction.KERNEL) {

            @Override
            public MinImpShrinker getStrategyInstance() {
                return new SlidingWindowMinImpShrinker(
                        (int) kerShrinkingFixedSWSizeSpinner.getValue());
            }
        });
        kerShrinkingStrategyCbox.addItem(new Strategy<MinImpShrinker>(
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE,
                Construction.KERNEL) {

            @Override
            public MinImpShrinker getStrategyInstance() {
                return new RelativeSizeSlidingWindowMinImpShrinker(
                        ((Double) kerShrinkingRelativeSWSizeSpinner.getValue())
                                .floatValue());
            }
        });
        kerShrinkingStrategyCbox.addItem(new Strategy<MinImpShrinker>(
                StrategyType.DIVIDE_AND_CONQUER, Construction.KERNEL) {

            @Override
            public MinImpShrinker getStrategyInstance() {
                return new DivideAndConquerMinImpShrinker();
            }
        });

        for (SortingOption s : SortingOption.values()) {
            kerEnlargingSortCbox.addItem(s);
            remShrinkingSortCbox.addItem(s);
        }

        panelOfConstruction = Stream
                .of(new Object[][] {
                        { Construction.KERNEL, kernelStrategyPanel },
                        { Construction.PARTIAL_MEET, remainderStrategyPanel } })
                .collect(Collectors.toMap(x -> (Construction) x[0],
                        x -> (JPanel) x[1]));

        linkStrategyToNumericFieldVisibility(remEnlargingStrategyCbox,
                remEnlargingFixedSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_FIXED_SIZE);
        linkStrategyToNumericFieldVisibility(remEnlargingStrategyCbox,
                remEnlargingRelativeSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE);
        linkStrategyToNumericFieldVisibility(kerEnlargingStrategyCbox,
                kerEnlargingFixedSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_FIXED_SIZE);
        linkStrategyToNumericFieldVisibility(kerEnlargingStrategyCbox,
                kerEnlargingRelativeSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE);
        linkStrategyToNumericFieldVisibility(remShrinkingStrategyCbox,
                remShrinkingFixedSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_FIXED_SIZE);
        linkStrategyToNumericFieldVisibility(remShrinkingStrategyCbox,
                remShrinkingRelativeSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE);
        linkStrategyToNumericFieldVisibility(kerShrinkingStrategyCbox,
                kerShrinkingFixedSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_FIXED_SIZE);
        linkStrategyToNumericFieldVisibility(kerShrinkingStrategyCbox,
                kerShrinkingRelativeSWSizeSpinner,
                StrategyType.SLIDING_WINDOW_RELATIVE_SIZE);
    }

    @SuppressWarnings("unchecked")
    private void linkStrategyToNumericFieldVisibility(JComboBox<?> cbox,
            JSpinner spinner, StrategyType st) {
        cbox.addItemListener(e -> {
            if (((Strategy<MinImpShrinker>) e.getItem())
                    .getStrategyType() == st)
                spinner.setVisible(e.getStateChange() == ItemEvent.SELECTED);
        });
    }

    public MaxNonEnlarger getRemainderEnlarger() {
        @SuppressWarnings("unchecked")
        Strategy<MaxNonEnlarger> strategy = (Strategy<MaxNonEnlarger>) remEnlargingStrategyCbox
                .getSelectedItem();
        if (strategy == null)
            return null;
        return strategy.getStrategyInstance();
    }

    public MaxNonShrinker getRemainderShrinker() {
        @SuppressWarnings("unchecked")
        Strategy<MaxNonShrinker> strategy = (Strategy<MaxNonShrinker>) remShrinkingStrategyCbox
                .getSelectedItem();
        if (strategy == null)
            return null;
        return strategy.getStrategyInstance();
    }

    public MinImpEnlarger getKernelEnlarger() {
        @SuppressWarnings("unchecked")
        Strategy<MinImpEnlarger> strategy = (Strategy<MinImpEnlarger>) kerEnlargingStrategyCbox
                .getSelectedItem();
        if (strategy == null)
            return null;
        return strategy.getStrategyInstance();
    }

    public MinImpShrinker getKernelShrinker() {
        @SuppressWarnings("unchecked")
        Strategy<MinImpShrinker> strategy = (Strategy<MinImpShrinker>) kerShrinkingStrategyCbox
                .getSelectedItem();
        if (strategy == null)
            return null;
        return strategy.getStrategyInstance();
    }

    public void showOnly(Construction construction) {
        for (Entry<Construction, JPanel> entry : panelOfConstruction
                .entrySet()) {
            entry.getValue().setVisible(entry.getKey() == construction);
        }
        SwingUtilities.invokeLater(() -> {
            repaint();
        });

    }

    public SortingOption getRemainderSortingOption() {
        return (SortingOption) remShrinkingSortCbox.getSelectedItem();
    }

    public SortingOption getKernelSortingOption() {
        return (SortingOption) kerEnlargingSortCbox.getSelectedItem();
    }

    public OWLAxiom getSentence() {
        return sentence;
    }

    public void setSentence(OWLAxiom sentence) {
        this.sentence = sentence;
    }

}
