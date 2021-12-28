package gui.main_panels.function_panels;

import control.type_enums.JointType;
import control.NodeControl;
import gui.node_components.GraphicNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FunctionTabbedPane extends JTabbedPane {

    private NodeControl nodeControl;
    private ArrayList<FunctionEditWindow> functionEditWindows;

    public FunctionTabbedPane(NodeControl nodeControl) {
        super(JTabbedPane.TOP);
        this.nodeControl = nodeControl;
        this.functionEditWindows = new ArrayList<>();
    }

    public ArrayList<FunctionEditWindow> getFunctionEditWindows() {
        return this.functionEditWindows;
    }

    public void setNodeControl(NodeControl nodeControl) {
        this.nodeControl = nodeControl;
        for(FunctionEditWindow functionEditWindow : this.functionEditWindows) {
            functionEditWindow.setNodeControl(nodeControl);
        }

    }

    public int addPanel(String panelName) {
        int functionIndex = this.nodeControl.getNextFreeFunctionIndex();
        FunctionEditWindow newFunctionEditWindow = new FunctionEditWindow(this.nodeControl, functionIndex);
        this.functionEditWindows.add(newFunctionEditWindow);
        this.addTab(panelName, newFunctionEditWindow);
        this.repaint();
        return functionIndex;
    }

    public void onFunctionCreated(int panelIndex, String[] inputNames, JointType[] inputTypes, String[] outputNames, JointType[] outputTypes) {
        this.functionEditWindows.get(panelIndex).addFunction(inputNames, inputTypes, outputNames, outputTypes);
    }

    public void updateFunctions(Point[][] functionEditGraphicNodePositions) {
        int[] functionIndexArray = nodeControl.getFunctionIndexArray();
        for(int i = 0; i < functionIndexArray.length; i++) {
            FunctionEditWindow newFunctionEditWindow = new FunctionEditWindow(this.nodeControl, functionIndexArray[i]);
            this.functionEditWindows.add(newFunctionEditWindow);
            newFunctionEditWindow.updateGraphicNodes(functionEditGraphicNodePositions[i]);
            this.addTab("", newFunctionEditWindow);
            this.repaint();
        }
    }
}
