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

        //int i;
        //long currentTimeMillis = System.currentTimeMillis();
        /*
        while(true) {
            i = (int)(System.currentTimeMillis() - currentTimeMillis);
            if(songControl.isSongSelected()) {
                songControl.tick(i);
            } else {
                try {
                    Thread.sleep(1);
                    currentTimeMillis = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
         */

        AtomicInteger currentMs = new AtomicInteger((int) System.currentTimeMillis());
        AtomicInteger msSince = new AtomicInteger();
        AtomicInteger lastUpdatedMs = new AtomicInteger(0);

        Thread asyncThreadA = new Thread(() -> {
            while(true) {
                //TODO: every 500ms or so, update Song playing status
                if(songControl.isSongSelected()) {
                    long msBeforeRequest = System.currentTimeMillis();
                    songControl.updatePlayingState();
                    long msAfterRequest = System.currentTimeMillis();

                    System.out.println(msAfterRequest - msBeforeRequest);
                    lastUpdatedMs.set(songControl.getCurrentSongMs());

                    currentMs.set((int)System.currentTimeMillis());
                    msSince.set((int)(msAfterRequest - msBeforeRequest));
                }
                mainWindow.repaintWindows();
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

                        int msSinceInt = msSince.intValue();
                        int lastUpdatedInt = lastUpdatedMs.intValue();
                        int correctMS = msSinceInt + lastUpdatedInt;

                        songControl.tick(correctMS);
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