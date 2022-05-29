package gui.main_panels.node_panel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class MaskPanel extends JPanel {

    private final Function<Integer, Double[][]> getValuesFunction;
    private final Function<Integer, Color[][]> getColorsFunction;

    public MaskPanel(Function<Integer, Double[][]> valuesFunction, Function<Integer, Color[][]> colorsFunction) {
        this.getValuesFunction = valuesFunction;
        this.getColorsFunction = colorsFunction;

        this.setOpaque(true);
        this.setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Double[][] values = this.getValuesFunction.apply(0);
        Color[][] colors = this.getColorsFunction.apply(0);
        double step = this.getWidth() / (double)values.length;

        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values[i].length; j++) {
                double intensity = values[i][j];
                Color color = colors.length == values.length ? colors[i][j] : Color.WHITE;
                try {
                    g.setColor(new Color(
                            (int)Math.round(intensity * color.getRed()),
                            (int)Math.round(intensity * color.getGreen()),
                            (int)Math.round(intensity * color.getBlue())
                    ));
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
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
