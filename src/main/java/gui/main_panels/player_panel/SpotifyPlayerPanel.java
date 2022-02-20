package gui.main_panels.player_panel;

import control.song.SongControl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SpotifyPlayerPanel extends JPanel {

    private SongControl songControl;

    private final JLabel connectedLabel;
    private final JButton connectToSpotifyButton;

    private final JTextField searchSongField;
    private final JButton searchSongButton;

    private final JComboBox<String> selectSongBox;
    private final JButton selectSongButton;

    private final JLabel songConnectedLabel;
    private  final JButton songConnectButton;

    private JLabel currentDevicesText;
    private JButton refreshDevicesButton;

    private JLabel songImageLabel;

    public SpotifyPlayerPanel(SongControl songControl, Dimension windowDimension) {
        super(null);

        this.setOpaque(true);
        this.setBackground(new Color(24, 24, 24));

        this.songControl = songControl;

        //Connected Label
        this.connectedLabel = new JLabel("", SwingConstants.CENTER);
        this.connectedLabel.setForeground(Color.WHITE);
        this.connectedLabel.setBounds((windowDimension.width - 200) / 2, 5, 200, 20);

        this.add(this.connectedLabel);

        //Connect To Spotify Button
        this.connectToSpotifyButton = new JButton("Connect to your Spotify");
        this.connectToSpotifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.connectToSpotify();
                repaint();
            }
        });
        this.connectToSpotifyButton.setBounds((windowDimension.width - 200) / 2, 30, 200, 20);

        this.add(this.connectToSpotifyButton);

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

        //Search Song Field
        this.searchSongField = new JTextField();
        this.searchSongField.setBounds((windowDimension.width / 2) - 150, 60, 200, 20);
        this.searchSongField.setEnabled(false);

        this.add(this.searchSongField);

        //Search Song Button
        this.searchSongButton = new JButton("Search");
        this.searchSongButton.setBounds((windowDimension.width / 2) + 51, 60, 100, 20);
        this.searchSongButton.setEnabled(false);

        this.searchSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] trackList = songControl.searchSongByName(searchSongField.getText());
                DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(trackList);
                selectSongBox.setModel(newModel);
            }
        });

        this.add(searchSongButton);

        //Select Song Box
        this.selectSongBox = new JComboBox<>();
        this.selectSongBox.setBounds((windowDimension.width / 2) - 150, 85, 200, 20);
        this.selectSongBox.setEnabled(false);

        this.add(selectSongBox);

        //Select Song Button
        this.selectSongButton = new JButton("Select");
        this.selectSongButton.setBounds((windowDimension.width / 2) + 51, 85, 100, 20);
        this.selectSongButton.setEnabled(false);

        this.selectSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.selectSongBySearchedList(selectSongBox.getSelectedIndex());
                repaint();
            }
        });
        this.add(selectSongButton);

        //Song Image Label
        this.songImageLabel = new JLabel();
        this.songImageLabel.setBounds((windowDimension.width - 300) / 2, 120, 300, 300);

        this.add(this.songImageLabel);

        //Synced Label
        this.songConnectedLabel = new JLabel("", SwingConstants.CENTER);
        this.songConnectedLabel.setBounds(windowDimension.width / 2 - 150, 430, 150, 20);
        this.songConnectedLabel.setForeground(Color.WHITE);
        this.add(this.songConnectedLabel);

        //Sync Button
        this.songConnectButton = new JButton("Connect Song");
        this.songConnectButton.setBounds(windowDimension.width / 2, 430, 150, 20);
        this.songConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        this.add(this.songConnectButton);

        //Current Devices Text
        this.currentDevicesText = new JLabel();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.songControl.isSpotifyConnected()) {
            this.connectedLabel.setText("Connected to " + this.songControl.getLastConnectedUserName() + "'s Spotify");
            this.searchSongField.setEnabled(true);
            this.searchSongButton.setEnabled(true);
            this.selectSongBox.setEnabled(true);
            this.selectSongButton.setEnabled(true);
            this.songConnectButton.setEnabled(true);

        } else {
            this.connectedLabel.setText("[not connected]");
            this.searchSongField.setEnabled(false);
            this.searchSongButton.setEnabled(false);
            this.selectSongBox.setEnabled(false);
            this.selectSongButton.setEnabled(false);
            this.songConnectButton.setEnabled(false);
        }
        if(!this.songControl.isSongPlaying()) {
            this.songConnectedLabel.setText("[not connected]");
        } else {
            this.songConnectedLabel.setText("[connected at " + this.songControl.getCurrentSongMs() + "ms]");
        }

        ImageIcon albumImage = this.songControl.getAlbumImage();
        if(albumImage != null) {
            this.songImageLabel.setIcon(albumImage);
        }
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
