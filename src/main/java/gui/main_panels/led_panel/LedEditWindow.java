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

        this.layersPanel = new LayersPanel(this);

        this.pixelPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Point[] pixels = ledControl.getPixels();
                int finalDegree = ledControl.getFinalDegree();
                int pixelLength = (finalDegree * 2) + 1;
                int step = (int)Math.round(this.getWidth() / (double)pixelLength);

                if(!drawOnlyLedPixels) {
                    for(int i = -finalDegree; i <= finalDegree; i++) {
                        for(int j = -finalDegree; j <= finalDegree; j++) {
                            g.setColor(ledControl.getColorAt(i, j));
                            g.fillRect(
                                    (i + finalDegree) * step,
                                    (j + finalDegree) * step,
                                    step, step
                            );
                        }
                    }
                }
                for(Point pixel : pixels) {
                    if(drawOnlyLedPixels) {
                        g.setColor(ledControl.getColorAt(pixel.x, pixel.y));
                        g.fillRect(
                                (pixel.x + finalDegree) * step,
                                (pixel.y + finalDegree) * step,
                                step, step
                        );
                    }
                    g.setColor(Color.WHITE);
                    g.drawRect(
                            (pixel.x + finalDegree) * step,
                            (pixel.y + finalDegree) * step,
                            step, step
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
}

class LayersPanel extends JPanel {


    private LedGraphicUnit ledGraphicUnit;

    private ArrayList<JLabel> layerLabels;
    private ArrayList<JCheckBox> layerCheckBoxes;

    public LayersPanel(LedGraphicUnit ledGraphicUnit) {
        super(null);

        this.ledGraphicUnit = ledGraphicUnit;
        this.layerLabels = new ArrayList<>();
        this.layerCheckBoxes = new ArrayList<>();

        this.setOpaque(true);
        this.setBackground(new Color(40, 40, 40));
    }

    public void addLayer(int newIndex) {
        JLabel newLayerLabel = new JLabel("Layer " + newIndex, SwingConstants.CENTER);
        newLayerLabel.setForeground(Color.WHITE);
        newLayerLabel.setBounds(50, this.layerLabels.size() * 30, 100, 30);
        this.layerLabels.add(newLayerLabel);
        this.add(newLayerLabel);

        JCheckBox newLayerCheckBox = new JCheckBox();
        newLayerCheckBox.setSelected(true);
        newLayerCheckBox.setBounds(50, this.layerCheckBoxes.size() * 30 + 5, 20, 20);
        newLayerCheckBox.setBackground(new Color(40, 40, 40));
        this.layerCheckBoxes.add(newLayerCheckBox);
        this.add(newLayerCheckBox);

        newLayerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : Layer wird sichtbar / unsichtbar gemacht

            }
        });

        this.setPreferredSize(new Dimension(this.getWidth(), this.layerLabels.size() * 30));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
