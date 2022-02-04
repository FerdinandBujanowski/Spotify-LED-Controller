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

        int i;
        long currentTimeMillis = System.currentTimeMillis();
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
        Thread asyncThread = new Thread(() -> {
            while(true) {
                //TODO: every 500ms or so, update Song playing status
                songControl.updatePlayingState();
                mainWindow.repaintWindows();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        asyncThread.start();

        while (true) {
            //TODO: as long as Spotify and SongControl are still synchronized, update the GUI accordingly
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}