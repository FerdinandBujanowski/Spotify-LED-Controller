package gui;

import javax.swing.*;
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
}
