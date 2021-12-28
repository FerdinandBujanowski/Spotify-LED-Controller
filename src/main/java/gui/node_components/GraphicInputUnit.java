package gui.node_components;

import javax.swing.*;
import java.awt.*;

public class GraphicInputUnit extends JPanel {

    private JComponent jComponent;

    public GraphicInputUnit(JComponent jComponent) {
        super(null);
        this.jComponent = jComponent;
        this.add(jComponent);
        this.setOpaque(true);
        this.setBackground(Color.RED);
    }

    public JComponent getComponent() {
        return this.jComponent;
    }
    public int getPreferredWidth() {
        return 0;
    }
}
