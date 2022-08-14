package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;

public abstract class Dialogues {

    public static int getSelectedOptionFromArray(Object[] items, String messageString, int currentSelection) {
        JComboBox comboBox = new JComboBox(items);
        comboBox.setSelectedIndex(currentSelection);
        JOptionPane.showOptionDialog(null, comboBox, messageString, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        return comboBox.getSelectedIndex();
    }

    public static double getNumberValue(String message) {
        JFormattedTextField numberTextField = new JFormattedTextField(NumberFormat.getNumberInstance());
        numberTextField.setValue(0.0);
        numberTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        JOptionPane.showOptionDialog(
                null, numberTextField, message,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
        );
        if(numberTextField.getValue().getClass() == Long.class) {
            return ((Long) numberTextField.getValue()).doubleValue();
        } else {
            return (Double) numberTextField.getValue();
        }
    }

    public static int getIntegerValue(String message) {
        JFormattedTextField numberTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        numberTextField.setValue(0);
        numberTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        JOptionPane.showOptionDialog(
                null, numberTextField, message,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
        );
        if(numberTextField.getValue().getClass() == Long.class) {
            return ((Long) numberTextField.getValue()).intValue();
        } else {
            return (Integer) numberTextField.getValue();
        }
    }

    public static JFileChooser getDefaultFileSaveChooser() {

        return new JFileChooser() {
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "File already exists, overwrite?", "Existing File",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                        default:
                            return;
                    }
                }
                super.approveSelection();
            }
        };
    }

    public static String getJsonChooserFile(Component parent, String message) {
        JFileChooser fileOpenChooser = new JFileChooser(message);
        FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
        fileOpenChooser.setFileFilter(serializedFilter);
        int returnValue = fileOpenChooser.showOpenDialog(parent);
        if(returnValue == JFileChooser.APPROVE_OPTION) return fileOpenChooser.getSelectedFile().getPath();
        else return "";
    }
}
