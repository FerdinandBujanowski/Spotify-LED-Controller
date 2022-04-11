package control.save;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.exceptions.JointConnectionFailedException;
import control.node.NodeConnection;
import control.node.NodeControl;
import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;
import control.type_enums.InputDialogType;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import gui.MainWindow;
import logic.function.LogicFunction;
import logic.node.LogicNode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

public abstract class JsonWriter {

    private final static String NODES = "nodes";
    private final static String NODE_CONNECTIONS = "node_connections";
    private final static String FUNCTIONS = "functions";

    private final static String NODE_INDEX = "node_index";
    private final static String NODE_TYPE = "node_type";
    private final static String SPECIFIC_NAME = "specific_name";

    private final static String EXTRA_PARAMETERS = "extra_parameters";
    private final static String TYPE = "type";
    private final static String VALUE = "value";

    private final static String FUNCTION_INDEX = "function_index";
    private final static String FUNCTION_NAME = "function_name";

    private final static String INPUT = "input";
    private final static String OUTPUT = "output";
    private final static String C_1 = "function_index", C_2 = "node_index", C_3 = "joint_index";


    public static void writeNodesToFile(NodeSaveUnit nodeSaveUnit, String path) {
        JsonObject finalObject = new JsonObject();

        JsonArray nodeArray = new JsonArray();
        for(LogicNode logicNode : nodeSaveUnit.getLogicNodes()) {
            nodeArray.add(JsonWriter.getNodeJsonObject(logicNode));
        }
        finalObject.add(NODES, nodeArray);

        JsonArray connectionsArray = new JsonArray();
        for(NodeConnection nodeConnection : nodeSaveUnit.getNodeConnections()) {
            connectionsArray.add(JsonWriter.getNodeConenctionJsonObject(nodeConnection));
        }
        finalObject.add(NODE_CONNECTIONS, connectionsArray);

        JsonArray functionArray = new JsonArray();
        for(LogicFunction logicFunction : nodeSaveUnit.getLogicFunctions()) {
            JsonObject functionObject = new JsonObject();
            functionObject.addProperty(FUNCTION_INDEX, logicFunction.getFunctionIndex());
            functionObject.addProperty(FUNCTION_NAME, logicFunction.getFunctionName());

            JsonArray functionNodesArray = new JsonArray();
            for(LogicNode logicNode : logicFunction.getLogicNodes()) {
                functionNodesArray.add(JsonWriter.getNodeJsonObject(logicNode));
            }
            functionObject.add(NODES, functionNodesArray);

            JsonArray functionConnectionsArray = new JsonArray();
            for(NodeConnection nodeConnection : logicFunction.getNodeConnections()) {
                functionConnectionsArray.add(JsonWriter.getNodeConenctionJsonObject(nodeConnection));
            }
            functionObject.add(NODE_CONNECTIONS, functionConnectionsArray);

            functionArray.add(functionObject);
        }
        finalObject.add(FUNCTIONS, functionArray);

        try {
            FileWriter file = new FileWriter(path);
            file.write(finalObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file successfully created");
    }

    private static JsonObject getNodeJsonObject(LogicNode logicNode) {
        JsonObject nodeObject = new JsonObject();
        NodeType nodeType = logicNode.getNodeType();
        nodeObject.addProperty(NODE_INDEX, logicNode.getNodeIndex());
        nodeObject.addProperty(NODE_TYPE, nodeType.toString());
        if(!logicNode.getSpecificName().equals("")) {
            nodeObject.addProperty(SPECIFIC_NAME, logicNode.getSpecificName());
        }
        Object[] extraParameters = logicNode.getExtraParameters();
        if(extraParameters.length > 0) {
            JsonArray extraParamArray = new JsonArray();
            InputDialogType[] inputDialogTypes = nodeType.getInputDialogTypes();
            for(int i = 0; i < extraParameters.length; i++) {
                JsonObject inputObject = new JsonObject();
                inputObject.addProperty(TYPE, inputDialogTypes[i].toString());
                inputObject.addProperty(VALUE, InputDialogType.valueToString(inputDialogTypes[i], extraParameters[i]));
                extraParamArray.add(inputObject);
            }
            nodeObject.add(EXTRA_PARAMETERS, extraParamArray);
        }
        return nodeObject;
    }
    private static JsonObject getNodeConenctionJsonObject(NodeConnection nodeConnection) {
        JsonObject connectionObject = new JsonObject();
        connectionObject.add(INPUT, JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getInputCoordinates()));
        connectionObject.add(OUTPUT, JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getOutputCoordinates()));
        return connectionObject;
    }
    private static JsonObject getThreeCoordinateJsonObject(ThreeCoordinatePoint coordinatePoint) {
        JsonObject coordinateObject = new JsonObject();
        coordinateObject.addProperty(C_1, coordinatePoint.getX());
        coordinateObject.addProperty(C_2, coordinatePoint.getY());
        coordinateObject.addProperty(C_3, coordinatePoint.getZ());
        return coordinateObject;
    }

