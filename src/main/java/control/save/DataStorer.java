package control.save;

import control.NodeControl;
import gui.main_panels.function_panels.FunctionEditWindow;
import gui.main_panels.function_panels.FunctionTabbedPane;
import gui.main_panels.node_panel.NodeEditWindow;
import gui.node_components.GraphicNode;

import java.awt.*;
import java.io.*;

public class DataStorer implements Serializable {

    private String selectedSongID;
    private NodeControl nodeControl;
    private Point[] nodeEditGraphicNodePositions;
    private Point[][] functionEditGraphicNodePositions;

    public DataStorer(String selectedSongID, NodeControl nodeControl, Point[] nodeEditGraphicNodePositions, Point[][] functionEditGraphicNodePositions) {
        this.selectedSongID = selectedSongID;
        this.nodeControl = nodeControl;
        this.nodeEditGraphicNodePositions = nodeEditGraphicNodePositions;
        this.functionEditGraphicNodePositions = functionEditGraphicNodePositions;
    }

    public String getSelectedSongID() {
        return this.selectedSongID;
    }
    public NodeControl getNodeControl() {
        return this.nodeControl;
    }
    public Point[] getNodeEditGraphicNodePositions() {
        return this.nodeEditGraphicNodePositions;
    }
    public Point[][] getFunctionEditGraphicNodePositions() {
        return this.functionEditGraphicNodePositions;
    }

    public static void writeToFile(String filePath, DataStorer dataStorer) {
        try {

            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(dataStorer);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static DataStorer readFromFile(String filePath) {
        DataStorer newDataStorer = null;
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            newDataStorer = (DataStorer) objectIn.readObject();
            objectIn.close();
            System.out.println("Successfully read file");
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return newDataStorer;
    }


}
