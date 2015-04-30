package fr.viveris.xlsdiff.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.viveris.xlsdiff.utils.Exceptions;

public final class GUIUtils {
    private static final Logger LOGGER             = LoggerFactory
                                                           .getLogger(GUIUtils.class);
    private static boolean      lookAndFeelApplied = false;

    public abstract static class ExceptionRunnable {
        public void before() {
        }

        public abstract void run() throws Exception;

        public void after() {
        }
    }

    private GUIUtils() {
    }

    public static void applyLookAndFeel() {
        if (lookAndFeelApplied) {
            return;
        }

        final Collection<Class<NimbusLookAndFeel>> lookAndFeels = Arrays
                .asList(NimbusLookAndFeel.class);

        for (final Class<? extends BasicLookAndFeel> lookAndFeel : lookAndFeels) {
            try {
                UIManager.setLookAndFeel(lookAndFeel.newInstance());
                break;
            } catch (final Exception e) {
            }
        }

        lookAndFeelApplied = true;
    }

    public static void centerOnScreen(final Component frame) {
        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
        final Dimension frameSize = frame.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        frame.setSize(frameSize);
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    public static void centerOn(final Container parent,
            final Component component) {
        final Dimension parentSize = parent.getSize();
        final Dimension componentSize = component.getSize();

        if (componentSize.height > parentSize.height) {
            componentSize.height = parentSize.height;
        }

        if (componentSize.width > parentSize.width) {
            componentSize.width = parentSize.width;
        }

        component.setSize(componentSize);
        component.setLocation((parentSize.width - componentSize.width) / 2,
                (parentSize.height - componentSize.height) / 2);
    }

    public static void centerOnParent(final Component component) {
        centerOn(component.getParent(), component);
    }

    public static void leftOnParent(final Component component) {
        final Component parent = component.getParent();
        final Dimension parentSize = parent.getSize();
        final Dimension componentSize = component.getSize();

        componentSize.height = parentSize.height;
        if (componentSize.width > parentSize.width) {
            componentSize.width = parentSize.width;
        }

        component.setSize(componentSize);
        component.setLocation(0, 0);
    }

    public static void info(final String message) {
        JOptionPane.showMessageDialog(null, message, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static Integer confirm(final String message) {
        return JOptionPane.showConfirmDialog(null, message, "Confirm",
                JOptionPane.YES_NO_OPTION);
    }

    public static void warn(final String message) {
        JOptionPane.showMessageDialog(null, message, "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void error(final String message) {
        JOptionPane.showMessageDialog(null, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void error(final Throwable e) {
        error("An error occured: " + e.getLocalizedMessage());
    }

    public static void doInBackground(final ExceptionRunnable runnable) {
        final WaitingDialog waitingDialog = new WaitingDialog();
        final Thread worker = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.before();
                    runnable.run();
                    waitingDialog.dispose();
                    runnable.after();
                } catch (final Exception e) {
                    LOGGER.error("Error while doing in background", e);
                    error(e);
                }
                waitingDialog.dispose();
            }
        };
        worker.start();
        waitingDialog.setVisible(true);
    }

    public static Icon getIcon(final String string) {
        try {
            return new ImageIcon(ImageIO.read(GUIUtils.class
                    .getResource("/icons/" + string)));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void buildExceptionsDisplay(final Exceptions e) {
        final JDialog errorDialog = new JDialog();
        final DefaultListModel<String> listModel = new DefaultListModel<>();
        final JList<String> errorList = new JList<>(listModel);
        final JScrollPane scrollPane = new JScrollPane();
        final JPanel panel = new JPanel();
        final JButton close = new JButton("Fermer");
        final JButton export = new JButton("Export log");

        errorDialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        errorDialog.setBounds(100, 100, 600, 600);
        GUIUtils.centerOnScreen(errorDialog);

        errorDialog.getContentPane().setLayout(new BorderLayout(0, 0));
        errorDialog.getContentPane().add(panel, BorderLayout.SOUTH);
        final GridBagLayout gblPanel = new GridBagLayout();
        gblPanel.columnWidths = new int[] { 515, 100, 100, 0 };
        gblPanel.rowHeights = new int[] { 23, 0 };
        gblPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gblPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gblPanel);
        {
            final GridBagConstraints gbcClose = new GridBagConstraints();
            gbcClose.insets = new Insets(5, 5, 5, 5);
            gbcClose.fill = GridBagConstraints.BOTH;
            gbcClose.anchor = GridBagConstraints.EAST;
            gbcClose.gridx = 2;
            gbcClose.gridy = 0;
            panel.add(close, gbcClose);
            close.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    errorDialog.dispose();
                }
            });
        }
        {
            final GridBagConstraints gbcExport = new GridBagConstraints();
            gbcExport.fill = GridBagConstraints.BOTH;
            gbcExport.insets = new Insets(5, 5, 5, 5);
            gbcExport.anchor = GridBagConstraints.EAST;
            gbcExport.gridx = 1;
            gbcExport.gridy = 0;
            panel.add(export, gbcExport);
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final JFileChooser fileChooser = new fr.viveris.xlsdiff.gui.JFileChooser();
                    fileChooser.setDialogTitle("Export log file");
                    fileChooser.setFileFilter(new FileNameExtensionFilter(
                            "Log file (*.log)", "log"));
                    final int returnVal = fileChooser
                            .showSaveDialog(errorDialog);
                    final File tmpFile = fileChooser.getSelectedFile();

                    if (returnVal != JFileChooser.APPROVE_OPTION
                            || tmpFile == null) {
                        return;
                    }

                    final File file;
                    if (tmpFile.getAbsolutePath().endsWith(".log")) {
                        file = new File(tmpFile.getAbsolutePath());
                    } else {
                        file = new File(tmpFile.getAbsolutePath() + ".log");
                    }

                    final String userName;
                    if (System.getProperty("project.username") != null) {
                        userName = System.getProperty("project.username");
                    } else {
                        userName = "user";
                    }

                    try (final OutputStream stream = new FileOutputStream(file)) {
                        final StringBuilder sb = new StringBuilder(
                                "Log file generated on ")
                                .append(new DateTime()
                                        .toString("yyyy/MM/dd HH:mm:ss"))
                                .append(" by user : ").append(userName)
                                .append(".\r\n");
                        for (int i = 0; i < errorList.getModel().getSize(); i++) {
                            sb.append(errorList.getModel().getElementAt(i))
                                    .append("\r\n");
                        }
                        IOUtils.write(sb.toString(), stream);

                        JOptionPane.showMessageDialog(null,
                                "Log file exported.", "Information",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (final IOException ex) {
                        error(ex);
                    }
                }
            });
        }
        {
            errorDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        }

        scrollPane.setViewportView(errorList);

        for (final Exception exception : e) {
            listModel.addElement(exception.getMessage());
        }

        errorDialog.setVisible(true);
    }
}
