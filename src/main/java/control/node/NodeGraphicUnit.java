package control.node;

import control.type_enums.NodeType;
import gui.main_panels.node_panel.GraphicNode;

import java.awt.*;

public interface NodeGraphicUnit {

    void addGraphicNode(int functionIndex, int nodeIndex, NodeType nodeType, String nodeName);
    GraphicNode findGraphicNode(int nodeIndex);
    void moveGraphicNode(GraphicNode graphicNode, Point relativeMovement);
    void cleanUpCanvas();
}
