package gui.main_panels.player_panel;

import com.google.gson.JsonArray;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.*;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.spotify.SpotifyWebHandler;
import gui.main_panels.node_panel.SongPlayerPanel;
import org.apache.hc.core5.http.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SpotifyPlayerPanel extends JPanel {

    private SpotifyWebHandler spotifyWebHandler;
    private SongPlayerPanel songPlayerPanel;

    public SpotifyPlayerPanel() {
        super(new BorderLayout());
        this.spotifyWebHandler = new SpotifyWebHandler();
        this.songPlayerPanel = new SongPlayerPanel(this);

        this.setOpaque(true);
        this.setBackground(new Color(24, 24, 24));

        JLabel connectedLabel = new JLabel();
        JPanel northPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //super.paintComponent(g);
                if(spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
                    GetCurrentUsersProfileRequest currentUsersProfileRequest = spotifyWebHandler.getSpotifyApi().getCurrentUsersProfile().build();
                    try {
                        User currentUser = currentUsersProfileRequest.execute();
                        connectedLabel.setText("Connected to " + currentUser.getDisplayName() + "'s Spotify");
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        northPanel.setPreferredSize(new Dimension(500, 100));
        JPanel southPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {}
        };
        southPanel.setPreferredSize(new Dimension(500, 50));
        JPanel eastPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {}
        };
        eastPanel.setPreferredSize(new Dimension(400, 500));
        JPanel westPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {}
        };
        westPanel.setPreferredSize(new Dimension(400, 500));

        this.add(songPlayerPanel, BorderLayout.CENTER);
        this.add(northPanel, BorderLayout.NORTH);
        this.add(southPanel, BorderLayout.SOUTH);
        this.add(eastPanel, BorderLayout.EAST);
        this.add(westPanel, BorderLayout.WEST);

        northPanel.add(connectedLabel);
        JButton connectToSpotifyButton = new JButton("Connect to your Spotify");
        connectToSpotifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spotifyWebHandler.init();
                songPlayerPanel.updatePlayImage();
                repaint();
            }
        });
        northPanel.add(connectToSpotifyButton);

        JButton clickMeButton = new JButton("Click Me!");
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
        northPanel.add(clickMeButton);
    }

    public CurrentlyPlayingContext onGetCurrentPlayingTrack() {
        GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
        CurrentlyPlayingContext currentlyPlayingContext = null;
        try {
            currentlyPlayingContext = currentPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return currentlyPlayingContext;
    }

    public void onPausePlayback() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().pauseUsersPlayback().build();
        try {
            pauseUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void onResumePlayback() {
        StartResumeUsersPlaybackRequest resumeUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback().build();
        try {
            resumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
}
