package eu.ha3.matmos.editor;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InstantTextField extends JTextField {
    private static final long serialVersionUID = 1L;

    public InstantTextField() {
        super();

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                editEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                editEvent();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                editEvent();
            }
        });
    }

    protected void editEvent() {
    }
}
