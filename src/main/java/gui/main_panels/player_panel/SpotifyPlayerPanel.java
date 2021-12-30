package gui.main_panels.player_panel;

import control.song.SongControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SpotifyPlayerPanel extends JPanel {

    private SongControl songControl;

    private JLabel connectedLabel;

    public SpotifyPlayerPanel(SongControl songControl, Dimension windowDimension) {
        super(null);

        this.songControl = songControl;

        this.setOpaque(true);
        this.setBackground(new Color(24, 24, 24));

        this.connectedLabel = new JLabel("", SwingConstants.CENTER);
        this.connectedLabel.setForeground(Color.WHITE);
        this.connectedLabel.setBounds((windowDimension.width - 200) / 2, 5, 200, 20);
        //this.add(this.songPlayerPanel);
        //this.songPlayerPanel.setBounds(100, 100, 500, 500);

        JButton connectToSpotifyButton = new JButton("Connect to your Spotify");
        connectToSpotifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.connectToSpotify();
                repaint();
            }
        });
        connectToSpotifyButton.setBounds((windowDimension.width - 200) / 2, 30, 200, 20);

        JButton clickMeButton = new JButton("Click Me!");
        /**
        clickMeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add("spotify:track:5mCPDVBb16L4XQwDdbRUpz");
                StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback()
                        .uris(jsonArray)
                        .build();
                try {
                    startResumeUsersPlaybackRequest.execute();
                } catch (IOException | SpotifyWebApiException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        **/
        clickMeButton.setBounds(10, 10, 40, 10);

        this.add(this.connectedLabel);
        this.add(connectToSpotifyButton);
        //this.add(clickMeButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            this.connectedLabel.setText("Connected to " + this.songControl.getLastConnectedUserName() + "'s Spotify");
        } catch (NullPointerException e) {
            this.connectedLabel.setText("[ not connected ]");
        }
    }
}
