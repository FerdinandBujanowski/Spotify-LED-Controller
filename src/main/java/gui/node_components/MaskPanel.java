package gui.node_components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class MaskPanel extends JPanel {

    private final Function<Integer, Double[][]> getValuesFunction;

    public MaskPanel(Function<Integer, Double[][]> function) {
        this.getValuesFunction = function;

        this.setOpaque(true);
        this.setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Double[][] values = this.getValuesFunction.apply(0);
        double step = this.getWidth() / (double)values.length;

        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values.length; j++) {
                int intensity = (int)Math.round(values[i][j] * 255);
                g.setColor(new Color(intensity, intensity, intensity));
                g.fillRect(
                        (int)Math.round(step * i),
                        (int)Math.round(step * j),
                        (int)Math.round(step),
                        (int)Math.round(step)
                );
                g.setColor(Color.BLACK);
                g.drawRect(
                        (int)Math.round(step * i),
                        (int)Math.round(step * j),
                        (int)Math.round(step),
                        (int)Math.round(step)
                );
            }
        }
    }
}
