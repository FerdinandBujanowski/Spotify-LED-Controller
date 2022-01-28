package gui;

import control.song.SongControl;
import control.save.DataStorer;
import control.type_enums.*;
import control.node.NodeControl;
import gui.main_panels.event_panel.EventEditWindow;
import gui.main_panels.function_panels.FunctionTabbedPane;
import gui.main_panels.node_panel.NodeEditWindow;
import gui.main_panels.player_panel.SpotifyPlayerPanel;

import javax.swing.*;
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

    private JMenuBar jMenuBar;
    private JTabbedPane tabbedPane;

    JSplitPane splitPane;

    private JMenu songMenu, nodeMenu, functionMenu;

    private boolean bProjectOpen;

    public MainWindow(Dimension dimension, String title, NodeControl nodeControl, SongControl songControl) {
        super(title);

        this.setIconImage(new ImageIcon("images\\icon\\icon.png").getImage());

        this.bProjectOpen = false;

        this.setResizable(false);
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
        this.tabbedPane.addTab("LED's", null);

        JPanel buttonPanel = new JPanel(null);
        JButton playButton = new JButton("|>/||");
        JSlider songSlider = new JSlider(0, 1000, 0);
        buttonPanel.add(playButton);
        playButton.setSize(100, 20);
        playButton.setLocation((dimension.width - playButton.getSize().width) / 2, 0);
        buttonPanel.add(songSlider);
        songSlider.setSize(dimension.width, 20);
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
        fileMenu.add(newProject);
        fileMenu.add(openProject);
        fileMenu.add(new JSeparator());
        fileMenu.add(saveProject);
        this.jMenuBar.add(fileMenu);
        this.setJMenuBar(jMenuBar);

        this.songMenu = new JMenu("Song");
        JMenu trackSubMenu = new JMenu("Track");
        JMenu eventSubMeu = new JMenu("Event");
        JMenu rhythmSubMenu = new JMenu("Rhythm");

        JMenuItem addTrackMenuItem = new JMenuItem("Add Track");
        addTrackMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songControl.onAddTrackRequest();
            }
        });
        trackSubMenu.add(addTrackMenuItem);

        this.songMenu.add(trackSubMenu);

        this.songMenu.add(eventSubMeu);

        JMenuItem editInputRhythmMenuItem = new JMenuItem("Edit Input Notation");
        editInputRhythmMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox notationTypeComboBox = new JComboBox(TimeSignature.getNameArray());
                notationTypeComboBox.setSelectedIndex(TimeSignature.indexOf(eventEditWindow.getBarRoster()));
                JOptionPane.showOptionDialog(null, notationTypeComboBox, "Edit Input Notation", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                TimeSignature newTimeSignature = TimeSignature.values()[notationTypeComboBox.getSelectedIndex()];
                eventEditWindow.setBarRoster(newTimeSignature);
            }
        });
        rhythmSubMenu.add(editInputRhythmMenuItem);
        this.songMenu.add(rhythmSubMenu);

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
        for(NodeType nodeType : NodeType.values()) {
            String currentCategory = nodeType.getCategoryName();
            JMenuItem currentNodeMenuItem = new JMenuItem(nodeType.getName());
            currentNodeMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    InputDialogType[] inputDialogTypes = nodeType.getInputDialogTypes();
                    Object[] extraParameters = new Object[inputDialogTypes.length];

                    for(int i = 0; i < inputDialogTypes.length; i++) {
                        switch(inputDialogTypes[i]) {
                            case JOINT_TYPE_INPUT -> {
                                JComboBox jointTypeComboBox = new JComboBox(JointType.values());
                                JOptionPane.showOptionDialog(
                                        null, jointTypeComboBox, inputDialogTypes[i].getMessage(),
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
                                );
                                extraParameters[i] = (JointType) JointType.values()[jointTypeComboBox.getSelectedIndex()];
                            }
                            case NUMBER_TYPE_INPUT -> {
                                JFormattedTextField numberTextField = new JFormattedTextField(NumberFormat.getNumberInstance());
                                numberTextField.setValue(0.0);
                                numberTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
                                JOptionPane.showOptionDialog(
                                        null, numberTextField, inputDialogTypes[i].getMessage(),
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
                                );
                                extraParameters[i] = numberTextField.getValue();
                                if(extraParameters[i].getClass() == Long.class) {
                                    extraParameters[i] = ((Long) numberTextField.getValue()).doubleValue();
                                }
                            }
                            case COLOR_TYPE_INPUT -> {
                                Color pickedColor = JColorChooser.showDialog(null, inputDialogTypes[i].getMessage(), null);
                                extraParameters[i] = pickedColor;
                            }
                            case INTEGER_TYPE_INPUT -> {
                                JFormattedTextField numberTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
                                numberTextField.setValue(0);
                                numberTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
                                JOptionPane.showOptionDialog(
                                        null, numberTextField, inputDialogTypes[i].getMessage(),
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
                                );
                                extraParameters[i] = numberTextField.getValue();
                                if(extraParameters[i].getClass() == Long.class) {
                                    extraParameters[i] = ((Long) numberTextField.getValue()).intValue();
                                }
                            }
                            case UNIT_NUMBER_TYPE_INPUT -> {
                                //TODO: Textfeld formatieren, dass nur Doubles zwischen 0 und 1 erlaubt sind

                            }
                        }
                    }
                    if(tabbedPane.getSelectedComponent() == nodeEditWindow) {
                        nodeEditWindow.addLogicNodeToNodeControl(nodeType, 10, 10, extraParameters);
                    } else if(tabbedPane.getSelectedComponent() == functionTabbedPane) {
                        functionTabbedPane.getFunctionEditWindows().get(functionTabbedPane.getSelectedIndex()).addLogicNodeToNodeControl(nodeType, 10, 10, extraParameters);
                    }
                }
            });
            subMenus[nodeCategories.indexOf(currentCategory)].add(currentNodeMenuItem);
        }
        jMenuBar.add(this.nodeMenu);
        this.nodeMenu.setEnabled(false);

        this.functionMenu = new JMenu("Function");

        JMenuItem createFunctionMenuItem = new JMenuItem("Create Function");
        JMenu addFunctionMenu = new JMenu("Add Function");
        JMenu addFunctionInstance = new JMenu("Add Function Instance");

        ArrayList<JMenuItem> functionItemList = new ArrayList<>();
        ArrayList<JMenuItem> functionInstanceItemList = new ArrayList<>();

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

                    int newPanelIndex = functionTabbedPane.addPanel(functionName);
                    functionTabbedPane.onFunctionCreated(newPanelIndex, inputNames, inputTypes, outputNames, outputTypes);

                    tabbedPane.setSelectedComponent(functionTabbedPane);
                    functionTabbedPane.setSelectedIndex(functionTabbedPane.getTabCount() - 1);

                    JMenuItem addNewFunctionItem = new JMenuItem(functionName);
                    JMenuItem addNewFunctionInstanceItem = new JMenuItem(functionName);

                    addFunctionMenu.add(addNewFunctionItem);
                    addFunctionInstance.add(addNewFunctionInstanceItem);

                    functionItemList.add(addNewFunctionItem);
                    functionInstanceItemList.add(addNewFunctionInstanceItem);

                    addNewFunctionItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int currentFunctionIndex = functionItemList.indexOf(addNewFunctionItem);
                            if(tabbedPane.getSelectedIndex() == 2) {
                                nodeEditWindow.addFunctionNode(currentFunctionIndex, functionName, 10, 10);
                            } else if(tabbedPane.getSelectedIndex() == 3) {
                                int selectedFunctionIndex = functionTabbedPane.getSelectedIndex();
                                functionTabbedPane.getFunctionEditWindows().get(selectedFunctionIndex).addFunctionNode(currentFunctionIndex, functionName, 10, 10);
                            }
                        }
                    });

                    addNewFunctionInstanceItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //TODO: Function Instance Creation weiterleiten
                            int currentFunctionIndex = functionInstanceItemList.indexOf(addNewFunctionInstanceItem);
                            if(tabbedPane.getSelectedIndex() == 2) {
                                nodeEditWindow.addFunctionInstanceNode(currentFunctionIndex, functionName, 10, 10);
                            } else if(tabbedPane.getSelectedIndex() == 3) {
                                int selectedFunctionIndex = functionTabbedPane.getSelectedIndex();
                                functionTabbedPane.getFunctionEditWindows().get(selectedFunctionIndex).addFunctionInstanceNode(currentFunctionIndex, functionName, 10, 10);
                            }
                        }
                    });
                }
            }
        });
        this.functionMenu.add(createFunctionMenuItem);
        this.functionMenu.add(new JSeparator());
        this.functionMenu.add(addFunctionMenu);
        this.functionMenu.add(addFunctionInstance);
        jMenuBar.add(this.functionMenu);
        this.functionMenu.setEnabled(false);

        this.pack();
        this.setCorrectLocation();

        newProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(bProjectOpen) {
                    //TODO: Abfangen, dass im Zweifelsfall das geöffnete Programm nicht gespeichert wird
                }
                newProject(nodeControl, songControl, dimension);
                pack();
                setCorrectLocation();
                repaint();
                bProjectOpen = true;
            }
        });

        openProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpenChooser = new JFileChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("Spotify LED Control", "ledcontrol");
                fileOpenChooser.setFileFilter(serializedFilter);
                //fileOpenChooser.setCurrentDirectory(new File("params"));
                int returnValue = fileOpenChooser.showOpenDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    DataStorer data = DataStorer.readFromFile(fileOpenChooser.getSelectedFile().getPath());
                    if(data != null) {
                        newProject(data.getNodeControl(), data.getSongControl(), dimension);

                        nodeEditWindow.updateGraphicNodes(data.getNodeEditGraphicNodePositions());
                        functionTabbedPane.updateFunctions(data.getFunctionEditGraphicNodePositions());

                        enableTabs();
                    }
                }
            }
        });

        saveProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point[] nodeEditGraphicNodePositions = new Point[nodeEditWindow.getGraphicNodes().size()];
                for(int i = 0; i < nodeEditGraphicNodePositions.length; i++) {
                    nodeEditGraphicNodePositions[i] = nodeEditWindow.getGraphicNodes().get(i).getLocation();
                }
                Point[][] functionEditGraphicNodePositions = new Point[functionTabbedPane.getFunctionEditWindows().size()][];
                for(int i = 0; i < functionEditGraphicNodePositions.length; i++) {
                    functionEditGraphicNodePositions[i] = new Point[functionTabbedPane.getFunctionEditWindows().get(i).getGraphicNodes().size()];
                    for(int j = 0; j < functionEditGraphicNodePositions[i].length; j++) {
                        functionEditGraphicNodePositions[i][j] = functionTabbedPane.getFunctionEditWindows().get(i).getGraphicNodes().get(j).getLocation();
                    }
                }
                DataStorer dataStorer = new DataStorer(
                        null,
                        nodeControl,
                        songControl,
                        nodeEditGraphicNodePositions,
                        functionEditGraphicNodePositions
                );

                JFileChooser fileSaveChooser = getDefaultFileSaveChooser();
                FileNameExtensionFilter serializedFilter = new FileNameExtensionFilter("Spotify LED Control", "ledcontrol");
                fileSaveChooser.setFileFilter(serializedFilter);
                //fileSaveChooser.setCurrentDirectory(new File("params"));
                fileSaveChooser.setSelectedFile(new File("new_song.ledcontrol"));
                int returnValue = fileSaveChooser.showSaveDialog(getParent());
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    DataStorer.writeToFile(fileSaveChooser.getSelectedFile().getPath(), dataStorer);
                }
            }
        });
    }

    private void newProject(NodeControl nodeControl, SongControl songControl, Dimension dimension) {

        this.spotifyPlayerPanel = new SpotifyPlayerPanel(songControl, dimension);
        this.nodeEditWindow = new NodeEditWindow(nodeControl);
        this.functionTabbedPane = new FunctionTabbedPane(nodeControl);
        this.eventEditWindow = new EventEditWindow(songControl);

        this.nodeEditWindow.setPreferredSize(dimension);

        this.enableTabs();
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
        this.tabbedPane.setComponentAt(4, new JPanel());

        this.songMenu.setEnabled(true);
        this.nodeMenu.setEnabled(true);
        this.functionMenu.setEnabled(true);

        this.pack();
        this.repaint();
    }

    private void setCorrectLocation() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int posX = (screenSize.width - this.getWidth()) / 2;
        int posY = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(posX, posY);
    }

    private static JFileChooser getDefaultFileSaveChooser() {

        return new JFileChooser() {
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "File already exists, overwrite?", "Existing File",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                        default:
                            return;
                    }
                }
                super.approveSelection();
            }
        };
    }
}
