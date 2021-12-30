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
    private JButton connectToSpotifyButton;

    private final JTextField searchSongField;
    private final JButton searchSongButton;
    private final JComboBox<String> selectSongBox;
    private final JButton selectSongButton;

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
                if(songControl.connectToSpotify()) {
                    searchSongField.setEnabled(true);
                    searchSongButton.setEnabled(true);
                    selectSongBox.setEnabled(true);
                    selectSongButton.setEnabled(true);
                }
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
        this.searchSongField.setBounds((windowDimension.width / 4) - 150, 30, 200, 20);
        this.searchSongField.setEnabled(false);

        this.add(this.searchSongField);

        //Search Song Button
        this.searchSongButton = new JButton("Search");
        this.searchSongButton.setBounds((windowDimension.width / 4) + 51, 30, 100, 20);
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
        this.selectSongBox.setBounds((windowDimension.width / 4) - 150, 55, 200, 20);
        this.selectSongBox.setEnabled(false);

        this.add(selectSongBox);

        //Select Song Button
        this.selectSongButton = new JButton("Select");
        this.selectSongButton.setBounds((windowDimension.width / 4) + 51, 55, 100, 20);
        this.selectSongButton.setEnabled(false);

        this.selectSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.selectSongBySearchedList(selectSongBox.getSelectedIndex());

                String imageURL = songControl.getImageURL();
                if(imageURL != null) {
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(new URL(imageURL));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    songImageLabel.setIcon(new ImageIcon(getScaledImage(image, 300, 300)));
                    repaint();
                }
            }
        });

        this.add(selectSongButton);

        //Song Image Label
        this.songImageLabel = new JLabel();
        this.songImageLabel.setBounds((windowDimension.width - 300) / 2, 100, 300, 300);

        this.add(this.songImageLabel);
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

    public static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}
