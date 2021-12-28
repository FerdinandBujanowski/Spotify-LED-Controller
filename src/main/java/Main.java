import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import control.NodeControl;
import control.event.EventControl;
import control.spotify.SpotifyWebHandler;
import gui.MainWindow;

import org.apache.hc.core5.http.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        NodeControl nodeControl = new NodeControl();
        EventControl eventControl = new EventControl();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        MainWindow mainWindow = new MainWindow(
                new Dimension((int)(screenSize.width * 0.75), (int)(screenSize.height * 0.75)),
                "LED Node Control",
                nodeControl,
                eventControl
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
