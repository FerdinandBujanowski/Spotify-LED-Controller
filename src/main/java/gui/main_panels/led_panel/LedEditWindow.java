package gui.main_panels.led_panel;

import control.led.*;

import javax.swing.*;
import java.awt.*;

public class LedEditWindow extends JPanel implements LedGraphicUnit {

    private LedRequestAcceptor ledControl;
    private boolean drawOnlyLedPixels;

    private JPanel layersPanel;
    private JPanel pixelPanel;


    public LedEditWindow(LedRequestAcceptor ledControl, Dimension windowDimension) {
        super(null);

        this.ledControl = ledControl;
        this.ledControl.setLedGraphicUnit(this);

        this.drawOnlyLedPixels = false;

        this.layersPanel = new JPanel(null);
        this.layersPanel.setOpaque(true);
        this.layersPanel.setBackground(new Color(40, 40, 40));

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
    public void addLayer() {

    }
}
