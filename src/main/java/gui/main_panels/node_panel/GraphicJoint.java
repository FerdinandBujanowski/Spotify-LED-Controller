package gui.main_panels.node_panel;

import control.node.NodeControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphicJoint extends JPanel {

    private Color color;
    private boolean hover;

    public GraphicJoint(Color color) {
        this.setSize(NodeControl.JOINT_WIDTH, NodeControl.JOINT_WIDTH);
        this.color = color;
        this.hover = false;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
                getParent().repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
                getParent().repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {

                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ((ParentNodePanel)getParent()).onMouseMoved(new Point(getX() + e.getX(), getY() + e.getY()));
            }
        });
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(this.hover ? Color.WHITE : this.color);
        g.fillOval(0, 0, this.getWidth(), this.getHeight());
        g.setColor(g.getColor().darker());
        g.drawOval(0, 0, this.getWidth(), this.getHeight());
    }

    public Color getColor() {
        return(hover ? Color.WHITE : this.color);
    }
}
