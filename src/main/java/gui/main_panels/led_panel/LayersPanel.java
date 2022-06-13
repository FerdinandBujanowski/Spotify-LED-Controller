package gui.main_panels.led_panel;

import control.led.LedGraphicUnit;
import control.led.LedRequestAcceptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class LayersPanel extends JPanel {

    private LedRequestAcceptor ledRequestAcceptor;
    private LedGraphicUnit ledGraphicUnit;

    private ArrayList<JLabel> layerLabels;
    private ArrayList<JCheckBox> layerCheckBoxes;

    public LayersPanel(LedRequestAcceptor ledRequestAcceptor, LedGraphicUnit ledGraphicUnit) {
        super(null);

        this.ledRequestAcceptor = ledRequestAcceptor;
        this.ledGraphicUnit = ledGraphicUnit;
        this.layerLabels = new ArrayList<>();
        this.layerCheckBoxes = new ArrayList<>();

        this.setOpaque(true);
        this.setBackground(new Color(25, 20, 20));
    }

    public void addLayer(int newIndex) {
        JLabel newLayerLabel = new JLabel("Layer " + (newIndex + 1), SwingConstants.CENTER);
        newLayerLabel.setForeground(Color.WHITE);
        this.layerLabels.add(newLayerLabel);
        this.add(newLayerLabel);

        JCheckBox newLayerCheckBox = new JCheckBox();
        newLayerCheckBox.setSelected(true);
        newLayerCheckBox.setBackground(new Color(25, 20, 20));
        this.layerCheckBoxes.add(newLayerCheckBox);
        this.add(newLayerCheckBox);

        newLayerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : Layer wird sichtbar / unsichtbar gemacht
                ledRequestAcceptor.enableLayer(newIndex, newLayerCheckBox.isSelected());
            }
        });

        this.setPreferredSize(new Dimension(this.getWidth(), this.layerLabels.size() * 30));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 1; i <= this.layerLabels.size(); i++) {
            this.layerLabels.get(this.layerLabels.size() - i).setBounds(50, (i - 1) * 30, 100, 30);
            this.layerCheckBoxes.get(this.layerCheckBoxes.size() - i).setBounds(50, (i - 1) * 30 + 5, 20, 20);
        }
    }

}
