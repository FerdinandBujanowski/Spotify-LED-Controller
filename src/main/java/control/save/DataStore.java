package control.save;

import control.led.LedControl;
import control.node.NodeControl;
import control.song.SongControl;

import java.awt.*;
import java.io.*;

public class DataStore implements Serializable {

    private final String selectedSongID;
    private final SongControl songControl;
    private final NodeControl nodeControl;
    private final LedControl ledControl;
    private final Point[] nodeEditGraphicNodePositions;
    private final Point[][] functionEditGraphicNodePositions;

    public DataStore(String selectedSongID, SongControl songControl, NodeControl nodeControl, LedControl ledControl, Point[] nodeEditGraphicNodePositions, Point[][] functionEditGraphicNodePositions) {
        this.selectedSongID = selectedSongID;
        this.nodeControl = nodeControl;
        this.songControl = songControl;
        this.ledControl = ledControl;
        this.nodeEditGraphicNodePositions = nodeEditGraphicNodePositions;
        this.functionEditGraphicNodePositions = functionEditGraphicNodePositions;
    }

    public String getSelectedSongID() {
        return this.selectedSongID;
    }
    public SongControl getSongControl() {
        return this.songControl;
    }
    public NodeControl getNodeControl() {
        return this.nodeControl;
    }
    public LedControl getLedControl() {
        return this.ledControl;
    }
    public Point[] getNodeEditGraphicNodePositions() {
        return this.nodeEditGraphicNodePositions;
    }
    public Point[][] getFunctionEditGraphicNodePositions() {
        return this.functionEditGraphicNodePositions;
    }

    public static void writeToFile(String filePath, DataStore dataStore) {
        try {

            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(dataStore);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static DataStore readFromFile(String filePath) {
        DataStore newDataStore = null;
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            newDataStore = (DataStore) objectIn.readObject();
            objectIn.close();
            System.out.println("Successfully read file");
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return newDataStore;
    }


}
