package gui.main_panels.node_panel;

import control.type_enums.NodeType;

public interface NodeGraphicUnit {

    void addGraphicNode(int functionIndex, int nodeIndex, NodeType nodeType, String nodeName);
}
