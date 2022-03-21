package control.save;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import control.node.NodeConnection;
import control.node.ThreeCoordinatePoint;
import control.type_enums.InputDialogType;
import control.type_enums.NodeType;
import logic.function.LogicFunction;
import logic.node.LogicNode;

import java.io.FileWriter;
import java.io.IOException;

public abstract class JsonWriter {

    public static void writeNodesToFile(NodeSaveUnit nodeSaveUnit, String path) {
        JsonObject finalObject = new JsonObject();

        JsonArray nodeArray = new JsonArray();
        for(LogicNode logicNode : nodeSaveUnit.getLogicNodes()) {
            nodeArray.add(JsonWriter.getNodeJsonObject(logicNode));
        }
        finalObject.add("nodes", nodeArray);

        JsonArray connectionsArray = new JsonArray();
        for(NodeConnection nodeConnection : nodeSaveUnit.getNodeConnections()) {
            connectionsArray.add(JsonWriter.getNodeConenctionJsonObject(nodeConnection));
        }
        finalObject.add("node_connections", connectionsArray);

        JsonArray functionArray = new JsonArray();
        for(LogicFunction logicFunction : nodeSaveUnit.getLogicFunctions()) {
            JsonObject functionObject = new JsonObject();
            functionObject.addProperty("function_index", logicFunction.getFunctionIndex());
            functionObject.addProperty("function_name", logicFunction.getFunctionName());

            JsonArray functionNodesArray = new JsonArray();
            for(LogicNode logicNode : logicFunction.getLogicNodes()) {
                functionNodesArray.add(JsonWriter.getNodeJsonObject(logicNode));
            }
            functionObject.add("nodes", functionNodesArray);

            JsonArray functionConnectionsArray = new JsonArray();
            for(NodeConnection nodeConnection : logicFunction.getNodeConnections()) {
                functionConnectionsArray.add(JsonWriter.getNodeConenctionJsonObject(nodeConnection));
            }
            functionObject.add("node_connections", functionConnectionsArray);

            functionArray.add(functionObject);
        }
        finalObject.add("functions", functionArray);

        JsonArray trackNodeIndexArray = new JsonArray();
        for(ThreeCoordinatePoint coordinatePoint : nodeSaveUnit.getTrackNodeIndexes()) {
            trackNodeIndexArray.add(JsonWriter.getThreeCoordinateJsonObject(coordinatePoint));
        }
        finalObject.add("track_node_indexes", trackNodeIndexArray);

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
        nodeObject.addProperty("node_index", logicNode.getNodeIndex());
        nodeObject.addProperty("node_type", nodeType.toString());
        if(!logicNode.getSpecificName().equals("")) {
            nodeObject.addProperty("specific_name", logicNode.getSpecificName());
        }
        Object[] extraParameters = logicNode.getExtraParameters();
        if(extraParameters.length > 0) {
            JsonArray extraParamArray = new JsonArray();
            InputDialogType[] inputDialogTypes = nodeType.getInputDialogTypes();
            for(int i = 0; i < extraParameters.length; i++) {
                JsonObject inputObject = new JsonObject();
                inputObject.addProperty("type", inputDialogTypes[i].toString());
                inputObject.addProperty("value", InputDialogType.valueToString(inputDialogTypes[i], extraParameters[i]));
                extraParamArray.add(inputObject);
            }
            nodeObject.add("extra_parameters", extraParamArray);
        }
        return nodeObject;
    }
    private static JsonObject getNodeConenctionJsonObject(NodeConnection nodeConnection) {
        JsonObject connectionObject = new JsonObject();
        connectionObject.add("input", JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getInputCoordinates()));
        connectionObject.add("output", JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getOutputCoordinates()));
        return connectionObject;
    }
    private static JsonObject getThreeCoordinateJsonObject(ThreeCoordinatePoint coordinatePoint) {
        JsonObject coordinateObject = new JsonObject();
        coordinateObject.addProperty("c_1", coordinatePoint.getX());
        coordinateObject.addProperty("c_2", coordinatePoint.getY());
        coordinateObject.addProperty("c_3", coordinatePoint.getZ());
        return coordinateObject;
    }
}
