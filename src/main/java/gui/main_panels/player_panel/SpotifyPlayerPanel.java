package gui.main_panels.player_panel;

import com.google.gson.JsonArray;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.*;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.song.SongControl;
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

    private SongControl songControl;

    private JLabel connectedLabel;

    public SpotifyPlayerPanel(SongControl songControl, Dimension windowDimension) {
        super(null);
        this.spotifyWebHandler = new SpotifyWebHandler();
        this.songPlayerPanel = new SongPlayerPanel(this);

        this.songControl = songControl;

        this.setOpaque(true);
        this.setBackground(new Color(24, 24, 24));

        this.connectedLabel = new JLabel();
        this.connectedLabel.setBounds(0, 0, 50, 10);
        this.add(this.songPlayerPanel);
        this.songPlayerPanel.setBounds(100, 100, 300, 300);

        JButton connectToSpotifyButton = new JButton("Connect to your Spotify");
        connectToSpotifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spotifyWebHandler.init();
                songPlayerPanel.updatePlayImage();
                repaint();
            }
        });
        connectToSpotifyButton.setBounds(20, 20, 40, 10);

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
        clickMeButton.setBounds(10, 10, 40, 10);

        this.add(this.connectedLabel);
        this.add(connectToSpotifyButton);
        this.add(clickMeButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetCurrentUsersProfileRequest currentUsersProfileRequest = this.spotifyWebHandler.getSpotifyApi().getCurrentUsersProfile().build();
            try {
                User currentUser = currentUsersProfileRequest.execute();
                this.connectedLabel.setText("Connected to " + currentUser.getDisplayName() + "'s Spotify");
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
            }
        }
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
