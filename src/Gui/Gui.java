package Gui;

import Event_Simulation.STK;
import Event_Simulation.Sim_Core;
import Event_Simulation.SimulationMode;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Gui implements ISimDelegate {

    private final JFrame frame = new JFrame();
    private JButton startButton;
    private JButton resumeButton;
    private JButton pauseButton;
    private JButton terminateButton;

    private JLabel lblProbability;
    private JLabel lblAvgWaitTime;
    private Sim_Core simulation;

//    private JCheckBox cBsimulationMode = new JCheckBox("Zapnut rychly mod");
    private JLabel lblSimTimeValue = new JLabel("");
    private JTextField tfMechanicNumber;
    private JTextField tfReceptionNumber;
    private JTextField tfReplicationNumber;
    private JLabel lblBusyTechValue = new JLabel("");
    private JLabel lblFreeTechValue = new JLabel("");
    private JLabel lblBusyMechanicsValue = new JLabel("");
    private JLabel lblFreeMechanicsValue = new JLabel("");
    private JLabel lblCurrentParkingCapacityValue = new JLabel("");
    private JLabel lblCarsTransportingQuantityValue = new JLabel("");
    private JLabel lblWaitingCustomersQuantityValue = new JLabel("");
    private JLabel lblWaitingPayCustomersQuantityValue = new JLabel("");
    private JLabel lblCarsInSTKQuantityValue = new JLabel("");
    private JLabel lblPayingCustomersQuantityValue = new JLabel("");
    private JLabel lblCurrentReplicationValue = new JLabel("");
    private JPanel settingsPanelBeforeSim = new JPanel();
    private JPanel settingsPanelSim = new JPanel();
    private JPanel northPanel = new JPanel();
    private JPanel eastPanel = new JPanel();

    private JSlider timeScaleSlider;
    private JSpinner spinner;
    private JTable customerTable;
    private DefaultTableModel customerModel;
    private DefaultTableModel employeesModel;
    private DefaultTableModel carsModel;
    private DefaultTableModel statsModel;
    private DefaultTableModel statsModelSim;
    private JPanel outputPanelReplication;
    private JPanel outputPanelReplicationCenter;
    private JPanel outputPanelSimulation;
    private JComboBox<String> cbSimMode;
    private JPanel graphPanel;
    private MySpinnerNumberModel spinnerModel;
    private MyTableModel allCustomersModel;
    private MyTableModel allTechniciansModel;
    private MyTableModel allMechanicsModel;
    private MyTableModel allCarsWaitingModel;
    private MyTableModel allInQueueModel;
    private JPanel centerPanel = new JPanel();
    private JPanel buttonPanelGraphsGUI;
    private JPanel buttonPanelMain;
    private JPanel buttonPanelWrapper = new JPanel();
    private MyXYLineChart chart2;
    private MyXYLineChart chart;
    private JButton graphsGuiBtn1;
    private JButton graphsGuiBtn2;
    private JButton graphsGuiBtnEnd;
    private boolean graph1Active;
    private boolean endGraph = false;

    public Gui(Sim_Core simulation){
        this.simulation = simulation;
        this.simulation.registerDelegate(this);
        graphsGui();
        simSettings();

        frame.add(eastPanel, BorderLayout.EAST);
        frame.add(centerPanel, BorderLayout.CENTER);
        simulationOutput();
        replicationOutput();//Replication output
        outputPanelSimulation.setVisible(false);
        graphPanel.setVisible(false);

        initView();
        buttons();
        // Set the frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(960,540));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void simulationOutput() {
        JLabel lblCurrentReplication = new JLabel("Aktualna replikacia: ");

        outputPanelSimulation = new JPanel();
        outputPanelSimulation.setLayout(new BoxLayout(outputPanelSimulation, BoxLayout.Y_AXIS));

        lblCurrentReplication.setFont(new Font("Calibri", Font.PLAIN, 20));
        lblCurrentReplicationValue.setFont(new Font("Calibri", Font.PLAIN, 18));

        outputPanelSimulation.add(lblCurrentReplication);
        outputPanelSimulation.add(lblCurrentReplicationValue);

        //TABLE
        String[] columnNamesS = {"Statistika", "Hodnota", "Interval spolahlivosti"};
        Object[][] dataS = {
                {"Priem. cas zakaznika v prevadzke", "", ""},
                {"Priem. cas cakania na odovzdanie auta", "", ""},
                {"Priem. cas cakania na zaplatenie", "", ""},
                {"Priem. cas cakania na ukoncenie kontroly", "", ""},

                {"Priem. pocet cakajucich v rade na odovzdanie auta", "", ""},
                {"Priem. pocet zakaznikov v systeme", "", ""},
                {"Priem. pocet volnych pracovnickov sk. 1", "", ""},
                {"Priem. pocet volnych pracovnickov sk. 2", "", ""},
                {"Priem. pocet aut v prevadzke na konci dna", "", ""},
                {"Priem. pocet zak. v prevadzke na konci dna", "", ""},
        };

        statsModelSim = new DefaultTableModel(dataS, columnNamesS);
        JTable statsTable = new JTable(statsModelSim);
        JScrollPane scrollPaneS = new JScrollPane(statsTable);
        setWidthColumn(statsTable, 2, 500);
        setWidthColumn(statsTable, 1, 500);
        setWidthColumn(statsTable, 0, 500);
        //end

        //Table font
        statsTable.setFont(new Font("", Font.PLAIN, 20));
        statsTable.setRowHeight(25);
        statsTable.getPreferredSize().width = 1500;
        Dimension tableSizeT = scrollPaneS.getPreferredSize();
        tableSizeT.width = 1500;
        tableSizeT.height = 275;
        scrollPaneS.setPreferredSize(tableSizeT);
        //

        //Spacing
        Box container = Box.createVerticalBox();
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(scrollPaneS);
        //

        outputPanelSimulation.add(container);

        centerPanel.add(outputPanelSimulation);

    }

    private void replicationOutput() {
        replicationOutputCenter();

        JLabel lblSimTime = new JLabel("Simulacny cas: ");

        outputPanelReplication = new JPanel();
        outputPanelReplication.setLayout(new BoxLayout(outputPanelReplication, BoxLayout.Y_AXIS));

        lblSimTime.setFont(new Font("Calibri", Font.PLAIN, 20));
        lblSimTimeValue.setFont(new Font("Calibri", Font.PLAIN, 18));

        outputPanelReplication.add(lblSimTime);
        outputPanelReplication.add(lblSimTimeValue);

        //TABLE
        String[] columnNames = {"Stav", "Pocet"};
        Object[][] data = {
                {"Celkovy pocet zakaznikov", ""},
                {"Celkovy pocet cak. v rade", ""},
                {"Pocet cak. na odovzdanie auta", ""},
                {"Pocet cakajucich na platenie", ""},
                {"Pocet platiacich zakaznikov", ""},
                {"Pocet cak. na koniec kontroly", ""},
                {"Pocet odidenych zakaznikov", ""},
        };

        customerModel = new DefaultTableModel(data, columnNames);
        JTable customersTable = new JTable(customerModel);

        int[] rows = {0, 1, 2, 4};
        Color[] bgColors = {Color.BLUE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.LIGHT_GRAY};
        Color[] textColors = {Color.WHITE, Color.BLACK, Color.white, Color.BLACK};

        colorRows(customersTable, rows, bgColors, textColors);

        setWidthColumn(customersTable, 1, 40);
        setWidthColumn(customersTable, 0, 350);
//        outputPanel.add(customerTable);
        //end

        //TABLE
        String[] columnNamesE = {"Stav", "Pocet"};
        Object[][] dataE = {
                {"Pocet obsadenych technikov", ""},
                {"Pocet volnych technikov", ""},
                {"Pocet obsadenych mechanikov", ""},
                {"Pocet volnych mechanikov", ""},
        };

        employeesModel = new DefaultTableModel(dataE, columnNamesE);
        JTable employeesTable = new JTable(employeesModel);

        int[] rowsE = {0, 1, 2, 3}; // specify the rows to color
        Color[] bgColorsE = {Color.YELLOW, Color.GREEN, Color.YELLOW, Color.GREEN};
        Color[] textColorsE = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};

        colorRows(employeesTable, rowsE, bgColorsE, textColorsE);

        setWidthColumn(employeesTable, 1, 40);
        setWidthColumn(employeesTable, 0, 350);
//        outputPanel.add(employeesTable);
        //end

        //TABLE
        String[] columnNamesC = {"Stav", "Pocet"};
        Object[][] dataC = {
                {"Pocet aut v prevadzke", ""},
                {"Pocet kontrolovanych aut", ""},
                {"Aktualna kapacita parkoviska", ""},
                {"Pocet aut v preprave", ""},
        };

        carsModel = new DefaultTableModel(dataC, columnNamesC);
        JTable carsTable = new JTable(carsModel);

        int[] rowsC = {0, 1};
        Color[] bgColorsC = {Color.BLUE, Color.LIGHT_GRAY};
        Color[] textColorsC = {Color.WHITE, Color.BLACK};

        colorRows(carsTable, rowsC, bgColorsC, textColorsC);

        setWidthColumn(carsTable, 1, 40);
        setWidthColumn(carsTable, 0, 350);
//        outputPanel.add(employeesTable);
        //end

        //TABLE
        String[] columnNamesS = {"Statistika", "Hodnota"};
        Object[][] dataS = {
                {"Priem. cas zakaznika v prevadzke", ""},
                {"Priem. cas cakania na odovzdanie auta", ""},
                {"Priem. pocet cak. v rade na odovz. auta", ""},
                {"Priem. pocet zak. v systeme", ""},
                {"Priem. pocet volnych prac. sk. 1", ""},
                {"Priem. pocet volnych prac. sk. 2", ""},
                {"Pocet zakaznikov na konci dna", ""},
        };

        statsModel = new DefaultTableModel(dataS, columnNamesS);
        JTable statsTable = new JTable(statsModel);

        setWidthColumn(statsTable, 1, 80);
        setWidthColumn(statsTable, 0, 350);
//        outputPanel.add(employeesTable);
        //end

        //Table font
        customersTable.setFont(new Font("", Font.PLAIN, 20));
        employeesTable.setFont(new Font("", Font.PLAIN, 20));
        carsTable.setFont(new Font("", Font.PLAIN, 20));
        statsTable.setFont(new Font("", Font.PLAIN, 20));
        customersTable.setRowHeight(25);
        employeesTable.setRowHeight(25);
        carsTable.setRowHeight(25);
        statsTable.setRowHeight(25);
        //

        //Spacing
        Box container = Box.createVerticalBox();
        container.add(customersTable);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(employeesTable);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(carsTable);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(statsTable);
        //

        outputPanelReplication.add(container);
        eastPanel.add(outputPanelReplication);
    }

    private void replicationOutputCenter() {
        outputPanelReplicationCenter = new JPanel();

        allCustomersModel = new MyTableModel(new ArrayList<>(), new ArrayList<>(Arrays.asList("Zakaznik", "Stav", "Auto")));
        allInQueueModel = new MyTableModel(new ArrayList<>(), new ArrayList<>(Arrays.asList("Zakaznik", "Stav", "Auto")));
        allTechniciansModel = new MyTableModel(new ArrayList<>(), new ArrayList<>(Arrays.asList("Technik", "Stav", "Auto")));
        allMechanicsModel = new MyTableModel(new ArrayList<>(), new ArrayList<>(Arrays.asList("Mechanik", "Stav", "Auto")));
        allCarsWaitingModel = new MyTableModel(new ArrayList<>(), new ArrayList<>(Arrays.asList("Poradie", "Auto")));
        //Table
        JTable allCustomersTable = new JTable(allCustomersModel);
        JScrollPane scrollPane = new JScrollPane(allCustomersTable);
        allCustomersTable.setFont(new Font("", Font.PLAIN, 18));
        allCustomersTable.setRowHeight(25);
        setWidthColumn(allCustomersTable, 2, 165);
        setWidthColumn(allCustomersTable, 1, 300);
        setWidthColumn(allCustomersTable, 0, 185);

        Dimension tableSizeC = scrollPane.getPreferredSize();
        tableSizeC.height = 440;//890
        scrollPane.setPreferredSize(tableSizeC);
        //

        //Table
        JTable allInQueueTable = new JTable(allInQueueModel);
        JScrollPane scrollPaneQ = new JScrollPane(allInQueueTable);
        allInQueueTable.setFont(new Font("", Font.PLAIN, 18));
        allInQueueTable.setRowHeight(25);
        setWidthColumn(allInQueueTable, 2, 165);
        setWidthColumn(allInQueueTable, 1, 300);
        setWidthColumn(allInQueueTable, 0, 185);

        Dimension tableSizeQ = scrollPane.getPreferredSize();
        tableSizeQ.height = 440;
        scrollPaneQ.setPreferredSize(tableSizeQ);
        //

        //Table
        JTable allTechniciansTable = new JTable(allTechniciansModel);
        JScrollPane scrollPaneT = new JScrollPane(allTechniciansTable);
        allTechniciansTable.setFont(new Font("", Font.PLAIN, 18));
        allTechniciansTable.setRowHeight(25);
        setWidthColumn(allTechniciansTable, 2, 315);
        setWidthColumn(allTechniciansTable, 1, 215);
        setWidthColumn(allTechniciansTable, 0, 185);

        Dimension tableSizeT = scrollPaneT.getPreferredSize();
        tableSizeT.height = 890;
        tableSizeT.width = 390;
        scrollPaneT.setPreferredSize(tableSizeT);
        //

        //Table
        JTable allMechanicsTable = new JTable(allMechanicsModel);
        JScrollPane scrollPaneM = new JScrollPane(allMechanicsTable);
        allMechanicsTable.setFont(new Font("", Font.PLAIN, 18));
        allMechanicsTable.setRowHeight(25);
        setWidthColumn(allMechanicsTable, 2, 300);
        setWidthColumn(allMechanicsTable, 1, 200);
        setWidthColumn(allMechanicsTable, 0, 185);

        Dimension tableSizeM = scrollPaneM.getPreferredSize();
        tableSizeM.height = 890;
        tableSizeM.width = 390;
        scrollPaneM.setPreferredSize(tableSizeM);
        //

        //Table
        JTable allCarsWaitingTable = new JTable(allCarsWaitingModel);
        JScrollPane scrollPaneCar = new JScrollPane(allCarsWaitingTable);
        allCarsWaitingTable.setFont(new Font("", Font.PLAIN, 18));
        allCarsWaitingTable.setRowHeight(25);
        setWidthColumn(allCarsWaitingTable, 1, 160);
        setWidthColumn(allCarsWaitingTable, 0, 20);

        Dimension tableSizeCar = scrollPaneCar.getPreferredSize();
        tableSizeCar.height = 890;
        tableSizeCar.width = 200;//180
        scrollPaneCar.setPreferredSize(tableSizeCar);
        //

        Box container = Box.createVerticalBox();
        container.add(scrollPaneQ);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(scrollPane);

        outputPanelReplicationCenter.add(scrollPaneT);
//        outputPanelReplicationCenter.add(scrollPane);
        outputPanelReplicationCenter.add(container);
        outputPanelReplicationCenter.add(scrollPaneCar);
        outputPanelReplicationCenter.add(scrollPaneM);
        centerPanel.add(outputPanelReplicationCenter);
    }

    private void buttons() {
        buttonPanelMain = new JPanel();
        buttonPanelMain.add(startButton);
        buttonPanelMain.add(pauseButton);
        buttonPanelMain.add(resumeButton);
        buttonPanelMain.add(terminateButton);
        buttonPanelWrapper.add(buttonPanelMain);
        frame.add(buttonPanelWrapper, BorderLayout.SOUTH);
    }

     private void simSettings() {
        cbSimMode = new JComboBox<>();
        cbSimMode.addItem("Replikacia");
        cbSimMode.addItem("Simulacia");
        cbSimMode.addItem("Grafy");

        JLabel lblReceptionNumber = new JLabel("Pocet prijimacich technikov: ");
        tfReceptionNumber = new JTextField();
        tfReceptionNumber.setColumns(2);
        tfReceptionNumber.setText("4");

        JLabel lblMechanicNumber = new JLabel("Pocet mechanikov: ");
        tfMechanicNumber = new JTextField();
        tfMechanicNumber.setColumns(2);
        tfMechanicNumber.setText("17");


        JLabel lblReplicationNumber = new JLabel("Pocet replikacii: ");
        tfReplicationNumber = new JTextField();
        tfReplicationNumber.setColumns(10);
        tfReplicationNumber.setText("1000");

        lblReceptionNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
        tfReceptionNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
        lblMechanicNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
        tfMechanicNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
        lblReplicationNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
        tfReplicationNumber.setFont(new Font("Calibri", Font.PLAIN, 20));
//        cBsimulationMode.setFont(new Font("Calibri", Font.PLAIN, 20));

        settingsPanelBeforeSim.add(lblReceptionNumber);
        settingsPanelBeforeSim.add(tfReceptionNumber);

        settingsPanelBeforeSim.add(lblMechanicNumber);
        settingsPanelBeforeSim.add(tfMechanicNumber);

        settingsPanelBeforeSim.add(lblReplicationNumber);
        settingsPanelBeforeSim.add(tfReplicationNumber);

//        settingsPanelBeforeSim.add(cBsimulationMode);
        settingsPanelBeforeSim.add(cbSimMode);
//---------------------------------------------------------------------------------

//        timeScaleSlider = new JSlider(JSlider.HORIZONTAL, -1, 3, 0);
//        timeScaleSlider.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                double timeScale;
//                int value = timeScaleSlider.getValue();
//                if (value >= 0) {
//                    timeScale = Math.pow(10, value);
//
//                } else {
//                    timeScale = 1.0 / Math.pow(10, -value);
//                }
//                ((STK)simulation).setSimulationSpeed(timeScale);
//            }
//        });
//        timeScaleSlider.setMajorTickSpacing(1);
//        timeScaleSlider.setMinorTickSpacing(1);
//        timeScaleSlider.setPaintTicks(true);
//        timeScaleSlider.setPaintLabels(true);
//        timeScaleSlider.setLabelTable(timeScaleSlider.createStandardLabels(1));

        spinnerModel = new MySpinnerNumberModel(1, 1/8.0, 10000);
        spinner = new JSpinner(spinnerModel);

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
        Font font = editor.getTextField().getFont();
        editor.getTextField().setFont(font.deriveFont(font.getSize() * 1.5f));

        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((STK)simulation).setSimulationSpeed((double)spinnerModel.getValue());
            }
        });

