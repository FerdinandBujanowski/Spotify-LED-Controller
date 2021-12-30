package gui.main_panels.event_panel;

import control.song.SongControl;

import javax.swing.*;
import java.awt.*;

public class EventEditWindow extends JPanel {

    private SongControl songControl;

    public EventEditWindow(SongControl songControl) {
        super(null);

        this.songControl = songControl;
        this.setOpaque(true);
        this.setBackground(new Color(111, 159, 152));
    }
}
