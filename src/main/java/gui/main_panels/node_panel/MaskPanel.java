package gui.main_panels.node_panel;

import control.save.JsonWriter;
import gui.Dialogues;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.function.Function;

public class MaskPanel extends JPanel {

    private final Function<Integer, Double[][]> getValuesFunction;
    private final Function<Integer, Color[][]> getColorsFunction;

    public MaskPanel(Function<Integer, Double[][]> valuesFunction, Function<Integer, Color[][]> colorsFunction) {
        this.getValuesFunction = valuesFunction;
        this.getColorsFunction = colorsFunction;

        this.setOpaque(true);
        this.setBackground(Color.BLACK);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu rightClickPopupMenu = new JPopupMenu();
                    JMenuItem exportMaskItem = new JMenuItem("Export Mask");
                    rightClickPopupMenu.add(exportMaskItem);
                    rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    exportMaskItem.addActionListener(event -> {
                        JFileChooser fileSaveChooser = Dialogues.getDefaultFileSaveChooser();
                        FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                        fileSaveChooser.setFileFilter(serializedFilter);
                        fileSaveChooser.setSelectedFile(new File("mask.json"));
                        int returnValue = fileSaveChooser.showSaveDialog(getParent());
                        if(returnValue == JFileChooser.APPROVE_OPTION) {
                            Double[][] values = getValuesFunction.apply(0);
                            JsonWriter.writeMaskToFile(values, fileSaveChooser.getSelectedFile().getPath());
                        }
                    });
                }
            }
        });
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
                if((int)Math.round(step) > 3) {
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
}