    public static void addNodesFromFile(String path, NodeControl nodeControl, MainWindow mainWindow) {
        JsonObject nodesObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(path);
            nodesObject = jsonParser.parse(fileReader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //FIRST : functions
        TwoIntegerCorrespondence functionCorrespondence = new TwoIntegerCorrespondence();
        int functionIndexOffset = nodeControl.getNextFreeFunctionIndex();
        JsonArray functionArray = nodesObject.getAsJsonArray(FUNCTIONS);
        for(int i = 0; i < functionArray.size(); i++) {

            JsonObject functionObject = functionArray.get(i).getAsJsonObject();
            int oldFunctionIndex = functionObject.get(FUNCTION_INDEX).getAsInt();
            int newFunctionIndex = functionIndexOffset + oldFunctionIndex;
            functionCorrespondence.addValue(oldFunctionIndex, newFunctionIndex);
            JsonArray functionNodesArray = functionObject.getAsJsonArray(NODES);
            JsonArray nodeConnectionsArray = functionObject.getAsJsonArray(NODE_CONNECTIONS);
            String functionName = functionObject.get(FUNCTION_NAME).getAsString();

            mainWindow.createFunction(functionName, new String[0], new JointType[0], new String[0], new JointType[0], nodeControl);
            nodeControl.addFunction(newFunctionIndex, functionName, new String[0], new JointType[0], new String[0], new JointType[0]);

            TwoIntegerCorrespondence nodeIndexCorrespondence = new TwoIntegerCorrespondence();

            //FUNCTION NODES
            for(int functionNodesIndex = 0; functionNodesIndex < functionNodesArray.size(); functionNodesIndex++) {
                JsonObject currentNodeObject = functionNodesArray.get(functionNodesIndex).getAsJsonObject();

                int oldIndex = currentNodeObject.get(NODE_INDEX).getAsInt();
                int newIndex = nodeControl.getNextFreeNodeIndex(newFunctionIndex);
                nodeIndexCorrespondence.addValue(oldIndex, newIndex);
                NodeType nodeType = NodeType.getNodeTypeByString(currentNodeObject.get(NODE_TYPE).getAsString());

                JsonArray extraParamJsonArray = currentNodeObject.getAsJsonArray(EXTRA_PARAMETERS);
                Object[] extraParamArray = new Object[extraParamJsonArray != null ? extraParamJsonArray.size() : 0];
                for(int extraParamIndex = 0; extraParamIndex < extraParamArray.length; extraParamIndex++) {
                    JsonObject currentParamObject = extraParamJsonArray.get(extraParamIndex).getAsJsonObject();
                    String typeString = currentParamObject.get(TYPE).getAsString();
                    String valueString = currentParamObject.get(VALUE).getAsString();
                    extraParamArray[extraParamIndex] = InputDialogType.getValueByString(typeString, valueString);
                }

                if(nodeType == NodeType._INPUT_PARAMETER_NODE || nodeType == NodeType._OUTPUT_PARAMETER_NODE) {
                    extraParamArray[0] = functionCorrespondence.getCorrespondingValue((Integer) extraParamArray[0]);
                }
                nodeControl.addNode(newFunctionIndex, nodeType, extraParamArray);
            }

            //NODE CONNECTIONS
            for(int nodeConnectionIndex = 0; nodeConnectionIndex < nodeConnectionsArray.size(); nodeConnectionIndex++) {
                JsonObject currentConnectionObject = nodeConnectionsArray.get(nodeConnectionIndex).getAsJsonObject();

                NodeConnection nodeConnection = getNodeConnection(currentConnectionObject, functionCorrespondence, nodeIndexCorrespondence);
                try {
                    nodeControl.makeNodeConnection(nodeConnection);
                } catch (JointConnectionFailedException e) {
                    System.out.println(e.getNodeConnection());
                    e.printStackTrace();
                }
            }
        }

        //NODES
        JsonArray nodesArray = nodesObject.getAsJsonArray(NODES);
        TwoIntegerCorrespondence nodeIndexCorrespondence = new TwoIntegerCorrespondence();
        for(int currentNodeIndex = 0; currentNodeIndex < nodesArray.size(); currentNodeIndex++) {
            JsonObject currentNodeObject = nodesArray.get(currentNodeIndex).getAsJsonObject();

            int oldIndex = currentNodeObject.get(NODE_INDEX).getAsInt();
            int newIndex = nodeControl.getNextFreeNodeIndex(-1);
            nodeIndexCorrespondence.addValue(oldIndex, newIndex);

            NodeType nodeType = NodeType.getNodeTypeByString(currentNodeObject.get(NODE_TYPE).getAsString());
            JsonArray extraParamJsonArray = currentNodeObject.getAsJsonArray(EXTRA_PARAMETERS);
            Object[] extraParamArray = new Object[extraParamJsonArray != null ? extraParamJsonArray.size() : 0];
            for(int extraParamIndex = 0; extraParamIndex < extraParamArray.length; extraParamIndex++) {
                JsonObject currentParamObject = extraParamJsonArray.get(extraParamIndex).getAsJsonObject();
                String typeString = currentParamObject.get(TYPE).getAsString();
                String valueString = currentParamObject.get(VALUE).getAsString();
                extraParamArray[extraParamIndex] = InputDialogType.getValueByString(typeString, valueString);
            }

            if(nodeType == NodeType._FUNCTION_NODE) {
                extraParamArray[0] = functionCorrespondence.getCorrespondingValue((Integer) extraParamArray[0]);
            }
            nodeControl.addNode(-1, nodeType, extraParamArray);
        }

        //NODE CONNECTIONS
        JsonArray nodeConnectionsArray = nodesObject.getAsJsonArray(NODE_CONNECTIONS);
        for(int connectionIndex = 0; connectionIndex < nodeConnectionsArray.size(); connectionIndex++) {
            JsonObject currentConnectionObject = nodeConnectionsArray.get(connectionIndex).getAsJsonObject();
            NodeConnection nodeConnection = getNodeConnection(currentConnectionObject, new TwoIntegerCorrespondence(), nodeIndexCorrespondence);
            try {
                nodeControl.makeNodeConnection(nodeConnection);
            } catch (JointConnectionFailedException e) {
                e.printStackTrace();
            }
        }
    }

    private static NodeConnection getNodeConnection(JsonObject connectionObject, TwoIntegerCorrespondence funcCor, TwoIntegerCorrespondence nodeCor) {
        ThreeCoordinatePoint input = getCoordinate(connectionObject.get(INPUT).getAsJsonObject(), funcCor, nodeCor);
        ThreeCoordinatePoint output = getCoordinate(connectionObject.get(OUTPUT).getAsJsonObject(), funcCor, nodeCor);
        return new NodeConnection(output, input);
    }

    private static ThreeCoordinatePoint getCoordinate(JsonObject pointObject, TwoIntegerCorrespondence funcCor, TwoIntegerCorrespondence nodeCor) {
        return new ThreeCoordinatePoint(
                funcCor.getCorrespondingValue(pointObject.get(C_1).getAsInt()),
                nodeCor.getCorrespondingValue(pointObject.get(C_2).getAsInt()),
                pointObject.get(C_3).getAsInt()
        );
    }
}
