package gui.main_panels.player_panel;

import control.song.SongRequestAcceptor;

import javax.swing.*;

public class PlaySlider extends JSlider {

    private SongRequestAcceptor songControl;

    public PlaySlider(SongRequestAcceptor songControl) {
        super(0, 1000, 0);

        this.songControl = songControl;

    }
}
