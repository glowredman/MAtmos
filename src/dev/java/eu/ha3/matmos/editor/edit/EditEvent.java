package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import eu.ha3.matmos.editor.filechooser.OggFileChooser;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;

@SuppressWarnings("serial")
public class EditEvent extends JPanel {
    
    private final EditPanel edit;
    private final SerialEvent event;
    
    private JSpinner volMin;
    private JSpinner volMax;
    
    private JSpinner pitchMin;
    private JSpinner pitchMax;
    
    private JSpinner distance;
    
    private JCheckBox chckbxIsStereoFile;

    private boolean init = true;
    private JTextArea files;

    public EditEvent(EditPanel parentConstruct, SerialEvent eventConstruct) {
        edit = parentConstruct;
        event = eventConstruct;
        setLayout(new BorderLayout(0, 0));

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(options, BorderLayout.SOUTH);
        options.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton btnTest = new JButton("Test sound in-game");
        btnTest.addActionListener(e -> edit.getModel().pushSound(event));
        options.add(btnTest);

        JPanel information = new JPanel();
        information.setBorder(new TitledBorder(
                null, "Important information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(information, BorderLayout.NORTH);

        JLabel lblSoundsPlayedEqually = new JLabel(
                "<html>Stereo sounds are always played equally on both ears.<br>Mono sounds will be played above the player if distance is 0.</html>");
        information.add(lblSoundsPlayedEqually);

        JPanel internal = new JPanel();
        internal.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(internal, BorderLayout.CENTER);
        internal.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        internal.add(panel, BorderLayout.NORTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] {50, 100, 100, 0};
        gbl_panel.rowHeights = new int[] {0, 0, 0, 0};
        gbl_panel.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblVolumeminmax = new JLabel("Volume (min/max)");
        GridBagConstraints gbc_lblVolumeminmax = new GridBagConstraints();
        gbc_lblVolumeminmax.anchor = GridBagConstraints.EAST;
        gbc_lblVolumeminmax.insets = new Insets(0, 0, 5, 5);
        gbc_lblVolumeminmax.gridx = 0;
        gbc_lblVolumeminmax.gridy = 0;
        panel.add(lblVolumeminmax, gbc_lblVolumeminmax);

        volMin = new JSpinner();
        volMin.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            event.vol_min = (Float)volMin.getValue();
            edit.flagChange();
        });
        volMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_volMin = new GridBagConstraints();
        gbc_volMin.fill = GridBagConstraints.HORIZONTAL;
        gbc_volMin.insets = new Insets(0, 0, 5, 5);
        gbc_volMin.gridx = 1;
        gbc_volMin.gridy = 0;
        panel.add(volMin, gbc_volMin);

        volMax = new JSpinner();
        volMax.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            event.vol_max = (Float)volMax.getValue();
            edit.flagChange();
        });
        volMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_volMax = new GridBagConstraints();
        gbc_volMax.fill = GridBagConstraints.HORIZONTAL;
        gbc_volMax.insets = new Insets(0, 0, 5, 0);
        gbc_volMax.gridx = 2;
        gbc_volMax.gridy = 0;
        panel.add(volMax, gbc_volMax);

        JLabel lblPitchminmax = new JLabel("Pitch (min/max)");
        GridBagConstraints gbc_lblPitchminmax = new GridBagConstraints();
        gbc_lblPitchminmax.anchor = GridBagConstraints.EAST;
        gbc_lblPitchminmax.insets = new Insets(0, 0, 5, 5);
        gbc_lblPitchminmax.gridx = 0;
        gbc_lblPitchminmax.gridy = 1;
        panel.add(lblPitchminmax, gbc_lblPitchminmax);

        pitchMin = new JSpinner();
        pitchMin.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            event.pitch_min = (Float)pitchMin.getValue();
            edit.flagChange();
        });
        pitchMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_pitchMin = new GridBagConstraints();
        gbc_pitchMin.fill = GridBagConstraints.HORIZONTAL;
        gbc_pitchMin.insets = new Insets(0, 0, 5, 5);
        gbc_pitchMin.gridx = 1;
        gbc_pitchMin.gridy = 1;
        panel.add(pitchMin, gbc_pitchMin);

        pitchMax = new JSpinner();
        pitchMax.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            event.pitch_max = (Float)pitchMax.getValue();
            edit.flagChange();
        });
        pitchMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
        GridBagConstraints gbc_pitchMax = new GridBagConstraints();
        gbc_pitchMax.fill = GridBagConstraints.HORIZONTAL;
        gbc_pitchMax.insets = new Insets(0, 0, 5, 0);
        gbc_pitchMax.gridx = 2;
        gbc_pitchMax.gridy = 1;
        panel.add(pitchMax, gbc_pitchMax);

        JLabel lblDistance = new JLabel("Distance");
        GridBagConstraints gbc_lblDistance = new GridBagConstraints();
        gbc_lblDistance.anchor = GridBagConstraints.EAST;
        gbc_lblDistance.insets = new Insets(0, 0, 0, 5);
        gbc_lblDistance.gridx = 0;
        gbc_lblDistance.gridy = 2;
        panel.add(lblDistance, gbc_lblDistance);

        distance = new JSpinner();
        distance.addChangeListener(arg0 -> {
            if (init) {
                return;
            }

            event.distance = (Integer)distance.getValue();
            chckbxIsStereoFile.setSelected(event.distance <= 0);
            edit.flagChange();
        });
        distance.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
        GridBagConstraints gbc_distance = new GridBagConstraints();
        gbc_distance.fill = GridBagConstraints.HORIZONTAL;
        gbc_distance.insets = new Insets(0, 0, 0, 5);
        gbc_distance.gridx = 1;
        gbc_distance.gridy = 2;
        panel.add(distance, gbc_distance);

        chckbxIsStereoFile = new JCheckBox("0 = Is stereo file");
        chckbxIsStereoFile.setEnabled(false);
        GridBagConstraints gbc_chckbxIsStereoFile = new GridBagConstraints();
        gbc_chckbxIsStereoFile.fill = GridBagConstraints.HORIZONTAL;
        gbc_chckbxIsStereoFile.gridx = 2;
        gbc_chckbxIsStereoFile.gridy = 2;
        panel.add(chckbxIsStereoFile, gbc_chckbxIsStereoFile);

        JPanel panel_1 = new JPanel();
        internal.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane);

        files = new JTextArea();
        files.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                updatedFiles();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                updatedFiles();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                updatedFiles();
            }
        });
        scrollPane.setViewportView(files);

        JButton btnAddSounds = new JButton("Add sounds...");
        btnAddSounds.addActionListener(arg0 -> addSoundDialog());
        internal.add(btnAddSounds, BorderLayout.SOUTH);

        updateValues();
        init = false;
    }

    protected void updatedFiles() {
        if (EditEvent.this.init) {
            return;
        }

        List<String> paths = new ArrayList<>();
        String[] lines = files.getText().split("[" + System.getProperty("line.separator") + "]");
        for (String line : lines) {
            if (!line.equals("")) {
                paths.add(line);
            }
        }

        event.path.clear();
        event.path.addAll(paths);
        edit.flagChange();
    }

    protected void addSoundDialog() {
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
                String path = new File(edit.getSoundDirectory().getAbsolutePath())
                        .toURI().relativize(new File(file.getAbsolutePath()).toURI()).getPath();
                event.path.add(path);
            }
        }
        edit.flagChange();
        updateValues();

    }

    private void updateValues() {
        volMin.setValue(event.vol_min);
        volMax.setValue(event.vol_max);
        pitchMin.setValue(event.pitch_min);
        pitchMax.setValue(event.pitch_max);
        distance.setValue(event.distance);
        chckbxIsStereoFile.setSelected(event.distance <= 0);

        StringBuilder paths = new StringBuilder();
        for (String path : event.path) {
            paths.append(path);
            paths.append(System.getProperty("line.separator"));
        }
        files.setText(paths.toString());
    }
}
