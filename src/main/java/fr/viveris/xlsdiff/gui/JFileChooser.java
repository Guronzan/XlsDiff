package fr.viveris.xlsdiff.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

public class JFileChooser extends javax.swing.JFileChooser {
    private static final long serialVersionUID = 1L;
    private static File       lastDirectory    = null;

    public JFileChooser() {
        setCurrentDirectory(lastDirectory);
    }

    @Override
    public int showDialog(final Component parent, final String approveButtonText)
            throws HeadlessException {
        final int value = super.showDialog(parent, approveButtonText);
        if (value == APPROVE_OPTION) {
            lastDirectory = getSelectedFile();
        }
        return value;
    }
}
