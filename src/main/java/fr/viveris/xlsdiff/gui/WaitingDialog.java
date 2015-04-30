package fr.viveris.xlsdiff.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class WaitingDialog extends JDialog {
    private static final long  serialVersionUID = 1L;
    private final JProgressBar progressBar      = new JProgressBar();
    private boolean            disposed         = false;

    public WaitingDialog() {
        setTitle("Please wait, data extraction in progress...");
        this.setBounds(100, 100, 600, 60);
        GUIUtils.centerOnScreen(this);
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        {
            this.progressBar.setIndeterminate(true);
            getContentPane().add(this.progressBar, BorderLayout.CENTER);
        }
    }

    @Override
    public void setVisible(final boolean b) {
        if (this.disposed) {
            return;
        }
        super.setVisible(b);
    }

    @Override
    public void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        super.dispose();
    }
}
