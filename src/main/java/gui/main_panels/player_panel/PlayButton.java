package gui.main_panels.player_panel;

import control.song.SongControl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayButton extends JButton {

    private SongControl songControl;
    final ImageIcon playIcon;
    final ImageIcon pauseIcon;

    public PlayButton(SongControl songControl) {
        this.songControl = songControl;

        BufferedImage playImage = null;
        try {
            playImage = ImageIO.read(new File("images\\icon\\play.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.playIcon = new ImageIcon(SpotifyPlayerPanel.getScaledImage(playImage, 20, 20));

        BufferedImage pauseImage = null;
        try {
            pauseImage = ImageIO.read(new File("images\\icon\\pause.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.pauseIcon = new ImageIcon(SpotifyPlayerPanel.getScaledImage(pauseImage, 20, 20));

        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.onTogglePlayback();
            }
        });
    }

    public void updateIcon() {
        if(this.songControl.isSongPaused()) {
            this.setIcon(this.playIcon);
        } else {
            this.setIcon(this.pauseIcon);
        }
    }
}
