package control.save;

import control.led.LedControl;
import control.node.NodeControl;

import java.awt.*;
import java.io.*;

public class DataStore implements Serializable {

    private final EventSaveUnit eventSaveUnit;
    private final NodeSaveUnit nodeSaveUnit;
    private final LedControl ledControl;
    private final Point[] nodeEditGraphicNodePositions;
    private final Point[][] functionEditGraphicNodePositions;

    public DataStore(EventSaveUnit eventSaveUnit, NodeSaveUnit nodeSaveUnit, LedControl ledControl, Point[] nodeEditGraphicNodePositions, Point[][] functionEditGraphicNodePositions) {
        this.eventSaveUnit = eventSaveUnit;
        this.nodeSaveUnit = nodeSaveUnit;
        this.ledControl = ledControl;
        this.nodeEditGraphicNodePositions = nodeEditGraphicNodePositions;
        this.functionEditGraphicNodePositions = functionEditGraphicNodePositions;
    }

    public EventSaveUnit getEventSaveUnit() {
        return this.eventSaveUnit;
    }
    public NodeSaveUnit getNodeSaveUnit() {
        return this.nodeSaveUnit;
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
