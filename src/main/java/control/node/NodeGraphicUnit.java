package control.node;

import control.type_enums.NodeType;

public interface NodeGraphicUnit {

    void addGraphicNode(int functionIndex, int nodeIndex, NodeType nodeType, String nodeName);
    void cleanUpCanvas();
}
