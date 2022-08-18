package gui.main_panels.player_panel;

import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import control.event.EventControl;
import control.event.EventSongCommunication;
import control.event.TimeMeasure;
import control.song.SongControl;
import control.song.SongRequestAcceptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SpotifyPlayerPanel extends JPanel {

    private final SongRequestAcceptor songControl;
    private final EventSongCommunication eventSongCommunication;

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

    private final JButton exportTimeMeasureButton;

    public SpotifyPlayerPanel(SongRequestAcceptor songControl, EventSongCommunication eventSongCommunication, Dimension windowDimension) {
        super(null);

        this.setOpaque(true);
        this.setBackground(new Color(25, 20, 20));

        this.songControl = songControl;
        this.eventSongCommunication = eventSongCommunication;

        //Connected Label
        this.connectedLabel = new JLabel("", SwingConstants.CENTER);
        this.connectedLabel.setForeground(Color.WHITE);

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
        this.searchSongField.setEnabled(false);

        this.add(this.searchSongField);

        //Search Song Button
        this.searchSongButton = new JButton("Search");
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
        this.selectSongBox.setEnabled(false);

        this.add(selectSongBox);

        //Select Song Button
        this.selectSongButton = new JButton("Select");
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

        this.add(this.songImageLabel);

        //Synced Label
        this.songConnectedLabel = new JLabel("", SwingConstants.CENTER);
        this.songConnectedLabel.setForeground(Color.WHITE);
        this.add(this.songConnectedLabel);

        //Sync Button
        SpotifyPlayerPanel that = this;
        this.songConnectButton = new JButton("Connect Song");
        this.songConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    songControl.onStartPlayback();
                } catch (NotFoundException ex) {
                    JOptionPane.showMessageDialog(that, ex.getMessage(), "Connection failed!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.add(this.songConnectButton);

        //Current Devices Text
        this.currentDevicesText = new JLabel("", SwingConstants.CENTER);
        this.currentDevicesText.setForeground(Color.WHITE);
        this.currentDevicesText.setBackground(Color.BLUE);
        this.add(this.currentDevicesText);

        //Export Time Measures Button
        this.exportTimeMeasureButton = new JButton("Generate Time Measures");
        this.exportTimeMeasureButton.setEnabled(false);
        this.exportTimeMeasureButton.addActionListener(e -> {
            //TODO abfragen ob WIRKLICH importieren (löscht die anderen TimeMeasures)

            this.eventSongCommunication.importTimeMeasure(this.songControl.generateTimeMeasures());
        });
        this.add(this.exportTimeMeasureButton);

        this.resizeComponents(windowDimension);
    }

    public void resizeComponents(Dimension dimension) {
        this.searchSongButton.setBounds((dimension.width / 2) + 51, 60, 100, 25);
        this.searchSongField.setBounds((dimension.width / 2) - 150, 60, 200, 25);
        this.connectToSpotifyButton.setBounds((dimension.width - 200) / 2, 30, 200, 25);
        this.connectedLabel.setBounds((dimension.width - 200) / 2, 5, 200, 25);
        this.selectSongBox.setBounds((dimension.width / 2) - 150, 85, 200, 25);
        this.selectSongButton.setBounds((dimension.width / 2) + 51, 85, 100, 25);
        this.songImageLabel.setBounds((dimension.width - 300) / 2, 120, 300, 300);
        this.songConnectedLabel.setBounds(dimension.width / 2 - 150, 430, 150, 25);
        this.songConnectButton.setBounds(dimension.width / 2, 430, 150, 20);
        this.currentDevicesText.setBounds((dimension.width) / 6 - 100, 5, 200, 100);
        this.exportTimeMeasureButton.setBounds((dimension.width * 5 / 6) - 100, 50, 200, 25);

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
            this.currentDevicesText.setText("<html>Available Devices: " + this.songControl.getDeviceNameList() + "</html>");
        } else {
            this.connectedLabel.setText("[not connected]");
            this.searchSongField.setEnabled(false);
            this.searchSongButton.setEnabled(false);
            this.selectSongBox.setEnabled(false);
            this.selectSongButton.setEnabled(false);
            this.currentDevicesText.setText("[devices]");
        }
        if(this.songControl.isSongSelected()) {
            this.songConnectButton.setEnabled(true);
            this.exportTimeMeasureButton.setEnabled(true);
        } else {
            this.songConnectButton.setEnabled(false);
            this.exportTimeMeasureButton.setEnabled(false);
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
