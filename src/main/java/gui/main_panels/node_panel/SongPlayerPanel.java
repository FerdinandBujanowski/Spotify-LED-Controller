package gui.main_panels.node_panel;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Track;
import control.spotify.SpotifyWebHandler;
import gui.main_panels.player_panel.SpotifyPlayerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Icons by: "https://www.flaticon.com/authors/roundicons"
 */

public class SongPlayerPanel extends JPanel {

    private SpotifyPlayerPanel spotifyPlayerPanel;
    private JLabel imageLabel;

    public SongPlayerPanel(SpotifyPlayerPanel spotifyPlayerPanel) {
        this.spotifyPlayerPanel = spotifyPlayerPanel;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setOpaque(true);
        //this.setBackground(Color.DARK_GRAY);

        this.imageLabel = new JLabel();
        this.imageLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        PlayButtonsPanel playButtonsPanel = new PlayButtonsPanel(this.spotifyPlayerPanel, new Dimension(300, 100));
        playButtonsPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        this.add(Box.createVerticalStrut(100));
        this.add(imageLabel);
        this.add(playButtonsPanel);
        //this.add(Box.createVerticalGlue());
    }

    public void updatePlayImage() {
        CurrentlyPlayingContext currentlyPlayingContext = this.spotifyPlayerPanel.onGetCurrentPlayingTrack();
        BufferedImage songImage = null;
        try {
            if(currentlyPlayingContext != null) {
                songImage = ImageIO.read(new URL(((Track)currentlyPlayingContext.getItem()).getAlbum().getImages()[0].getUrl()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.imageLabel .setIcon(new ImageIcon(getScaledImage(songImage, 300, 300)));
    }
    @Override
    public void paintComponent(Graphics g) {

    }

    public static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}

class PlayButtonsPanel extends JPanel {

    private SpotifyPlayerPanel spotifyPlayerPanel;

    public PlayButtonsPanel(SpotifyPlayerPanel spotifyPlayerPanel, Dimension dimension) {
        this.spotifyPlayerPanel = spotifyPlayerPanel;
        this.setLayout(new GridLayout(1, 3));
        this.setMaximumSize(dimension);
        this.setOpaque(true);
        this.setBackground(new Color(24, 24, 24));

        int buttonWidth = (dimension.width / 3);
        int buttonHeight = dimension.height;

        Icon previousIcon = null;
        Icon playIcon = null;
        Icon pauseIcon = null;
        Icon nextIcon = null;

        Icon previousIconHover = null;
        Icon playIconHover = null;
        Icon pauseIconHover = null;
        Icon nextIconHover = null;
        try {
            previousIcon = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\previous.png")));
            playIcon = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\play.png")));
            pauseIcon = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\pause.png")));
            nextIcon = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\next.png")));

            previousIconHover = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\previous_hover.png")));
            playIconHover = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\play_hover.png")));
            pauseIconHover = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\pause_hover.png")));
            nextIconHover = new ImageIcon(ImageIO.read(new File("images\\icon\\player_icons\\next_hover.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel skipLeftButton = new JLabel(previousIcon);
        skipLeftButton.setSize(buttonWidth, buttonHeight);
        JLabel playPauseButton = new JLabel(playIcon);
        playPauseButton.setSize(buttonWidth, buttonHeight);
        JLabel skipRightButton = new JLabel(nextIcon);
        skipRightButton.setSize(buttonWidth, buttonHeight);

        this.add(skipLeftButton);
        this.add(playPauseButton);
        this.add(skipRightButton);

        Icon finalPreviousIcon = previousIcon;
        Icon finalPreviousIconHover = previousIconHover;
        skipLeftButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO: Previous in Playlist
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                skipLeftButton.setIcon(finalPreviousIconHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                skipLeftButton.setIcon(finalPreviousIcon);
            }
        });

        Icon finalPlayIcon = playIcon;
        Icon finalPlayIconHover = playIconHover;
        Icon finalPauseIcon = pauseIcon;
        Icon finalPauseIconHover = pauseIconHover;
        playPauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO: Play/Pause Playback, control LEDs
                if(playPauseButton.getIcon() == finalPlayIconHover) {
                    playPauseButton.setIcon(finalPauseIconHover);
                    spotifyPlayerPanel.onResumePlayback();
                } else {
                    playPauseButton.setIcon(finalPlayIconHover);
                    spotifyPlayerPanel.onPausePlayback();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if(playPauseButton.getIcon() == finalPlayIcon) {
                    playPauseButton.setIcon(finalPlayIconHover);
                } else {
                    playPauseButton.setIcon(finalPauseIconHover);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if(playPauseButton.getIcon() == finalPlayIconHover) {
                    playPauseButton.setIcon(finalPlayIcon);
                } else {
                    playPauseButton.setIcon(finalPauseIcon);
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
