package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.editor.filechooser.OggFileChooser;
import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.serialisation.expansion.SerialMachine;
import eu.ha3.matmos.serialisation.expansion.SerialMachineEvent;
import eu.ha3.matmos.serialisation.expansion.SerialMachineStream;

@SuppressWarnings("serial")
public class EditMachine extends JPanel implements IFlaggable {
    private boolean init = true;
    private final EditPanel edit;
    private final SerialMachine machine;
    private SerialMachineEvent event;
    private JSpinner delayIn;
    private JSpinner delayOut;
    private JSpinner fadeIn;
    private JSpinner fadeOut;

    private JTextField fileField;
    private JSpinner streamVol;
    private JSpinner streamPitch;
    private JCheckBox chckbxIsLooping;
    private SetRemoverPanel activeSet;
    private SetRemoverPanel inactiveSet;
    private JList<String> list;
    private JList<String> timedEvents;
    private boolean eventBeingModified = true;
    private JSpinner startDelay;
    private JSpinner delayMax;
    private InstantTextField instantEvent;
    private JSpinner delayMin;
    private JSpinner pitchMod;
    private JSpinner volMod;

    public EditMachine(EditPanel parentConstruct, final SerialMachine machineConstruct) {
        edit = parentConstruct;
        machine = machineConstruct;
        setLayout(new BorderLayout(0, 0));

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(options, BorderLayout.SOUTH);
        options.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JPanel internal = new JPanel();
        internal.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(internal, BorderLayout.CENTER);
        internal.setLayout(new BorderLayout(0, 0));

        JPanel internalOptions = new JPanel();
        internal.add(internalOptions, BorderLayout.NORTH);
        GridBagLayout gbl_internalOptions = new GridBagLayout();
        gbl_internalOptions.columnWidths = new int[] {50, 100, 100, 0};
        gbl_internalOptions.rowHeights = new int[] {0, 0, 0};
        gbl_internalOptions.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_internalOptions.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        internalOptions.setLayout(gbl_internalOptions);

        JLabel lblDelay = new JLabel("Delay before fade (in/out)");
        GridBagConstraints gbc_lblDelay = new GridBagConstraints();
        gbc_lblDelay.anchor = GridBagConstraints.EAST;
        gbc_lblDelay.insets = new Insets(0, 0, 5, 5);
        gbc_lblDelay.gridx = 0;
        gbc_lblDelay.gridy = 0;
        internalOptions.add(lblDelay, gbc_lblDelay);

        delayIn = new JSpinner();
        delayIn.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            machine.delay_fadein = (Float)delayIn.getValue();
            edit.flagChange();
        });
        delayIn.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_delayIn = new GridBagConstraints();
        gbc_delayIn.fill = GridBagConstraints.HORIZONTAL;
        gbc_delayIn.insets = new Insets(0, 0, 5, 5);
        gbc_delayIn.gridx = 1;
        gbc_delayIn.gridy = 0;
        internalOptions.add(delayIn, gbc_delayIn);

        delayOut = new JSpinner();
        delayOut.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            machine.delay_fadeout = (Float)delayOut.getValue();
            edit.flagChange();
        });
        delayOut.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_delayOut = new GridBagConstraints();
        gbc_delayOut.fill = GridBagConstraints.HORIZONTAL;
        gbc_delayOut.insets = new Insets(0, 0, 5, 0);
        gbc_delayOut.gridx = 2;
        gbc_delayOut.gridy = 0;
        internalOptions.add(delayOut, gbc_delayOut);

        JLabel lblFade = new JLabel("Fade time (in/out)");
        GridBagConstraints gbc_lblFade = new GridBagConstraints();
        gbc_lblFade.anchor = GridBagConstraints.EAST;
        gbc_lblFade.insets = new Insets(0, 0, 0, 5);
        gbc_lblFade.gridx = 0;
        gbc_lblFade.gridy = 1;
        internalOptions.add(lblFade, gbc_lblFade);

        fadeIn = new JSpinner();
        fadeIn.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            machine.fadein = (Float)fadeIn.getValue();
            edit.flagChange();
        });
        fadeIn.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_fadeIn = new GridBagConstraints();
        gbc_fadeIn.fill = GridBagConstraints.HORIZONTAL;
        gbc_fadeIn.insets = new Insets(0, 0, 0, 5);
        gbc_fadeIn.gridx = 1;
        gbc_fadeIn.gridy = 1;
        internalOptions.add(fadeIn, gbc_fadeIn);

        fadeOut = new JSpinner();
        fadeOut.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            machine.fadeout = (Float)fadeOut.getValue();
            edit.flagChange();
        });
        fadeOut.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_fadeOut = new GridBagConstraints();
        gbc_fadeOut.fill = GridBagConstraints.HORIZONTAL;
        gbc_fadeOut.gridx = 2;
        gbc_fadeOut.gridy = 1;
        internalOptions.add(fadeOut, gbc_fadeOut);

        JPanel otherGroup = new JPanel();
        internal.add(otherGroup, BorderLayout.CENTER);
        otherGroup.setLayout(new BorderLayout(0, 0));

        JPanel timed = new JPanel();
        timed.setBorder(new TitledBorder(null, "Timed Events", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        otherGroup.add(timed);
        timed.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        timed.add(panel_2, BorderLayout.CENTER);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();
        panel_2.add(panel_3, BorderLayout.NORTH);
        panel_3.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        panel_3.add(scrollPane_1, BorderLayout.CENTER);

        timedEvents = new JList<>();
        timedEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timedEvents.addListSelectionListener(arg0 -> {
            if (eventBeingModified) {
                return;
            }

            if (timedEvents.getSelectedIndex() != -1
                    && timedEvents.getSelectedIndex() < machine.event.size()) {
                changeToEvent(timedEvents.getSelectedIndex());

            }
        });
        timedEvents.setVisibleRowCount(3);
        scrollPane_1.setViewportView(timedEvents);

        JButton btnAdd = new JButton("+");
        btnAdd.addActionListener(e -> addEvent());
        panel_3.add(btnAdd, BorderLayout.EAST);

        JPanel panel_4 = new JPanel();
        panel_2.add(panel_4, BorderLayout.CENTER);
        GridBagLayout gbl_panel_4 = new GridBagLayout();
        gbl_panel_4.columnWidths = new int[] {0, 100, 100, 0};
        gbl_panel_4.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        gbl_panel_4.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_4.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_4.setLayout(gbl_panel_4);

        JLabel lblEvent = new JLabel("Event");
        GridBagConstraints gbc_lblEvent = new GridBagConstraints();
        gbc_lblEvent.insets = new Insets(0, 0, 5, 5);
        gbc_lblEvent.anchor = GridBagConstraints.EAST;
        gbc_lblEvent.gridx = 0;
        gbc_lblEvent.gridy = 0;
        panel_4.add(lblEvent, gbc_lblEvent);

        instantEvent = new InstantTextField() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void editEvent() {
                if (eventBeingModified) {
                    return;
                }

                event.event = instantEvent.getText();
                edit.flagChange();
            }
        };
        GridBagConstraints gbc_instantEvent = new GridBagConstraints();
        gbc_instantEvent.gridwidth = 2;
        gbc_instantEvent.insets = new Insets(0, 0, 5, 0);
        gbc_instantEvent.fill = GridBagConstraints.HORIZONTAL;
        gbc_instantEvent.gridx = 1;
        gbc_instantEvent.gridy = 0;
        panel_4.add(instantEvent, gbc_instantEvent);

        JLabel lblDelayMin = new JLabel("Delay (min/max)");
        GridBagConstraints gbc_lblDelayMin = new GridBagConstraints();
        gbc_lblDelayMin.anchor = GridBagConstraints.EAST;
        gbc_lblDelayMin.insets = new Insets(0, 0, 5, 5);
        gbc_lblDelayMin.gridx = 0;
        gbc_lblDelayMin.gridy = 1;
        panel_4.add(lblDelayMin, gbc_lblDelayMin);

        delayMin = new JSpinner();
        delayMin.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }

            event.delay_min = (Float)delayMin.getValue();
            edit.flagChange();
        });
        delayMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_delayMin = new GridBagConstraints();
        gbc_delayMin.fill = GridBagConstraints.HORIZONTAL;
        gbc_delayMin.insets = new Insets(0, 0, 5, 5);
        gbc_delayMin.gridx = 1;
        gbc_delayMin.gridy = 1;
        panel_4.add(delayMin, gbc_delayMin);

        delayMax = new JSpinner();
        delayMax.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }

            event.delay_max = (Float)delayMax.getValue();
            edit.flagChange();
        });
        delayMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_delayMax = new GridBagConstraints();
        gbc_delayMax.fill = GridBagConstraints.HORIZONTAL;
        gbc_delayMax.insets = new Insets(0, 0, 5, 0);
        gbc_delayMax.gridx = 2;
        gbc_delayMax.gridy = 1;
        panel_4.add(delayMax, gbc_delayMax);

        JLabel lblStartDelay = new JLabel("Start delay");
        GridBagConstraints gbc_lblStartDelay = new GridBagConstraints();
        gbc_lblStartDelay.anchor = GridBagConstraints.EAST;
        gbc_lblStartDelay.insets = new Insets(0, 0, 5, 5);
        gbc_lblStartDelay.gridx = 0;
        gbc_lblStartDelay.gridy = 2;
        panel_4.add(lblStartDelay, gbc_lblStartDelay);

        startDelay = new JSpinner();
        startDelay.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }

            event.delay_start = (Float)startDelay.getValue();
            edit.flagChange();
        });
        startDelay.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_startDelay = new GridBagConstraints();
        gbc_startDelay.fill = GridBagConstraints.HORIZONTAL;
        gbc_startDelay.insets = new Insets(0, 0, 5, 5);
        gbc_startDelay.gridx = 1;
        gbc_startDelay.gridy = 2;
        panel_4.add(startDelay, gbc_startDelay);

        JLabel lblMultipliervolpitch = new JLabel("Multiplier (vol/pitch)");
        GridBagConstraints gbc_lblMultipliervolpitch = new GridBagConstraints();
        gbc_lblMultipliervolpitch.anchor = GridBagConstraints.EAST;
        gbc_lblMultipliervolpitch.insets = new Insets(0, 0, 5, 5);
        gbc_lblMultipliervolpitch.gridx = 0;
        gbc_lblMultipliervolpitch.gridy = 3;
        panel_4.add(lblMultipliervolpitch, gbc_lblMultipliervolpitch);

        volMod = new JSpinner();
        volMod.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }

            event.vol_mod = (Float)volMod.getValue();
            edit.flagChange();
        });
        volMod.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_volMod = new GridBagConstraints();
        gbc_volMod.fill = GridBagConstraints.HORIZONTAL;
        gbc_volMod.insets = new Insets(0, 0, 5, 5);
        gbc_volMod.gridx = 1;
        gbc_volMod.gridy = 3;
        panel_4.add(volMod, gbc_volMod);

        pitchMod = new JSpinner();
        pitchMod.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }

            event.pitch_mod = (Float)pitchMod.getValue();
            edit.flagChange();
        });
        pitchMod.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_pitchMod = new GridBagConstraints();
        gbc_pitchMod.insets = new Insets(0, 0, 5, 0);
        gbc_pitchMod.fill = GridBagConstraints.HORIZONTAL;
        gbc_pitchMod.gridx = 2;
        gbc_pitchMod.gridy = 3;
        panel_4.add(pitchMod, gbc_pitchMod);

        JButton btnRemove = new JButton("REMOVE");
        btnRemove.addActionListener(arg0 -> removeEvent());
        GridBagConstraints gbc_btnRemove = new GridBagConstraints();
        gbc_btnRemove.insets = new Insets(0, 0, 0, 5);
        gbc_btnRemove.gridx = 0;
        gbc_btnRemove.gridy = 4;
        panel_4.add(btnRemove, gbc_btnRemove);

        JPanel stream = new JPanel();
        stream.setBorder(new TitledBorder(null, "Streaming Sound", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        otherGroup.add(stream, BorderLayout.SOUTH);
        GridBagLayout gbl_stream = new GridBagLayout();
        gbl_stream.columnWidths = new int[] {426, 0};
        gbl_stream.rowHeights = new int[] {20, 73, 0};
        gbl_stream.columnWeights = new double[] {0.0, Double.MIN_VALUE};
        gbl_stream.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        stream.setLayout(gbl_stream);

        JPanel filePanel = new JPanel();
        GridBagConstraints gbc_filePanel = new GridBagConstraints();
        gbc_filePanel.anchor = GridBagConstraints.NORTH;
        gbc_filePanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_filePanel.insets = new Insets(0, 0, 5, 0);
        gbc_filePanel.gridx = 0;
        gbc_filePanel.gridy = 0;
        stream.add(filePanel, gbc_filePanel);
        filePanel.setLayout(new BorderLayout(0, 0));

        // dag edit
        fileField = new JTextField();
        fileField.setText(machine.stream != null ? machine.stream.path : "");
        fileField.setEditable(false);
        fileField.setEnabled(true);
        filePanel.add(fileField);
        fileField.setColumns(10);

        JButton btnOpen = new JButton("Open...");
        btnOpen.addActionListener(e -> addStreamDialog());
        filePanel.add(btnOpen, BorderLayout.EAST);

        JPanel otherPanel = new JPanel();
        GridBagConstraints gbc_otherPanel = new GridBagConstraints();
        gbc_otherPanel.fill = GridBagConstraints.BOTH;
        gbc_otherPanel.gridx = 0;
        gbc_otherPanel.gridy = 1;
        stream.add(otherPanel, gbc_otherPanel);
        GridBagLayout gbl_otherPanel = new GridBagLayout();
        gbl_otherPanel.columnWidths = new int[] {50, 100, 0};
        gbl_otherPanel.rowHeights = new int[] {0, 0, 0, 0};
        gbl_otherPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        gbl_otherPanel.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        otherPanel.setLayout(gbl_otherPanel);

        JLabel lblVolumeMultiplier = new JLabel("Volume multiplier");
        GridBagConstraints gbc_lblVolumeMultiplier = new GridBagConstraints();
        gbc_lblVolumeMultiplier.anchor = GridBagConstraints.EAST;
        gbc_lblVolumeMultiplier.insets = new Insets(0, 0, 5, 5);
        gbc_lblVolumeMultiplier.gridx = 0;
        gbc_lblVolumeMultiplier.gridy = 0;
        otherPanel.add(lblVolumeMultiplier, gbc_lblVolumeMultiplier);

        streamVol = new JSpinner();
        streamVol.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }
            if (machine.stream != null) {
                machine.stream.vol = (Float)streamVol.getValue();
                edit.flagChange();
            }
        });
        streamVol.setEnabled(true);
        streamVol.setModel(new SpinnerNumberModel(machine.stream != null ? machine.stream.vol : 0.0F, null, null, 0.1F));
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinner.insets = new Insets(0, 0, 5, 0);
        gbc_spinner.gridx = 1;
        gbc_spinner.gridy = 0;
        otherPanel.add(streamVol, gbc_spinner);

        JLabel lblPitchMultiplier = new JLabel("Pitch multiplier");
        GridBagConstraints gbc_lblPitchMultiplier = new GridBagConstraints();
        gbc_lblPitchMultiplier.anchor = GridBagConstraints.EAST;
        gbc_lblPitchMultiplier.insets = new Insets(0, 0, 5, 5);
        gbc_lblPitchMultiplier.gridx = 0;
        gbc_lblPitchMultiplier.gridy = 1;
        otherPanel.add(lblPitchMultiplier, gbc_lblPitchMultiplier);

        streamPitch = new JSpinner();
        streamPitch.addChangeListener(arg0 -> {
            if (init) {
                return;
            }
            if (eventBeingModified) {
                return;
            }
            if (machine.stream != null) {
                machine.stream.pitch = (Float)streamPitch.getValue();
                edit.flagChange();
            }
        });
        streamPitch.setEnabled(true);
        streamPitch.setModel(new SpinnerNumberModel(machine.stream != null ? machine.stream.pitch : 0F, null, null, 0.1F));
        GridBagConstraints gbc_spinner_2 = new GridBagConstraints();
        gbc_spinner_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinner_2.insets = new Insets(0, 0, 5, 0);
        gbc_spinner_2.gridx = 1;
        gbc_spinner_2.gridy = 1;
        otherPanel.add(streamPitch, gbc_spinner_2);

        chckbxIsLooping = new JCheckBox("Is looping");
        chckbxIsLooping.setEnabled(true);
        chckbxIsLooping.setSelected(machine.stream != null ? machine.stream.looping : false);
        chckbxIsLooping.addChangeListener(e -> {
            if (machine.stream != null) {
                machine.stream.looping = chckbxIsLooping.isSelected();
                edit.flagChange();
            }
        });
        GridBagConstraints gbc_chckbxIsLooping = new GridBagConstraints();
        gbc_chckbxIsLooping.anchor = GridBagConstraints.WEST;
        gbc_chckbxIsLooping.gridx = 1;
        gbc_chckbxIsLooping.gridy = 2;
        otherPanel.add(chckbxIsLooping, gbc_chckbxIsLooping);

        JPanel activationPanel = new JPanel();
        activationPanel.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Activation", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        add(activationPanel, BorderLayout.NORTH);
        GridBagLayout gbl_activationPanel = new GridBagLayout();
        gbl_activationPanel.columnWidths = new int[] {438, 0};
        gbl_activationPanel.rowHeights = new int[] {14, 10, 14, 10, 89, 0};
        gbl_activationPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
        gbl_activationPanel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        activationPanel.setLayout(gbl_activationPanel);

        JLabel lblMustBeActive = new JLabel("Any can be active (Power sources):");
        GridBagConstraints gbc_lblMustBeActive = new GridBagConstraints();
        gbc_lblMustBeActive.anchor = GridBagConstraints.WEST;
        gbc_lblMustBeActive.insets = new Insets(0, 0, 5, 0);
        gbc_lblMustBeActive.gridx = 0;
        gbc_lblMustBeActive.gridy = 0;
        activationPanel.add(lblMustBeActive, gbc_lblMustBeActive);

        activeSet = new SetRemoverPanel(this, machine.allow);
        GridBagConstraints gbc_activeSet = new GridBagConstraints();
        gbc_activeSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_activeSet.insets = new Insets(0, 0, 5, 0);
        gbc_activeSet.gridx = 0;
        gbc_activeSet.gridy = 1;
        activationPanel.add(activeSet, gbc_activeSet);

        JLabel lblMustBeInactive = new JLabel("None must be active (Jammers):");
        GridBagConstraints gbc_lblMustBeInactive = new GridBagConstraints();
        gbc_lblMustBeInactive.anchor = GridBagConstraints.WEST;
        gbc_lblMustBeInactive.insets = new Insets(0, 0, 5, 0);
        gbc_lblMustBeInactive.gridx = 0;
        gbc_lblMustBeInactive.gridy = 2;
        activationPanel.add(lblMustBeInactive, gbc_lblMustBeInactive);

        inactiveSet = new SetRemoverPanel(this, machine.restrict);
        GridBagConstraints gbc_inactiveSet = new GridBagConstraints();
        gbc_inactiveSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_inactiveSet.insets = new Insets(0, 0, 5, 0);
        gbc_inactiveSet.gridx = 0;
        gbc_inactiveSet.gridy = 3;
        activationPanel.add(inactiveSet, gbc_inactiveSet);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.anchor = GridBagConstraints.NORTH;
        gbc_panel.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 4;
        activationPanel.add(panel, gbc_panel);
        panel.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        list = new JList<>();
        list.setVisibleRowCount(4);
        scrollPane.setViewportView(list);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.EAST);
        panel_1.setLayout(new GridLayout(0, 1, 0, 0));

        JButton btnActive = new JButton("Active");
        btnActive.addActionListener(arg0 -> addToAllow());
        panel_1.add(btnActive);

        JButton btnInactive = new JButton("Inactive");
        btnInactive.addActionListener(arg0 -> addToRestrict());
        panel_1.add(btnInactive);

        updateValues();
        eventBeingModified = false;
        init = false;
    }

    protected void addEvent() {
        eventBeingModified = true;
        SerialMachineEvent sme = new SerialMachineEvent();
        machine.event.add(sme);
        event = sme;
        updateValues();
        changeToEvent(machine.event.size() - 1);
    }

    protected void removeEvent() {
        machine.event.remove(event);
        edit.flagChange();
        updateValues();
    }

    protected void changeToEvent(int selectedIndex) {
        eventBeingModified = true;
        timedEvents.setSelectedIndex(selectedIndex);
        event = machine.event.get(selectedIndex);
        fillEvents();
        eventBeingModified = false;
    }

    private void fillEvents() {
        if (machine.event == null) {
            return;
        }

        delayMin.setValue(event.delay_min);
        delayMax.setValue(event.delay_max);
        startDelay.setValue(event.delay_start);
        instantEvent.setText(event.event);

        volMod.setValue(event.vol_mod);
        pitchMod.setValue(event.pitch_mod);
    }

    private void updateValues() {
        delayIn.setValue(machine.delay_fadein);
        delayOut.setValue(machine.delay_fadeout);
        fadeIn.setValue(machine.fadein);
        fadeOut.setValue(machine.fadeout);

        if (machine.event != null) {
            List<String> data = new ArrayList<>();
            int acc = 1;
            for (SerialMachineEvent e : machine.event) {
                data.add("(" + acc + ") " + e.event);
                acc = acc + 1;
            }

            timedEvents.setListData(data.toArray(new String[data.size()]));
        }

        if (machine.stream != null) {
            machine.stream.vol = (Float)streamVol.getValue();
            machine.stream.pitch = (Float)streamPitch.getValue();
            machine.stream.looping = chckbxIsLooping.isSelected();
        }

        fillWithValues();
        activeSet.fillWithValues();
        inactiveSet.fillWithValues();

    }

    protected void addToAllow() {
        List<String> values = list.getSelectedValuesList();
        if (values.size() == 0) {
            return;
        }

        int addedCount = 0;
        for (Object o : values) {
            String value = (String)o;
            if (!machine.allow.contains(value)) {
                machine.allow.add(value);
                addedCount = addedCount + 1;
            }
        }

        if (addedCount > 0) {
            flagChange();
        }
    }

    protected void addToRestrict() {
        List<String> values = list.getSelectedValuesList();
        if (values.size() == 0) {
            return;
        }

        int addedCount = 0;
        for (Object o : values) {
            String value = (String)o;
            if (!machine.restrict.contains(value)) {
                machine.restrict.add(value);
                addedCount = addedCount + 1;
            }
        }

        if (addedCount > 0) {
            flagChange();
        }
    }

    // dag edit
    private void addStreamDialog() {
        OggFileChooser fc = new OggFileChooser(edit.getSoundDirectory());
        fc.setMultiSelectionEnabled(true);
        int returnValue = fc.showOpenDialog(this);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] files = fc.getSelectedFiles();
        if (files.length == 0) {
            return;
        }

        for (File file : files) {
            if (file != null && file.isFile() && file.exists()) {
                String path = new File(edit.getSoundDirectory().getAbsolutePath()).toURI().relativize(new File(file.getAbsolutePath()).toURI()).getPath();
                machine.stream = new SerialMachineStream();
                machine.stream.path = path;
                fileField.setText(path);
            }
        }
        edit.flagChange();
        updateValues();
    }

    private void fillWithValues() {
        Set<String> unused = new TreeSet<>(edit.getSerialRoot().set.keySet());
        unused.removeAll(machine.allow);
        unused.removeAll(machine.restrict);

        list.removeAll();
        list.setListData(unused.toArray(new String[unused.size()]));
    }

    @Override
    public void flagChange() {
        edit.flagChange();
        updateValues();
    }
}
