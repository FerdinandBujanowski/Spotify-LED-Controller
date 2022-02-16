import control.led.LedControl;
import control.node.NodeControl;
import control.song.SongControl;
import gui.MainWindow;

import javax.swing.*;
import java.awt.*;
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
                //TODO: every 500ms or so, update Song playing status
                if(songControl.isSongSelected()) {
                    songControl.updatePlayingState();
                    if(songControl.isSongPlaying() && songControl.isSongPaused()) {
                        songControl.setCurrentSongMs(songControl.getUpdatedSongMs());

                        songControl.tick(songControl.getUpdatedSongMs());
                        nodeControl.tick(songControl.getUpdatedSongMs());
                    }
                }
                mainWindow.repaintWindows(songControl);
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
                //TODO: as long as Spotify and SongControl are still synchronized, update the GUI accordingly
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
                        nodeControl.tick(correctMS);
                        mainWindow.repaintWindows(songControl);

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