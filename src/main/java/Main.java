import control.node.NodeControl;
import control.song.SongControl;
import gui.MainWindow;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        NodeControl nodeControl = new NodeControl();
        SongControl songControl = new SongControl();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        MainWindow mainWindow = new MainWindow(
                new Dimension((int)(screenSize.width * 0.75), (int)(screenSize.height * 0.75)),
                "LED Node Control",
                nodeControl,
                songControl
        );
        mainWindow.setVisible(true);

        /**
        SpotifyWebHandler spotifyWebHandler = new SpotifyWebHandler();

        GetInformationAboutUsersCurrentPlaybackRequest usersCurrentPlaybackRequest = spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
        try {
            CurrentlyPlayingContext currentlyPlayingContext = usersCurrentPlaybackRequest.execute();
            Track currentSong = (Track) currentlyPlayingContext.getItem();

            System.out.println("Currently Playing: " + currentSong.getName() + " by " + currentSong.getArtists()[0].getName() + ": " + currentSong.getDurationMs());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
         **/
    }
}
