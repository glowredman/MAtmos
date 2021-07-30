package eu.ha3.matmos.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import eu.ha3.matmos.editor.interfaces.Editor;

public class JsonPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private Editor model;

    private JTextArea textArea;

    public JsonPanel(Editor modelConstruct) {
        model = modelConstruct;

        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.SOUTH);

        JButton btnGeneratePretty = new JButton("Generate");
        btnGeneratePretty.addActionListener(arg0 -> textArea.setText(model.generateJson(true)));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(btnGeneratePretty);

        JButton btnGenerateMinified = new JButton("Minify");
        btnGenerateMinified.addActionListener(arg0 -> textArea.setText(model.generateJson(false)));
        panel.add(btnGenerateMinified);

        JButton btnCopyToClipboard = new JButton("Copy to Clipboard");
        btnCopyToClipboard.addActionListener(e -> {
            try {
                StringSelection selection = new StringSelection(textArea.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        panel.add(btnCopyToClipboard);
    }
}
