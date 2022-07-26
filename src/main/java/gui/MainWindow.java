package gui;

import control.event.TimeMeasure;
import control.led.LedControl;
import control.node.NodeControl;
import control.save.*;
import control.event.EventControl;
import control.song.SongControl;
import control.type_enums.*;
import gui.main_panels.event_panel.EventEditWindow;
import gui.main_panels.node_panel.FunctionTabbedPane;
import gui.main_panels.led_panel.LedEditWindow;
import gui.main_panels.node_panel.NodeEditWindow;
import gui.main_panels.player_panel.PlayButton;
import gui.main_panels.player_panel.SpotifyPlayerPanel;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private SpotifyPlayerPanel spotifyPlayerPanel;

    private NodeEditWindow nodeEditWindow;
    private EventEditWindow eventEditWindow;
    private FunctionTabbedPane functionTabbedPane;
    private LedEditWindow ledEditWindow;

    private PlayButton playButton;

    private JMenuBar jMenuBar;
    private JTabbedPane tabbedPane;

    JSplitPane splitPane;

    private final JMenu songMenu, nodeMenu, functionMenu, ledMenu, microControllerMenu, addFunctionMenu;
    private final ArrayList<JMenuItem> functionItemList;
    private final JMenu createTrackNodeMenu;

    private boolean bProjectOpen;

    public MainWindow(Dimension dimension, String title, SongControl songControl, EventControl eventControl, NodeControl nodeControl, LedControl ledControl) {
        super(title);

        this.setIconImage(new ImageIcon("images\\icon\\icon.png").getImage());
        this.bProjectOpen = false;
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setPreferredSize(dimension);
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT) {
            @Override
            public int getDividerLocation() {
                return 40;
            }
        };
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.tabbedPane.addTab("Spotify Player", null);
        this.tabbedPane.addTab("Events", null);
        this.tabbedPane.addTab("Nodes", null);
        this.tabbedPane.addTab("Functions", null);
        this.tabbedPane.addTab("LEDs", null);

        JPanel buttonPanel = new JPanel(null);
        JSlider songSlider = new JSlider(0, 1000, 0);

        this.playButton = new PlayButton(songControl);
        buttonPanel.add(this.playButton);
        this.playButton.setSize(100, 20);
        buttonPanel.add(songSlider);
        songSlider.setLocation(0, 20);
        songSlider.setMinorTickSpacing(1);
        this.splitPane.add(this.tabbedPane, JSplitPane.BOTTOM);
        this.splitPane.add(buttonPanel);
        this.splitPane.setDividerLocation(40);
        this.add(this.splitPane);
        this.jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newProject = new JMenuItem("New Project");
        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem saveProject = new JMenuItem("Save Project");
        JMenuItem exportTracks = new JMenuItem("Export Tracks");
        JMenuItem loadTracks = new JMenuItem("Add Tracks");
        JMenuItem exportTimeMeasures = new JMenuItem("Export Time Measures");
        JMenuItem loadTimeMeasures = new JMenuItem("Set Time Measures");
        JMenuItem exportNodes = new JMenuItem("Export Nodes");
        JMenuItem loadNodes = new JMenuItem("Load Nodes");
        JMenuItem exportLeds = new JMenuItem("Export LEDs");
        JMenuItem loadLeds = new JMenuItem("Load LEDs");
        JMenu importMenu = new JMenu("Import");
        importMenu.add(loadTracks);
        importMenu.add(loadTimeMeasures);
        importMenu.add(loadNodes);
        importMenu.add(loadLeds);
        JMenu exportMenu = new JMenu("Export");
        exportMenu.add(exportTracks);
        exportMenu.add(exportTimeMeasures);
        exportMenu.add(exportNodes);
        exportMenu.add(exportLeds);
        fileMenu.add(newProject);
        fileMenu.add(openProject);
        fileMenu.add(new JSeparator());
        fileMenu.add(saveProject);
        fileMenu.add(new JSeparator());
        fileMenu.add(importMenu);
        fileMenu.add(exportMenu);
        this.jMenuBar.add(fileMenu);
        this.setJMenuBar(jMenuBar);
        this.songMenu = new JMenu("Song");
        JMenu trackSubMenu = new JMenu("Track");
        JMenu rhythmSubMenu = new JMenu("Rhythm");
        JMenu curveSubMenu = new JMenu("Curve");
        JMenuItem addTrackMenuItem = new JMenuItem("Add Track");
        trackSubMenu.add(addTrackMenuItem);
        this.songMenu.add(trackSubMenu);

        JMenuItem editInputRhythmMenuItem = new JMenuItem("Edit Input Notation");
        editInputRhythmMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentSelection = TimeSignature.indexOf(eventEditWindow.getBarRoster());
                int selectedOption = Dialogues.getSelectedOptionFromArray(TimeSignature.getNameArray(), "Edit InputNotation", currentSelection);
                TimeSignature newTimeSignature = TimeSignature.values()[selectedOption];
                eventEditWindow.setBarRoster(newTimeSignature);
            }
        });
        rhythmSubMenu.add(editInputRhythmMenuItem);
        this.songMenu.add(rhythmSubMenu);

        JMenuItem defaultCurveItem = new JMenuItem("Default Curve");
        defaultCurveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentSelection = CurveType.indexOf(eventEditWindow.getDefaultCurveType());
                int selectedOption = Dialogues.getSelectedOptionFromArray(CurveType.values(), "Select Default Curve Type", currentSelection);
                eventEditWindow.setDefaultCurveType(CurveType.values()[selectedOption]);
            }
        });
        curveSubMenu.add(defaultCurveItem);

        JCheckBoxMenuItem curveBrushItem = new JCheckBoxMenuItem("Curve Brush");
        curveBrushItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventEditWindow.setCurveBrush(curveBrushItem.isSelected());
            }
        });

        curveSubMenu.add(curveBrushItem);
        this.songMenu.add(curveSubMenu);
        this.songMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                curveBrushItem.setSelected(eventEditWindow.getCurveBrush());
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        this.songMenu.add(new JSeparator());

        this.createTrackNodeMenu = new JMenu("Create Track Node");
        addTrackMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int trackCount = eventControl.getTrackCount();
                eventControl.onAddTrackRequest();
                if(trackCount < eventControl.getTrackCount()) {
                    String trackName = "Track " + eventControl.getTrackCount();
                    JMenuItem newTrackNodeItem = createTrackToNodeItem(trackName, trackCount, nodeControl);
                    createTrackNodeMenu.add(newTrackNodeItem);
                }
            }
        });

        this.songMenu.add(createTrackNodeMenu);
        this.songMenu.setEnabled(false);
        this.jMenuBar.add(this.songMenu);

        this.nodeMenu = new JMenu("Node");
        ArrayList<String> nodeCategories = new ArrayList<>();
        for(NodeType nodeType : NodeType.values()) {
            if(!nodeCategories.contains(nodeType.getCategoryName())) {
                nodeCategories.add(nodeType.getCategoryName());
            }
        }
        JMenu[] subMenus = new JMenu[nodeCategories.size()];
        for(int i = 0; i < subMenus.length; i++) {
            subMenus[i] = new JMenu(nodeCategories.get(i));
            this.nodeMenu.add(subMenus[i]);
        }
        for(int i = 0; i < NodeType.values().length - 1; i++) {
            //TODO LayerNode (noch) nicht hinzufügbar durch Menu
            NodeType nodeType = NodeType.values()[i];
            String currentCategory = nodeType.getCategoryName();
            JMenuItem currentNodeMenuItem = new JMenuItem(nodeType.getName());
            currentNodeMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    InputDialogType[] inputDialogTypes = nodeType.getInputDialogTypes();
                    Object[] extraParameters = new Object[inputDialogTypes.length];

                    for(int i = 0; i < inputDialogTypes.length; i++) {
                        String message = inputDialogTypes[i].getMessage();
                        switch(inputDialogTypes[i]) {
                            case JOINT_TYPE_INPUT -> {
                                int selectedOption = Dialogues.getSelectedOptionFromArray(JointType.getNames(), message, 0);
                                extraParameters[i] = JointType.values()[selectedOption];
                            }
                            case NUMBER_TYPE_INPUT -> {
                                extraParameters[i] = Dialogues.getNumberValue(message);
                            }
                            case COLOR_TYPE_INPUT -> {
                                Color pickedColor = JColorChooser.showDialog(null, message, null);
                                extraParameters[i] = pickedColor;
                            }
                            case INTEGER_TYPE_INPUT -> {
                                extraParameters[i] = Dialogues.getIntegerValue(message);
                            }
                            case UNIT_NUMBER_TYPE_INPUT -> {
                                double value = Dialogues.getNumberValue(message);
                                if(value > 1.d) value = 1.d;
                                else if(value < 0.d) value = 0.d;
                                extraParameters[i] = value;
                            }
                            case STRING_TYPE_INPUT -> {
                                String name = JOptionPane.showInputDialog("Enter name");
                                extraParameters[i] = name;
                            }
                            case ROUND_PIXEL_INPUT -> {
                                JComboBox algorithmComboBox = new JComboBox(PixelAlgorithmType.values());
                                JOptionPane.showOptionDialog(
                                        null, algorithmComboBox, message,
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
                                );
                                extraParameters[i] = PixelAlgorithmType.values()[algorithmComboBox.getSelectedIndex()];
                            }
                            case ROUND_INPUT -> {
                                int selectedOption = Dialogues.getSelectedOptionFromArray(RoundAlgorithmType.values(), message, 0);
                                extraParameters[i] = RoundAlgorithmType.values()[selectedOption];
                            }
                            case COLOR_MIXING_INPUT -> {
                                int selectedOption = Dialogues.getSelectedOptionFromArray(MixingAlgorithmType.values(), message, 0);
                                extraParameters[i] = MixingAlgorithmType.values()[selectedOption];
                            }
                            case UPDATE_INPUT -> {
                                int selectedOption = Dialogues.getSelectedOptionFromArray(UpdateType.values(), message, 0);
                                extraParameters[i] = UpdateType.values()[selectedOption];
                            }
                            case BLEND_INPUT -> {
                                int selectedOption = Dialogues.getSelectedOptionFromArray(BlendType.values(), message, 0);
                                extraParameters[i] = BlendType.values()[selectedOption];
                            }
                            case JSON_INPUT -> {
                                JFileChooser fileOpenChooser = new JFileChooser();
                                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                                fileOpenChooser.setFileFilter(serializedFilter);
                                int returnValue = fileOpenChooser.showOpenDialog(getParent());
                                extraParameters[i] = returnValue == JFileChooser.APPROVE_OPTION ? fileOpenChooser.getSelectedFile().getPath() : "";
                            }
                        }
                    }
                    if(tabbedPane.getSelectedComponent() == nodeEditWindow) {
                        nodeControl.addNode(-1, nodeType, extraParameters, new Point(0, 0));
                    } else if(tabbedPane.getSelectedComponent() == functionTabbedPane) {
                        nodeControl.addNode(functionTabbedPane.getSelectedIndex(), nodeType, extraParameters, new Point(0, 0));
                    }
                }
            });
            subMenus[nodeCategories.indexOf(currentCategory)].add(currentNodeMenuItem);
        }
        jMenuBar.add(this.nodeMenu);
        this.nodeMenu.setEnabled(false);
        this.functionMenu = new JMenu("Function");
        JMenuItem createFunctionMenuItem = new JMenuItem("Create Function");
        this.addFunctionMenu = new JMenu("Add Function");
        JMenuItem cleanUpCanvas = new JMenuItem("Clean up Canvas");
        JCheckBoxMenuItem gridMenuItem = new JCheckBoxMenuItem("Grid Active");
        this.functionItemList = new ArrayList<>();

        createFunctionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String functionName = JOptionPane.showInputDialog("Create Function");
                if(functionName != null) {
                    int numberInputs, numberOutputs;
                    String[] inputNames, outputNames;
                    JointType[] inputTypes, outputTypes;

                    JComboBox comboBox = new JComboBox(new Integer[] {0, 1, 2, 3, 4, 5 });

                    JOptionPane.showOptionDialog(null, comboBox, "Number Inputs", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    numberInputs = comboBox.getSelectedIndex();
                    inputNames = new String[numberInputs];
                    inputTypes = new JointType[numberInputs];

                    JOptionPane.showOptionDialog(null, comboBox, "Number Outputs", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    numberOutputs = comboBox.getSelectedIndex();
                    outputNames = new String[numberOutputs];
                    outputTypes = new JointType[numberOutputs];

                    for(int i = 0; i < inputNames.length; i++) {
                        inputNames[i] = JOptionPane.showInputDialog("Input " + (i + 1) + " Name");
                        JComboBox jointTypeComboBox = new JComboBox(JointType.values());
                        JOptionPane.showOptionDialog(null, jointTypeComboBox, "Input " + (i + 1) + " Type", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        inputTypes[i] = JointType.values()[jointTypeComboBox.getSelectedIndex()];
                    }

                    for(int i = 0; i < outputNames.length; i++) {
                        outputNames[i] = JOptionPane.showInputDialog("Output " + (i + 1) + " Name");
                        JComboBox jointTypeComboBox = new JComboBox(JointType.values());
                        JOptionPane.showOptionDialog(null, jointTypeComboBox, "Output " + (i + 1) + " Type", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        outputTypes[i] = JointType.values()[jointTypeComboBox.getSelectedIndex()];
                    }
                    createFunction(functionName, inputNames, inputTypes, outputNames, outputTypes, nodeControl);
                }
            }
        });
        cleanUpCanvas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabbedPane.getSelectedIndex() == 2) {
                    nodeEditWindow.cleanUpCanvas();
                } else if(tabbedPane.getSelectedIndex() == 3) {
                    functionTabbedPane.getFunctionEditWindows().get(functionTabbedPane.getSelectedIndex()).cleanUpCanvas();
                }
            }
        });

        gridMenuItem.addActionListener(e -> {
            nodeEditWindow.setGridActive(gridMenuItem.isSelected());
            nodeEditWindow.repaint();
        });

        this.functionMenu.add(createFunctionMenuItem);
        this.functionMenu.add(addFunctionMenu);
        this.functionMenu.add(new JSeparator());
        this.functionMenu.add(cleanUpCanvas);
        this.functionMenu.add(gridMenuItem);
        jMenuBar.add(this.functionMenu);
        this.functionMenu.setEnabled(false);

        this.ledMenu = new JMenu("LED");
        JMenuItem addLayerMenuItem = new JMenuItem("Add Layer");
        addLayerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int currentLayerCount = ledControl.getLayerCount();
                ledControl.onAddLayerRequest();
                if(currentLayerCount < ledControl.getLayerCount()) {
                    nodeControl.addLayerNode(
                            ledControl.updateTextureFunction(currentLayerCount),
                            "Layer " + (currentLayerCount + 1),
                            new Point(0, 0)
                    );
                }
            }
        });
        this.ledMenu.add(addLayerMenuItem);

        JMenuItem addPixelMenuItem = new JMenuItem("Add LED Pixel");
        addPixelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = getIntegerFieldValue("Set X");
                int y = getIntegerFieldValue("Set Y");

                try {
                    ledControl.addPixel(x, y);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.ledMenu.add(new JSeparator());
        this.ledMenu.add(addPixelMenuItem);

        JMenuItem addPixelMatrix = new JMenuItem("Add Pixel Matrix");
        addPixelMatrix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int xFrom = getIntegerFieldValue("Set X (1)");
                int yFrom = getIntegerFieldValue("Set Y (1)");
                int xTo = getIntegerFieldValue("Set X (2)");
                int yTo = getIntegerFieldValue("Set Y (2)");

                for(int x = Math.min(xFrom, xTo); x <= Math.max(xFrom, xTo); x++) {
                    for(int y = Math.min(yFrom, yTo); y <= Math.max(yFrom, yTo); y++) {
                        try {
                            ledControl.addPixel(x, y);
                        } catch(Exception ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        this.ledMenu.add(addPixelMatrix);

        JMenuItem onlyDrawLedsItem = new JCheckBoxMenuItem("Only Draw LEDs");
        onlyDrawLedsItem.addActionListener(e -> {
            ledEditWindow.setDrawOnlyLedPixels(onlyDrawLedsItem.isSelected());
            ledEditWindow.repaint();
        });
        this.ledMenu.add(new JSeparator());
        this.ledMenu.add(onlyDrawLedsItem);

        JMenuItem showIndexItem = new JCheckBoxMenuItem("Show Indexes");
        showIndexItem.addActionListener(e -> {
            ledEditWindow.showIndexes(showIndexItem.isSelected());
            ledEditWindow.repaint();
        });
        this.ledMenu.add(showIndexItem);

        JMenuItem orderModeItem = new JCheckBoxMenuItem("Order Mode");
        orderModeItem.addActionListener(e -> {
            boolean submit = false;
            if(!orderModeItem.isSelected()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Do you want to submit the order changes?", "Submit Order", JOptionPane.YES_NO_OPTION);
                submit = confirm == JOptionPane.YES_OPTION;
            }
            ledEditWindow.setOrderMode(orderModeItem.isSelected(), submit);
            ledEditWindow.repaint();

            for(int i = 0; i < ledMenu.getItemCount(); i++) {
                if(ledMenu.getItem(i) != orderModeItem && ledMenu.getItem(i) != null) {
                    ledMenu.getItem(i).setEnabled(!orderModeItem.isSelected());
                }
            }
        });
        this.ledMenu.add(orderModeItem);

        this.jMenuBar.add(this.ledMenu);
        this.ledMenu.setEnabled(false);

        this.microControllerMenu = new JMenu("Micro Controller");

        JMenuItem refreshPortItem = new JMenuItem("Refresh Ports");
        this.microControllerMenu.add(refreshPortItem);

        JMenu availablePortsMenu = new JMenu("Available Ports");

        refreshPortItem.addActionListener(e -> {
            availablePortsMenu.removeAll();
            String[] currentPortNames = ledControl.onGetPortsRequest();
            for(int i = 0; i < currentPortNames.length; i++) {
                JMenuItem newPortItem = new JMenuItem(currentPortNames[i]);
                int finalI = i;
                newPortItem.addActionListener(a -> {
                    ledControl.onOpenPortRequest(finalI);
                });
                availablePortsMenu.add(newPortItem);
            }
        });
        this.microControllerMenu.add(availablePortsMenu);
        this.microControllerMenu.add(new JSeparator());

        JMenuItem uploadColorsItem = new JMenuItem("Upload Current Colors");
        uploadColorsItem.addActionListener(e -> {
            ledControl.updatePort();
        });
        this.microControllerMenu.add(uploadColorsItem);

        JCheckBoxMenuItem updateEveryTickItem = new JCheckBoxMenuItem("Update Colors Every Tick");
        updateEveryTickItem.addActionListener(e -> {
            this.ledEditWindow.setUpdatePortWhenRepaint(updateEveryTickItem.isSelected());
        });
        this.microControllerMenu.add(updateEveryTickItem);

        this.jMenuBar.add(this.microControllerMenu);
        this.microControllerMenu.setEnabled(false);

        this.pack();
        this.setCorrectLocation();

        newProject.addActionListener(e -> {
            if(this.bProjectOpen) {
                //TODO: Abfangen, dass im Zweifelsfall das geöffnete Programm nicht gespeichert wird
            }
            newProject(songControl, eventControl, nodeControl, ledControl, dimension);
            pack();
            setCorrectLocation();
            repaint();
            bProjectOpen = true;
        });

        openProject.addActionListener(e -> {
            JFileChooser fileOpenChooser = new JFileChooser();
            FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("Spotify LED Control", "ledcontrol");
            fileOpenChooser.setFileFilter(serializedFilter);
            int returnValue = fileOpenChooser.showOpenDialog(getParent());
            if(returnValue == JFileChooser.APPROVE_OPTION) {
                DataStore data = DataStore.readFromFile(fileOpenChooser.getSelectedFile().getPath());
                if(data != null) {

                    newProject(songControl, eventControl, nodeControl, data.getLedControl(), dimension);
                    eventControl.reinitialize(data.getEventSaveUnit());
                    nodeControl.reinitialize(data.getNodeSaveUnit());
                    nodeEditWindow.updateGraphicNodes(data.getNodeEditGraphicNodePositions());
                    functionTabbedPane.updateFunctions(data.getFunctionEditGraphicNodePositions());

                    enableTabs();
                }
            }
        });

        saveProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point[] nodeEditGraphicNodePositions = bakeGraphicNodePositions();
                Point[][] functionEditGraphicNodePositions = bakeFunctionGraphicNodePositions();
                DataStore dataStore = new DataStore(
                        eventControl.createEventSaveUnit(),
                        nodeControl.createNodeSaveUnit(),
                        ledControl,
                        nodeEditGraphicNodePositions,
                        functionEditGraphicNodePositions
                );

                JFileChooser fileSaveChooser = Dialogues.getDefaultFileSaveChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("Spotify LED Control", "ledcontrol");
                fileSaveChooser.setFileFilter(serializedFilter);
                fileSaveChooser.setSelectedFile(new File("new_song.ledcontrol"));
                int returnValue = fileSaveChooser.showSaveDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    DataStore.writeToFile(fileSaveChooser.getSelectedFile().getPath(), dataStore);
                }
            }
        });

        exportTracks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventSaveUnit eventSaveUnit = eventControl.createEventSaveUnit();

                JFileChooser fileSaveChooser = Dialogues.getDefaultFileSaveChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileSaveChooser.setFileFilter(serializedFilter);
                fileSaveChooser.setSelectedFile(new File("tracks.json"));
                int returnValue = fileSaveChooser.showSaveDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.writeTracksToFile(eventSaveUnit, fileSaveChooser.getSelectedFile().getPath());
                }
            }
        });

        loadTracks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpenChooser = new JFileChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileOpenChooser.setFileFilter(serializedFilter);
                int returnValue = fileOpenChooser.showOpenDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.addTracksFromFile(fileOpenChooser.getSelectedFile().getPath(), eventControl);
                }
            }
        });

        exportNodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NodeSaveUnit nodeSaveUnit = nodeControl.createNodeSaveUnit();
                Point[] graphicNodePositions = bakeGraphicNodePositions();
                Point[][] functionGraphicNodePositions = bakeFunctionGraphicNodePositions();

                JFileChooser fileSaveChooser = Dialogues.getDefaultFileSaveChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileSaveChooser.setFileFilter(serializedFilter);
                fileSaveChooser.setSelectedFile(new File("nodes.json"));
                int returnValue = fileSaveChooser.showSaveDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.writeNodesToFile(nodeSaveUnit, fileSaveChooser.getSelectedFile().getPath(), graphicNodePositions, functionGraphicNodePositions);
                }
            }
        });

        MainWindow thisWindow = this;
        loadNodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpenChooser = new JFileChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileOpenChooser.setFileFilter(serializedFilter);
                int returnValue = fileOpenChooser.showOpenDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.addNodesFromFile(fileOpenChooser.getSelectedFile().getPath(), nodeControl, thisWindow);
                }
            }
        });

        exportLeds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LedSaveUnit ledSaveUnit = ledControl.getLedSaveUnit();

                JFileChooser fileSaveChooser = Dialogues.getDefaultFileSaveChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileSaveChooser.setFileFilter(serializedFilter);
                fileSaveChooser.setSelectedFile(new File("leds.json"));
                int returnValue = fileSaveChooser.showSaveDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.writeLedsToFile(ledSaveUnit, fileSaveChooser.getSelectedFile().getPath());
                }
            }
        });

        loadLeds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpenChooser = new JFileChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("JSON", "json");
                fileOpenChooser.setFileFilter(serializedFilter);
                int returnValue = fileOpenChooser.showOpenDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    JsonWriter.addLedsFromFile(fileOpenChooser.getSelectedFile().getPath(), ledControl);
                }
            }
        });

        this.setFocusable(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension newSize = getContentPane().getSize();
                playButton.setLocation((newSize.width - playButton.getSize().width) / 2, 0);
                songSlider.setSize(newSize.width, 20);

                if(spotifyPlayerPanel != null) spotifyPlayerPanel.resizeComponents(newSize);
                if(ledEditWindow != null) ledEditWindow.resizeComponents(newSize);
                repaint();
                repaintWindows(eventControl, nodeControl);

                //TODO : other tabs
            }
        });
    }

    public void createFunction(String functionName, String[] inputNames, JointType[] inputTypes, String[] outputNames, JointType[] outputTypes, NodeControl nodeControl) {
        int newPanelIndex = this.functionTabbedPane.addPanel(functionName);
        this.functionTabbedPane.onFunctionCreated(newPanelIndex, functionName, inputNames, inputTypes, outputNames, outputTypes);
        this.tabbedPane.setSelectedComponent(this.functionTabbedPane);
        this.functionTabbedPane.setSelectedIndex(this.functionTabbedPane.getTabCount() - 1);
        JMenuItem addNewFunctionItem = new JMenuItem(functionName);
        this.addFunctionMenu.add(addNewFunctionItem);
        this.functionItemList.add(addNewFunctionItem);
        addNewFunctionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentFunctionIndex = functionItemList.indexOf(addNewFunctionItem);
                if(tabbedPane.getSelectedIndex() == 2) {
                    nodeControl.addFunctionNode(currentFunctionIndex, -1, new Point(0, 0));
                } else if(tabbedPane.getSelectedIndex() == 3) {
                    int selectedFunctionIndex = functionTabbedPane.getSelectedIndex();
                    nodeControl.addFunctionNode(currentFunctionIndex, selectedFunctionIndex, new Point(0, 0));
                }
            }
        });
    }

    private Point[] bakeGraphicNodePositions() {
        Point[] graphicNodePositions = new Point[nodeEditWindow.getGraphicNodes().size()];
        for(int i = 0; i < graphicNodePositions.length; i++) {
            graphicNodePositions[i] = nodeEditWindow.getGraphicNodes().get(i).getLocation();
        }
        return graphicNodePositions;
    }

    private Point[][] bakeFunctionGraphicNodePositions() {
        Point[][] functionGraphicNodePositions = new Point[functionTabbedPane.getFunctionEditWindows().size()][];
        for(int i = 0; i < functionGraphicNodePositions.length; i++) {
            functionGraphicNodePositions[i] = new Point[functionTabbedPane.getFunctionEditWindows().get(i).getGraphicNodes().size()];
            for(int j = 0; j < functionGraphicNodePositions[i].length; j++) {
                functionGraphicNodePositions[i][j] = functionTabbedPane.getFunctionEditWindows().get(i).getGraphicNodes().get(j).getLocation();
            }
        }
        return functionGraphicNodePositions;
    }

    private void newProject(SongControl songControl, EventControl eventControl, NodeControl nodeControl, LedControl ledControl, Dimension dimension) {

        this.spotifyPlayerPanel = new SpotifyPlayerPanel(songControl, eventControl, dimension);
        this.eventEditWindow = new EventEditWindow(eventControl);
        this.nodeEditWindow = new NodeEditWindow(nodeControl);
        this.functionTabbedPane = new FunctionTabbedPane(nodeControl);
        this.ledEditWindow = new LedEditWindow(ledControl, dimension);

        this.nodeEditWindow.setPreferredSize(dimension);

        this.enableTabs();
    }

    private JMenuItem createTrackToNodeItem(String trackName, int trackIndex, NodeControl nodeControl) {
        JMenuItem trackToNodeItem = new JMenuItem(trackName);
        trackToNodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : Track Node wird "gebacken"
                if(tabbedPane.getSelectedComponent() == nodeEditWindow) {
                    nodeControl.addTrackNode(trackIndex, new Point(0, 0));
                } else if(tabbedPane.getSelectedComponent() == functionTabbedPane) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to create Track Nodes inside Function",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        return trackToNodeItem;
    }

    private void enableTabs() {
        this.tabbedPane.setComponentAt(0, this.spotifyPlayerPanel);
        this.tabbedPane.setComponentAt(1, new JScrollPane(
                this.eventEditWindow,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ));
        this.tabbedPane.setComponentAt(2, this.nodeEditWindow);
        this.tabbedPane.setComponentAt(3, this.functionTabbedPane);

        JSplitPane ledSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
            @Override
            public int getDividerLocation() {
                return this.getWidth() - 200;
            }
        };
        ledSplitPane.add(this.ledEditWindow, JSplitPane.LEFT);
        ledSplitPane.add(new JScrollPane(
                this.ledEditWindow.getLayersPanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        ), JSplitPane.RIGHT);
        this.tabbedPane.setComponentAt(4, ledSplitPane);

        this.songMenu.setEnabled(true);
        this.nodeMenu.setEnabled(true);
        this.functionMenu.setEnabled(true);
        this.ledMenu.setEnabled(true);
        this.microControllerMenu.setEnabled(true);

        this.pack();
        this.repaint();
    }

    private void setCorrectLocation() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int posX = (screenSize.width - this.getWidth()) / 2;
        int posY = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(posX, posY);
    }

    public void repaintWindows(EventControl eventControl, NodeControl nodeControl) {
        if(this.spotifyPlayerPanel != null) this.spotifyPlayerPanel.repaint();
        if(this.eventEditWindow != null) this.eventEditWindow.repaint();
        if(this.nodeEditWindow != null) this.nodeEditWindow.repaint();
        if(this.functionTabbedPane != null) this.functionTabbedPane.repaint();
        if(this.ledEditWindow != null) this.ledEditWindow.repaint();

        if(this.createTrackNodeMenu.getItemCount() != eventControl.getTrackCount()) {
            while(this.createTrackNodeMenu.getItemCount() != 0) {
                this.createTrackNodeMenu.remove(0);
            }
            for(int i = 0; i < eventControl.getTrackCount(); i++) {
                String trackName = "Track " + i;
                JMenuItem newTrackNodeItem = createTrackToNodeItem(trackName, i, nodeControl);
                this.createTrackNodeMenu.add(newTrackNodeItem);
            }
        }
        this.playButton.updateIcon();
        this.repaint();
    }

    public int getIntegerFieldValue(String message) {
        JFormattedTextField numberTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        numberTextField.setValue(0);
        numberTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        JOptionPane.showOptionDialog(
                null, numberTextField, message,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
        );
        if(numberTextField.getValue().getClass() == Long.class) {
            return ((Long) numberTextField.getValue()).intValue();
        } else {
            return (int)numberTextField.getValue();
        }
    }
}