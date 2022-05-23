package gui.main_panels.led_panel;

import control.led.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LedEditWindow extends JPanel implements LedGraphicUnit {

    private LedRequestAcceptor ledControl;
    private boolean drawOnlyLedPixels;

    private LayersPanel layersPanel;
    private JPanel pixelPanel;


    public LedEditWindow(LedRequestAcceptor ledControl, Dimension windowDimension) {
        super(null);

        this.ledControl = ledControl;
        this.ledControl.setLedGraphicUnit(this);

        this.drawOnlyLedPixels = false;

        this.layersPanel = new LayersPanel(ledControl, this);

        this.pixelPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //super.paintComponent(g);

                Point[] pixels = ledControl.getPixels();
                int finalDegree = ledControl.getFinalDegree();
                int pixelLength = (finalDegree * 2) + 1;
                double step = this.getWidth() / (double)pixelLength;

                if(!drawOnlyLedPixels) {
                    for(int i = -finalDegree; i <= finalDegree; i++) {
                        for(int j = -finalDegree; j <= finalDegree; j++) {
                            g.setColor(ledControl.getColorAt(i, j));
                            g.fillRect(
                                    (int)Math.round((i + finalDegree) * step),
                                    (int)Math.round((j + finalDegree) * step),
                                    (int)Math.round(step), (int)Math.round(step)
                            );
                        }
                    }
                }
                for(Point pixel : pixels) {
                    if(drawOnlyLedPixels) {
                        g.setColor(ledControl.getColorAt(pixel.x, pixel.y));
                        g.fillRect(
                                (int)Math.round((pixel.x + finalDegree) * step),
                                (int)Math.round((pixel.y + finalDegree) * step),
                                (int)Math.round(step) - 1, (int)Math.round(step) - 1
                        );
                    }
                    g.setColor(Color.WHITE);
                    g.drawRect(
                            (int)Math.round((pixel.x + finalDegree) * step),
                            (int)Math.round((pixel.y + finalDegree) * step),
                            (int)Math.round(step) - 1, (int)Math.round(step) - 1
                    );
                }
            }
        };
        this.add(this.pixelPanel);
        this.pixelPanel.setBounds(
                ((windowDimension.width - 200) / 2) - 200,
                30,
                400, 400
        );

        this.setOpaque(true);
        this.setBackground(new Color(40, 40, 40));
    }

    public JPanel getLayersPanel() {
        return this.layersPanel;
    }

    @Override
    public void addLayer(int newIndex) {
        this.layersPanel.addLayer(newIndex);
        this.layersPanel.repaint();
        this.repaint();
    }

    @Override
    public void update() {
        this.repaint();
    }
}

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
        this.setBackground(new Color(40, 40, 40));
    }

    public void addLayer(int newIndex) {
        JLabel newLayerLabel = new JLabel("Layer " + (newIndex + 1), SwingConstants.CENTER);
        newLayerLabel.setForeground(Color.WHITE);
        this.layerLabels.add(newLayerLabel);
        this.add(newLayerLabel);

        JCheckBox newLayerCheckBox = new JCheckBox();
        newLayerCheckBox.setSelected(true);
        newLayerCheckBox.setBackground(new Color(40, 40, 40));
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

        for(int i = 1; i <= this.layerLabels.size(); i++) {
            this.layerLabels.get(this.layerLabels.size() - i).setBounds(50, (i - 1) * 30, 100, 30);
            this.layerCheckBoxes.get(this.layerCheckBoxes.size() - i).setBounds(50, (i - 1) * 30 + 5, 20, 20);
        }
    }

}
