package gui.main_panels.event_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrackLabel extends JLabel {

    public TrackLabel() {
        this.setOpaque(true);
        this.setBackground(new Color(36, 52, 86, 255));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("Test");
            }
        });
    }


}
