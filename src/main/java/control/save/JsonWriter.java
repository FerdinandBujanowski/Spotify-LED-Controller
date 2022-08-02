package control.save;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.exceptions.JointConnectionFailedException;
import control.led.LedControl;
import control.node.NodeConnection;
import control.node.NodeControl;
import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;
import control.event.EventControl;
import control.type_enums.CurveType;
import control.type_enums.InputDialogType;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import gui.MainWindow;
import logic.function.LogicFunction;
import logic.led.LogicMask;
import logic.node.LogicNode;
import logic.event.LogicEvent;
import logic.event.LogicTrack;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class JsonWriter {

    private final static String TRACKS = "tracks";
    private final static String EVENTS = "events";
    private final static String START = "start";
    private final static String DURATION = "duration";
    private final static String CURVE_TYPE = "curve_type";
    private final static String USER_INPUT = "user_input";
    //TODO : seperate export/import events and time measures
    private final static String TIME_MEASURES = "time_measures";
    private final static String BEATS_BAR = "beats_per_bar";
    private final static String BEATS_MINUTE = "beats_per_minute";

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

    private final static String LAYERS = "layers";
    private final static String PIXELS = "pixels";
    private final static String X = "x";
    private final static String Y = "y";

    private final static String MASK = "mask";
    private final static String INTENSITY = "intensity";

    public static void writeTracksToFile(EventSaveUnit eventSaveUnit, String path) {
        JsonObject finalObject = new JsonObject();

        JsonArray trackArray = new JsonArray();
        for(LogicTrack logicTrack : eventSaveUnit.getLogicTracks()) {
            JsonArray eventsArray = getEventsArray(logicTrack);
            if(eventsArray.size() > 0) {
                JsonObject eventsObject = new JsonObject();
                eventsObject.add(EVENTS, getEventsArray(logicTrack));
                trackArray.add(eventsObject);
            }
        }
        finalObject.add(TRACKS, trackArray);

        try {
            FileWriter file = new FileWriter(path);
            file.write(finalObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file successfully created");
    }

    private static JsonArray getEventsArray(LogicTrack logicTrack) {
        JsonArray eventsArray = new JsonArray();
        for(LogicEvent logicEvent : logicTrack.getEventsCopyArray()) {
            JsonObject eventObject = new JsonObject();
            eventObject.addProperty(START, logicEvent.getMsStart());
            eventObject.addProperty(DURATION, logicEvent.getMsDuration());
            eventObject.addProperty(CURVE_TYPE, logicEvent.getCurveType().toString());
            if(logicEvent.getCurveType() == CurveType.USER_INPUT) {
                eventObject.addProperty(USER_INPUT, logicEvent.getUserInput());
            }
            eventsArray.add(eventObject);
        }
        return eventsArray;
    }

    public static void addTracksFromFile(String path, EventControl eventControl) {
        JsonObject tracksObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(path);
            tracksObject = jsonParser.parse(fileReader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(JsonElement trackElement : tracksObject.getAsJsonArray(TRACKS)) {
            int newTrackIndex = eventControl.getTrackCount() + 1;
            eventControl.onAddTrackRequest();
            for(JsonElement eventElement : trackElement.getAsJsonObject().getAsJsonArray(EVENTS)) {
                JsonObject eventObject = eventElement.getAsJsonObject();
                eventControl.onAddEventToTrackRequest(
                        newTrackIndex,
                        eventObject.get(START).getAsInt(),
                        eventObject.get(DURATION).getAsInt(),
                        CurveType.getCurveTypeByString(eventObject.get(CURVE_TYPE).getAsString()),
                        eventObject.has(USER_INPUT) ? eventObject.get(USER_INPUT).getAsDouble() : 0
                );
            }
        }
    }

    public static void writeNodesToFile(NodeSaveUnit nodeSaveUnit, String path, Point[] graphicNodePositions, Point[][] functionGraphicNodePositions) {
        JsonObject finalObject = new JsonObject();

        JsonArray nodeArray = new JsonArray();
        for(int i = 0; i < nodeSaveUnit.getLogicNodes().size(); i++) {
            LogicNode logicNode = nodeSaveUnit.getLogicNodes().get(i);
            nodeArray.add(JsonWriter.getNodeJsonObject(logicNode, -1, graphicNodePositions[i]));
        }
        finalObject.add(NODES, nodeArray);

        JsonArray connectionsArray = new JsonArray();
        for(NodeConnection nodeConnection : nodeSaveUnit.getNodeConnections()) {
            connectionsArray.add(JsonWriter.getNodeConnectionJsonObject(nodeConnection, -1));
        }
        finalObject.add(NODE_CONNECTIONS, connectionsArray);

        JsonArray functionArray = new JsonArray();
        for(int i = 0; i < nodeSaveUnit.getLogicFunctions().size(); i++) {
            LogicFunction logicFunction = nodeSaveUnit.getLogicFunctions().get(i);
            JsonObject functionObject = JsonWriter.getFunctionJsonObject(logicFunction, logicFunction.getFunctionIndex(), functionGraphicNodePositions[i]);
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

    public static void writeFunctionToFile(NodeSaveUnit nodeSaveUnit, String path, int functionIndex, Point[] functionGraphicNodePositions) {
        LogicFunction logicFunction = nodeSaveUnit.getLogicFunctions().get(functionIndex);
        JsonObject functionObject = JsonWriter.getFunctionJsonObject(logicFunction, 0, functionGraphicNodePositions);

        try {
            FileWriter file = new FileWriter(path);
            file.write(functionObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file successfully created");
    }

    private static JsonObject getFunctionJsonObject(LogicFunction logicFunction, int functionIndex, Point[] functionGraphicNodePositions) {
        JsonObject functionObject = new JsonObject();
        functionObject.addProperty(FUNCTION_INDEX, functionIndex);
        functionObject.addProperty(FUNCTION_NAME, logicFunction.getFunctionName());

        JsonArray functionNodesArray = new JsonArray();
        for(int j = 0; j < logicFunction.getLogicNodes().size(); j++) {
            LogicNode logicNode = logicFunction.getLogicNodes().get(j);
            functionNodesArray.add(JsonWriter.getNodeJsonObject(logicNode, functionIndex, functionGraphicNodePositions[j]));
        }
        functionObject.add(NODES, functionNodesArray);

        JsonArray functionConnectionsArray = new JsonArray();
        for(NodeConnection nodeConnection : logicFunction.getNodeConnections()) {
            functionConnectionsArray.add(JsonWriter.getNodeConnectionJsonObject(nodeConnection, functionIndex));
        }
        functionObject.add(NODE_CONNECTIONS, functionConnectionsArray);

        return functionObject;
    }

    private static JsonObject getNodeJsonObject(LogicNode logicNode, int functionIndex, Point position) {
        JsonObject nodeObject = new JsonObject();
        NodeType nodeType = logicNode.getNodeType();
        nodeObject.addProperty(NODE_INDEX, logicNode.getNodeIndex());
        nodeObject.addProperty(NODE_TYPE, nodeType.toString());
        if(!logicNode.getSpecificName().equals("")) {
            nodeObject.addProperty(SPECIFIC_NAME, logicNode.getSpecificName());
        }
        nodeObject.addProperty(X, position.x);
        nodeObject.addProperty(Y, position.y);
        Object[] extraParameters = logicNode.getExtraParameters();
        if(extraParameters.length > 0) {
            JsonArray extraParamArray = new JsonArray();
            InputDialogType[] inputDialogTypes = nodeType.getInputDialogTypes();
            for(int i = 0; i < extraParameters.length; i++) {
                JsonObject inputObject = new JsonObject();
                inputObject.addProperty(TYPE, inputDialogTypes[i].toString());
                String value;
                if((nodeType == NodeType._INPUT_PARAMETER_NODE || nodeType == NodeType._OUTPUT_PARAMETER_NODE) && i == 0) {
                    value = String.valueOf(functionIndex);
                } else {
                    value = InputDialogType.valueToString(inputDialogTypes[i], extraParameters[i]);
                }
                inputObject.addProperty(VALUE, value);
                extraParamArray.add(inputObject);
            }
            nodeObject.add(EXTRA_PARAMETERS, extraParamArray);
        }
        return nodeObject;
    }
    private static JsonObject getNodeConnectionJsonObject(NodeConnection nodeConnection, int functionIndex) {
        JsonObject connectionObject = new JsonObject();
        connectionObject.add(INPUT, JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getInputCoordinates(), functionIndex));
        connectionObject.add(OUTPUT, JsonWriter.getThreeCoordinateJsonObject(nodeConnection.getOutputCoordinates(), functionIndex));
        return connectionObject;
    }
    private static JsonObject getThreeCoordinateJsonObject(ThreeCoordinatePoint coordinatePoint, int functionIndex) {
        JsonObject coordinateObject = new JsonObject();
        coordinateObject.addProperty(C_1, functionIndex);
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

            //TODO Funktion einbeziehen
            JsonWriter.addFunction(functionObject, nodeControl, mainWindow, functionIndexOffset, functionCorrespondence);
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
            int x = currentNodeObject.has(X) ? currentNodeObject.get(X).getAsInt() : 0;
            int y = currentNodeObject.has(Y) ? currentNodeObject.get(Y).getAsInt() : 0;
            nodeControl.addNode(-1, nodeType, extraParamArray, new Point(x, y));
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

    public static void importFunction(String path, NodeControl nodeControl, MainWindow mainWindow) {
        JsonObject functionObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(path);
            functionObject = jsonParser.parse(fileReader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonWriter.addFunction(functionObject, nodeControl, mainWindow, nodeControl.getNextFreeFunctionIndex(), new TwoIntegerCorrespondence());
    }

    private static void addFunction(JsonObject functionObject, NodeControl nodeControl, MainWindow mainWindow, int functionIndexOffset, TwoIntegerCorrespondence functionCorrespondence) {
        int oldFunctionIndex = functionObject.get(FUNCTION_INDEX).getAsInt();
        int newFunctionIndex = functionIndexOffset + oldFunctionIndex;
        functionCorrespondence.addValue(oldFunctionIndex, newFunctionIndex);
        JsonArray functionNodesArray = functionObject.getAsJsonArray(NODES);
        JsonArray nodeConnectionsArray = functionObject.getAsJsonArray(NODE_CONNECTIONS);
        String functionName = functionObject.get(FUNCTION_NAME).getAsString();

        mainWindow.createFunction(functionName, new String[0], new JointType[0], new String[0], new JointType[0], nodeControl);

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
            int x = currentNodeObject.has(X) ? currentNodeObject.get(X).getAsInt() : 0;
            int y = currentNodeObject.has(Y) ? currentNodeObject.get(Y).getAsInt() : 0;
            nodeControl.addNode(newFunctionIndex, nodeType, extraParamArray, new Point(x, y));
        }

        //NODE CONNECTIONS
        for(int nodeConnectionIndex = 0; nodeConnectionIndex < nodeConnectionsArray.size(); nodeConnectionIndex++) {
            JsonObject currentConnectionObject = nodeConnectionsArray.get(nodeConnectionIndex).getAsJsonObject();

            NodeConnection nodeConnection = getNodeConnection(currentConnectionObject, functionCorrespondence, nodeIndexCorrespondence);
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

    public static void writeLedsToFile(LedSaveUnit ledSaveUnit, String path) {
        JsonObject finalObject = new JsonObject();

        finalObject.addProperty(LAYERS, ledSaveUnit.getLayerSize());

        JsonArray pixelArray = new JsonArray();
        for(Point pixel : ledSaveUnit.getPixels()) {
            JsonObject pixelObject = new JsonObject();
            pixelObject.addProperty(X, pixel.x);
            pixelObject.addProperty(Y, pixel.y);
            pixelArray.add(pixelObject);
        }
        finalObject.add(PIXELS, pixelArray);
        try {
            FileWriter file = new FileWriter(path);
            file.write(finalObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file successfully created");
    }

    public static void addLedsFromFile(String path, LedControl ledControl) {
        JsonObject ledObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(path);
            ledObject = jsonParser.parse(fileReader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(JsonElement pixelElement : ledObject.getAsJsonArray(PIXELS)) {
            try {
                ledControl.addPixel(
                        pixelElement.getAsJsonObject().get(X).getAsInt(),
                        pixelElement.getAsJsonObject().get(Y).getAsInt()
                );
            } catch (Exception e) {}
        }
    }

    public static void writeMaskToFile(Double[][] values, String path) {

        JsonObject maskObject = new JsonObject();

        JsonArray pixelArray = new JsonArray();
        int degree = (values.length - 1) / 2;
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                JsonObject currentPixel = new JsonObject();
                double intensity = values[x + degree][y + degree];
                if(intensity > 0) {
                    currentPixel.addProperty(X, x);
                    currentPixel.addProperty(Y, y);
                    currentPixel.addProperty(INTENSITY, intensity);
                    pixelArray.add(currentPixel);
                }
            }
        }
        maskObject.add(MASK, pixelArray);
        try {
            FileWriter file = new FileWriter(path);
            file.write(maskObject.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file successfully created");
    }

    public static LogicMask getMaskFromFile(String path) {
        LogicMask logicMask = new LogicMask();

        JsonObject ledObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            FileReader fileReader = new FileReader(path);
            ledObject = jsonParser.parse(fileReader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonArray pixelArray = ledObject.getAsJsonArray(MASK);
        for(JsonElement jsonElement : pixelArray) {
            JsonObject pixelObject = jsonElement.getAsJsonObject();
            int x = pixelObject.get(X).getAsInt();
            int y = pixelObject.get(Y).getAsInt();
            double intensity = pixelObject.get(INTENSITY).getAsDouble();
            logicMask.setIntensityAt(x, y, intensity);
        }
        return logicMask;
    }
}