//        settingsPanelSim.add(timeScaleSlider);
        JLabel lblSimSpeed = new JLabel("Rychlost simulacie x-krat: ");
        lblSimSpeed.setFont(new Font("Calibri", Font.PLAIN, 20));

        settingsPanelSim.add(lblSimSpeed);
        settingsPanelSim.add(spinner);
//---------------------------------------------------------------------------------
        northPanel.add(settingsPanelBeforeSim);
        northPanel.add(settingsPanelSim);
        frame.add(northPanel, BorderLayout.NORTH);
    }

    private void graphsGui() {
        chart = new MyXYLineChart("Priemerny pocet cakajucich v rade na odovzdanie auta na pocte prac. sk. 1", "Pocet pracovnikov skupiny 1", "Priemerny pocet cakajucich", "Priemerny pocet cakajucich");
        ChartPanel chartPanel = new ChartPanel(chart.getChart());

        chart2 = new MyXYLineChart("Priemerny cas straveny zakaznikmi v prevadzke na pocte prac. sk. 2", "Pocet pracovnikov skupiny 2", "Priemerny cas zakaznika v prevadzke (minuty)", "Priemerny cas zakaznika v prevadzke (minuty)");
        ChartPanel chartPanel2 = new ChartPanel(chart2.getChart());

        graphPanel = new JPanel(new GridLayout(1, 2));
        graphPanel.add(chartPanel);
        graphPanel.add(chartPanel2);

        Dimension graphPanelDim = graphPanel.getPreferredSize();
        graphPanelDim.height = 900;
        graphPanelDim.width = 1800;
        graphPanel.setPreferredSize(graphPanelDim);

        // Create the buttons
        graphsGuiBtn1 = new JButton("Vykresli");
        graphsGuiBtn2 = new JButton("Vykresli");
        graphsGuiBtnEnd = new JButton("Ukonci");
        graphsGuiBtnEnd.setEnabled(false);
//        buttonPanelGraphsGUI = new JPanel(new FlowLayout(FlowLayout.CENTER, 820,0));
        buttonPanelGraphsGUI = new JPanel(new FlowLayout(FlowLayout.CENTER, 400,0));
        buttonPanelGraphsGUI.add(graphsGuiBtn1);
        buttonPanelGraphsGUI.add(graphsGuiBtnEnd);
        buttonPanelGraphsGUI.add(graphsGuiBtn2);
        buttonPanelGraphsGUI.setVisible(false);
        buttonPanelWrapper.add(buttonPanelGraphsGUI);

        centerPanel.add(graphPanel);
        frame.add(centerPanel, BorderLayout.CENTER);
    }

    private void initView() {
        // Create two buttons using Swing
        startButton = new JButton("Spusti");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                terminateButton.setEnabled(true);
                Thread simulationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (cbSimMode.getSelectedItem().toString().equals("Simulacia")) {setPanelEnabled(northPanel, false);} else {setPanelEnabled(settingsPanelBeforeSim, false);}
                        ((STK) simulation).setAvailableReceptionTech(Integer.parseInt(tfReceptionNumber.getText()));
                        ((STK) simulation).setAvailableMechanics(Integer.parseInt(tfMechanicNumber.getText()));
                        simulation.setSimMode(cbSimMode.getSelectedItem().toString().equals("Simulacia") ? SimulationMode.TURBO : SimulationMode.NORMAL);
                        simulation.simulate(cbSimMode.getSelectedItem().toString().equals("Simulacia") ? Integer.parseInt(tfReplicationNumber.getText()) : 1 ,((STK) simulation).getMaxTime());
                    }
                });
                simulationThread.start();
            }
        });

        pauseButton = new JButton("Zastav");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((STK)simulation).setSimStopped(true);
                terminateButton.setEnabled(false);
            }
        });

        resumeButton = new JButton("Pokracuj");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((STK)simulation).setSimStopped(false);
                terminateButton.setEnabled(true);
            }
        });

        terminateButton = new JButton("Ukonci");
        terminateButton.setEnabled(false);
        terminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                terminateButton.setEnabled(false);
                if (cbSimMode.getSelectedItem().toString().equals("Simulacia")) {setPanelEnabled(northPanel, true);} else {setPanelEnabled(settingsPanelBeforeSim, true);}
                simulation.setRunning(false);
            }
        });

