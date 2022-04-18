import control.led.LedControl;
import control.node.NodeControl;
import control.song.SongControl;
import control.spotify.SpotifyWebHandler;
import gui.MainWindow;
import logic.led.LogicMask;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SongControl songControl = new SongControl();
        NodeControl nodeControl = new NodeControl();
        LedControl ledControl = new LedControl();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        MainWindow mainWindow = new MainWindow(
                new Dimension((int)(screenSize.width * 0.75), (int)(screenSize.height * 0.75)),
                "LED Node Control",
                songControl,
                nodeControl,
                ledControl
        );
        mainWindow.setVisible(true);

        AtomicInteger currentMs = new AtomicInteger((int) System.currentTimeMillis());
        AtomicInteger msSince = new AtomicInteger(0);

        Thread asyncThreadA = new Thread(() -> {
            while(true) {
                if(songControl.isSongSelected()) {
                    songControl.updatePlayingState();
                    if(songControl.isSongPlaying() && songControl.isSongPaused()) {
                        int updatedMS = songControl.getUpdatedSongMs();
                        songControl.setCurrentSongMs(updatedMS);
                        songControl.onSkipTo(updatedMS);

                        songControl.tick(updatedMS);
                        nodeControl.tick(updatedMS, songControl.getTrackIntensitiesAt(updatedMS));
                    }
                }
                mainWindow.repaintWindows(songControl, nodeControl);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        asyncThreadA.start();

        Thread asyncThreadB = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(songControl.isSongPlaying()) {
                    if(!songControl.isSongPaused()) {
                        msSince.set((int)(System.currentTimeMillis() - currentMs.get()));

                        int correctMS = msSince.get() + songControl.getCurrentSongMs();

                        songControl.tick(correctMS);
                        nodeControl.tick(correctMS, songControl.getTrackIntensitiesAt(correctMS));
                        mainWindow.repaintWindows(songControl, nodeControl);

                    } else {
                        currentMs.set((int)System.currentTimeMillis());
                        msSince.set(0);
                    }
                }
            }
        });
        asyncThreadB.start();
    }
}