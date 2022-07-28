import com.formdev.flatlaf.FlatDarculaLaf;
import control.event.EventControl;
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
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SongControl songControl = new SongControl();
        EventControl eventControl = new EventControl();
        NodeControl nodeControl = new NodeControl();
        LedControl ledControl = new LedControl();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        MainWindow mainWindow = new MainWindow(
                new Dimension((int)(screenSize.width * 0.75), (int)(screenSize.height * 0.75)),
                "LED Node Control",
                songControl,
                eventControl,
                nodeControl,
                ledControl
        );
        nodeControl.setLedNodeCommunication(ledControl);
        mainWindow.setVisible(true);

        AtomicInteger currentMs = new AtomicInteger((int) System.currentTimeMillis());
        AtomicInteger msSince = new AtomicInteger(0);

        Thread asyncThreadA = new Thread(() -> {
            while(true) {
                if(songControl.isSongSelected()) {
                    songControl.updatePlayingState();
                    if(songControl.isSongPlaying() && songControl.isSongPaused()) {
                        int updatedMS;
                        if(songControl.isAnimationMode()) {
                            updatedMS = songControl.getAnimationTime();
                            songControl.setCurrentSongMs(updatedMS);
                        } else {
                            updatedMS = songControl.getUpdatedSongMs();
                            songControl.setCurrentSongMs(updatedMS);
                            songControl.onSkipTo(updatedMS);
                        }
                        eventControl.tick(updatedMS);
                        nodeControl.tick(updatedMS, eventControl.getTrackIntensitiesAt(updatedMS));
                    }
                }
                mainWindow.repaintWindows(eventControl, nodeControl);
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
                    int correctMS;
                    if(!songControl.isSongPaused()) {
                        msSince.set((int)(System.currentTimeMillis() - currentMs.get()));

                         correctMS = msSince.get() + songControl.getCurrentSongMs();

                        eventControl.tick(correctMS);
                        nodeControl.tick(correctMS, eventControl.getTrackIntensitiesAt(correctMS));
                        mainWindow.repaintWindows(eventControl, nodeControl);

                        if(songControl.isAnimationMode()) {
                            songControl.setAnimationTime(correctMS);
                        }

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