//        cBsimulationMode.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                if (cBsimulationMode.isSelected()) {
//                    outputPanelReplication.setVisible(false);
//                    outputPanelSimulation.setVisible(true);
//                    spinner.setEnabled(false);
//                } else {
//                    outputPanelReplication.setVisible(true);
//                    outputPanelSimulation.setVisible(false);
//                    spinner.setEnabled(true);
//                }
//            }
//        });

        cbSimMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cbSimMode.getSelectedItem();
                switch(selected) {
                    case "Replikacia":
                        outputPanelReplication.setVisible(true);
                        outputPanelReplicationCenter.setVisible(true);
                        buttonPanelMain.setVisible(true);
                        outputPanelSimulation.setVisible(false);
                        graphPanel.setVisible(false);
                        buttonPanelGraphsGUI.setVisible(false);
                        break;
                    case "Simulacia":
                        outputPanelReplication.setVisible(false);
                        outputPanelReplicationCenter.setVisible(false);
                        outputPanelSimulation.setVisible(true);
                        buttonPanelMain.setVisible(true);
                        graphPanel.setVisible(false);
                        buttonPanelGraphsGUI.setVisible(false);
                        break;
                    case "Grafy":
                        outputPanelReplication.setVisible(false);
                        outputPanelReplicationCenter.setVisible(false);
                        outputPanelSimulation.setVisible(false);
                        buttonPanelMain.setVisible(false);
                        graphPanel.setVisible(true);
                        buttonPanelGraphsGUI.setVisible(true);
                        break;
                    default:
                        break;
                }
            }
        });

        graphsGuiBtn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphsGuiBtn2.setEnabled(false);
                graphsGuiBtn1.setEnabled(false);
                graphsGuiBtnEnd.setEnabled(true);
                chart.clearSeries();
                Thread simulationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        graph1Active = true;
                        setPanelEnabled(northPanel, false);

                        for (int i = 1; i < 16; i++) {
                            ((STK) simulation).setAvailableReceptionTech(i+1);
                            ((STK) simulation).setAvailableMechanics(Integer.parseInt(tfMechanicNumber.getText()));
                            simulation.setSimMode(SimulationMode.TURBO);
                            simulation.simulate(Integer.parseInt(tfReplicationNumber.getText()), ((STK) simulation).getMaxTime());
                            if (endGraph) {break;}
                        }
                        graphsGuiBtn2.setEnabled(true);
                        graphsGuiBtn1.setEnabled(true);
                        graphsGuiBtnEnd.setEnabled(false);
                        endGraph = false;
                        setPanelEnabled(northPanel, true);
                    }
                });
                simulationThread.start();
            }
        });

        graphsGuiBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chart2.clearSeries();
                graphsGuiBtn2.setEnabled(false);
                graphsGuiBtn1.setEnabled(false);
                graphsGuiBtnEnd.setEnabled(true);
                Thread simulationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        graph1Active = false;
                        setPanelEnabled(northPanel, false);
                        for (int i = 10; i < 26; i++) {
                            ((STK) simulation).setAvailableReceptionTech(Integer.parseInt(tfReceptionNumber.getText()));
                            ((STK) simulation).setAvailableMechanics(i);
                            simulation.setSimMode(SimulationMode.TURBO);
                            simulation.simulate(Integer.parseInt(tfReplicationNumber.getText()), ((STK) simulation).getMaxTime());
                            if (endGraph) {break;}
                        }
                        graphsGuiBtn2.setEnabled(true);
                        graphsGuiBtn1.setEnabled(true);
                        graphsGuiBtnEnd.setEnabled(false);
                        endGraph = false;
                        setPanelEnabled(northPanel, true);
                    }
                });
                simulationThread.start();
            }
        });

        graphsGuiBtnEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.setRunning(false);
                endGraph = true;
            }
        });

        buttonHover(graphsGuiBtn2, tfReceptionNumber);

        buttonHover(graphsGuiBtn1, tfMechanicNumber);
    }

    private void buttonHover(JButton graphsGuiBtn2, JTextField tfReceptionNumber) {
        graphsGuiBtn2.addMouseListener(new MouseListener() {
            Border defaultBorder = tfReplicationNumber.getBorder();
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {
                Border border = BorderFactory.createLineBorder(Color.RED, 3);
                tfReplicationNumber.setBorder(border);
                tfReceptionNumber.setBorder(border);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                tfReplicationNumber.setBorder(defaultBorder);
                tfReceptionNumber.setBorder(defaultBorder);
            }
        });
    }

    @Override
    public void refresh(Sim_Core sim) {
        String selected = (String) cbSimMode.getSelectedItem();
        switch(selected) {
            case "Replikacia":
                refreshReplication(sim);
                break;
            case "Simulacia":
                refreshSimulation(sim);
                break;
            case "Grafy":
                refreshGraphs(sim);
                break;
            default:
                break;
        }
    }

    private void refreshGraphs(Sim_Core sim) {
        STK stk = (STK) sim;

        if (!stk.isRunning() && graph1Active) {
            chart.addPoint(chart.getItemCount()+1, stk.getAvgSimCustomerCountQueueCar().getMean());
        }

        if (!stk.isRunning() && !graph1Active) {
            chart2.addPoint(chart2.getItemCount()+10, roundNumber(stk.getAvgSimCustomerTimeInSystem().getMean()/60, 2));
        }
    }

    private void refreshSimulation(Sim_Core sim) {
        STK stk = (STK) sim;

        if (stk.getCompletedReplications() % (Integer.valueOf(tfReplicationNumber.getText())/10) == 0) {
            lblCurrentReplicationValue.setText(""+stk.getCompletedReplications());
        }

        if (!stk.isRunning()) {
            lblCurrentReplicationValue.setText(""+stk.getCompletedReplications());

            statsModelSim.setValueAt((stk.getAvgSimCustomerTimeInSystem().getMean()/60)  + " min", 0, 1);
            statsModelSim.setValueAt((stk.getAvgSimCustomerTimeInQueueCar().getMean()/60)  + " min", 1, 1);
            statsModelSim.setValueAt((stk.getAvgSimCustomerTimeInPayQueue().getMean()/60)  + " min", 2, 1);//todo new
            statsModelSim.setValueAt((stk.getAvgSimCustomerTimeWaitingCheck().getMean()/60)  + " min", 3, 1);//todo new

            statsModelSim.setValueAt((stk.getAvgSimCustomerCountQueueCar().getMean()), 4, 1);
            statsModelSim.setValueAt((stk.getAvgSimCustomerCountSystem().getMean()), 5, 1);
            statsModelSim.setValueAt((stk.getAvgSimTechnicianFreeCount().getMean()), 6, 1);
            statsModelSim.setValueAt((stk.getAvgSimMechanicFreeCount().getMean()), 7, 1);
            statsModelSim.setValueAt((stk.getAvgSimCarInSystemCount().getMean()), 8, 1);
            statsModelSim.setValueAt((stk.getAvgSimCustomersInSystemCount().getMean()), 9, 1);
            //CI
//            double [] valuesTimeInSystem = stk.getAvgSimCustomerTimeInSystem().getConfidenceIntervalValues("90");
//            statsModelSim.setValueAt("(" + roundNumber(valuesTimeInSystem[0]/60, 5)  + "; " + roundNumber(valuesTimeInSystem[1]/60, 5) + ") min", 0, 2);
//            double [] valuesCountInSystem = stk.getAvgSimCustomerCountSystem().getConfidenceIntervalValues("95");
//            statsModelSim.setValueAt("(" + roundNumber(valuesCountInSystem[0], 5)  + "; " + roundNumber(valuesCountInSystem[1], 5) + ")", 3, 2);

            double [] valuesTimeInSystem = stk.getAvgSimCustomerTimeInSystem().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesTimeInSystem[0]/60, 5)  + "; " + roundNumber(valuesTimeInSystem[1]/60, 5) + ") min", 0, 2);
            double [] valuesTimeInQueueCar = stk.getAvgSimCustomerTimeInQueueCar().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesTimeInQueueCar[0]/60, 5)  + "; " + roundNumber(valuesTimeInQueueCar[1]/60, 5) + ") min", 1, 2);
            double [] valuesTimeInQueuePay = stk.getAvgSimCustomerTimeInPayQueue().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesTimeInQueuePay[0]/60, 5)  + "; " + roundNumber(valuesTimeInQueuePay[1]/60, 5) + ") min", 2, 2);
            double [] valuesTimeWaitingCheck = stk.getAvgSimCustomerTimeWaitingCheck().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesTimeWaitingCheck[0]/60, 5)  + "; " + roundNumber(valuesTimeWaitingCheck[1]/60, 5) + ") min", 3, 2);

            double [] valuesCountInQueueCar = stk.getAvgSimCustomerCountQueueCar().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesCountInQueueCar[0], 5)  + "; " + roundNumber(valuesCountInQueueCar[1], 5) + ")", 4, 2);
            double [] valuesCountInSystem = stk.getAvgSimCustomerCountSystem().getConfidenceIntervalValues("95");
            statsModelSim.setValueAt("(" + roundNumber(valuesCountInSystem[0], 5)  + "; " + roundNumber(valuesCountInSystem[1], 5) + ")    95%", 5, 2);
            double [] valuesTechnicianFreeCount = stk.getAvgSimTechnicianFreeCount().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesTechnicianFreeCount[0], 5)  + "; " + roundNumber(valuesTechnicianFreeCount[1], 5) + ")", 6, 2);
            double [] valuesMechanicFreeCount = stk.getAvgSimMechanicFreeCount().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesMechanicFreeCount[0], 5)  + "; " + roundNumber(valuesMechanicFreeCount[1], 5) + ")", 7, 2);
            double [] valuesCarInSystemCount = stk.getAvgSimCarInSystemCount().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesCarInSystemCount[0], 5)  + "; " + roundNumber(valuesCarInSystemCount[1], 5) + ")", 8, 2);
            double [] valuesCustomerInSystemCount = stk.getAvgSimCustomersInSystemCount().getConfidenceIntervalValues("90");
            statsModelSim.setValueAt("(" + roundNumber(valuesCustomerInSystemCount[0], 5)  + "; " + roundNumber(valuesCustomerInSystemCount[1], 5) + ")", 9, 2);
            //
            statsModelSim.fireTableDataChanged();
        }
    }

    private void refreshReplication(Sim_Core sim) {
        STK stk = (STK) sim;
        lblSimTimeValue.setText(simTimeToHHMMSS(stk.getCurrentTime()));

        customerModel.setValueAt(stk.getTotalCustomers(), 0, 1);
        customerModel.setValueAt(stk.getWaitingCustomersQuantity(), 1, 1);
        customerModel.setValueAt(stk.getWaitingNewCustomersQuantity(), 2, 1);
        customerModel.setValueAt(stk.getWaitingPayCustomersQuantity(), 3, 1);
        customerModel.setValueAt(stk.getPayingCustomersQuantity(), 4, 1);
        customerModel.setValueAt(stk.getCarWaitingCustomersQuantity(), 5, 1);
        customerModel.setValueAt(stk.getTotalLeftCustomers(), 6, 1);

        customerModel.fireTableDataChanged();

        employeesModel.setValueAt(Integer.parseInt(tfReceptionNumber.getText()) - stk.getAvailableReceptionTech(), 0, 1);
        employeesModel.setValueAt(stk.getAvailableReceptionTech(), 1, 1);
        employeesModel.setValueAt(Integer.parseInt(tfMechanicNumber.getText()) - stk.getAvailableMechanics(), 2, 1);
        employeesModel.setValueAt(stk.getAvailableMechanics(), 3, 1);

        employeesModel.fireTableDataChanged();

        carsModel.setValueAt(stk.getCarsInSTKQuantity(), 0, 1);
        carsModel.setValueAt(Integer.parseInt(tfMechanicNumber.getText()) - stk.getAvailableMechanics(), 1, 1);
        carsModel.setValueAt(stk.getWorkShopParkingCount(), 2, 1);
        carsModel.setValueAt(stk.getCarsQuantityTransporting(), 3, 1);

        carsModel.fireTableDataChanged();

        statsModel.setValueAt(roundNumber(stk.getAvgCustomerTimeInSystem().getMean()/60, 2)  + "m", 0, 1);
        statsModel.setValueAt(roundNumber(stk.getAvgCustomerTimeInQueueCar().getMean()/60, 2)  + "m", 1, 1);
        statsModel.setValueAt(roundNumber(stk.getAvgCustomerCountQueueCar().getWeightedMean(), 2), 2, 1);
        statsModel.setValueAt(roundNumber(stk.getAvgCustomerCountSystem().getWeightedMean(), 2), 3, 1);
        statsModel.setValueAt(roundNumber(stk.getAvgTechnicianFreeCount().getWeightedMean(), 2), 4, 1);
        statsModel.setValueAt(roundNumber(stk.getAvgMechanicFreeCount().getWeightedMean(), 2), 5, 1);
        statsModel.setValueAt(roundNumber(stk.getCustomersInSTKAtClosure(), 2), 6, 1);

        statsModel.fireTableDataChanged();

        allCustomersModel.setData(stk.getAllCustomers());
        allCustomersModel.fireTableDataChanged();

        allInQueueModel.setData(stk.getAllInQueue());
        allInQueueModel.fireTableDataChanged();

        allTechniciansModel.setData(stk.getAllTechnicians());
        allTechniciansModel.fireTableDataChanged();

        allMechanicsModel.setData(stk.getAllMechanics());
        allMechanicsModel.fireTableDataChanged();

        allCarsWaitingModel.setData(stk.getAllCarsWaiting());
        allCarsWaitingModel.fireTableDataChanged();

    }

    private String simTimeToHHMMSS(double simTime) {
        int hours = (int)simTime / 3600;
        int minutes = (int)(simTime % 3600) / 60;
        int seconds = (int)simTime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private void setWidthColumn(JTable table, int i, int value) {
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(i); // get the second column (index 1)
        column.setPreferredWidth(value); // set the preferred width of the column to 200 pixels
        column.setResizable(false);
    }
    private void colorRows(JTable table, int[] rows, Color[] bgColors, Color[] textColors) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                for (int i = 0; i < rows.length; i++) {
                    if (row == rows[i]) {
                        cell.setBackground(bgColors[i]);
                        cell.setForeground(textColors[i]);
                        break;
                    }
                }
                return cell;
            }
        });
    }
    private double roundNumber(double number, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("decimalPlaces must be non-negative");
        }
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(number * factor) / factor;
    }
}
