/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import ModbusRTU.parameter.ParserCSV;
import ModbusRTU.parameter.Parameter;
import de.re.easymodbus.exceptions.ModbusException;
import java.awt.BorderLayout;
//import de.re.easymodbus.modbusclient.ModbusClient;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/**
 *
 * @author s.bikov
 */
public class ModbusTester extends JFrame implements ActionListener {

    ModbusClient modbusClient;

    JTable resultTable;
    DefaultTableModel tableModel;
    ParserCSV parser;

    JButton openButton, readButton, writeButton, writeOutOfRangeButton, saveButton;
    JTextField ipTextField;
    JFormattedTextField formattedTextField;

    public static void main(String[] args) {
        new ModbusTester();
    }

    public boolean init(String ip, int port) throws IOException, SerialPortException, ModbusException, SerialPortTimeoutException, MqttPersistenceException, MqttException, InterruptedException {
        modbusClient = new ModbusClient(ip, port);
        boolean success = modbusClient.Available(2500);
        System.out.println(success);
        if (success) {
            modbusClient.Connect();
        }

//        int[] resultArray;
//         while (true)
//         {
//        	 System.out.println(modbusClient.ReadInputRegisters(0, 10)[5]);
//               System.out.println(modbusClient.ReadHoldingRegisters(61440, 10));
//        resultArray = modbusClient.ReadHoldingRegisters(7777, 1);
//        for (int i : resultArray) {
//            System.out.println(i);
//        }
//        System.out.println();
//        resultArray = modbusClient.ReadHoldingRegisters(160, 2);
        Thread.sleep(2000);
//         }
        return success;
    }

    public ModbusTester() {
        super("modbus tester");

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

        openButton = new JButton("open");
        openButton.addActionListener(this);
        northPanel.add(openButton);

        ipTextField = new JTextField();
        ipTextField.setColumns(9);
        northPanel.add(ipTextField);

        try {
            MaskFormatter mf = new MaskFormatter("##.#.##.##");
            formattedTextField = new JFormattedTextField(mf);
//            northPanel.add(formattedTextField);
        } catch (ParseException ex) {
            Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
        }

        readButton = new JButton("read");
        readButton.setEnabled(false);
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
//                    if (init("10.6.18.33", 502)) {
                    if (init(ipTextField.getText(), 502)) {
                        for (Parameter param : parser.getParameterArray()) {
                            int[] dataArray = null;
                            if (param.funcToRead == 3) {
                                try {
                                    dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                                    System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
                                    param.readResult = "R+ Ch0";
                                    param.checkInterval(dataArray);
                                    Thread.sleep(200);
                                } catch (FuncException ex) {
                                    System.err.println(ex.getMessage());
                                    param.readResult = "cannot read";
                                }
                            } else {
                                System.err.println(param.name + " reading impossible or haven't implemented yet");
                            }
                        }
                    }
                } catch (IOException | SerialPortException | ModbusException | SerialPortTimeoutException | MqttException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(readButton);
        writeButton = new JButton("write");
        writeButton.setEnabled(false);
        writeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Parameter param : parser.getParameterArray()) {
                        if (param.funcToWrite != 0) {
                            try {
                                System.out.println("going to write valid data to param " + param.name);
                                modbusClient.WriteMultipleRegisters(param.address, param.getValidValue());
//                                temporary stub
                                System.out.print("checkin' written value...");
                                int[] dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                                if (dataArray[0] == param.getValidValue()[0]) {
                                    param.writeResult = "W+";
                                }
                                System.out.println("Ok");
                                Thread.sleep(200);
                            } catch (FuncException ex) {
                                System.err.println(ex.getMessage());
                                param.writeResult = "W-";
                            }
                        }
                    }

                } catch (ModbusException | SerialPortException | SerialPortTimeoutException | SocketException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(writeButton);

        writeOutOfRangeButton = new JButton("writeOutOfRange");
        writeOutOfRangeButton.setEnabled(false);
        writeOutOfRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Parameter param : parser.getParameterArray()) {
                        if (param.funcToWrite != 0) {
                            int[] tmpDataArray = null;
                            try {
                                tmpDataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                                System.out.println();
                                System.out.println("going to write wrong data " + param.getOutOfRangeValue()[0] + " to param " + param.address + " " + param.name);
                                modbusClient.WriteMultipleRegisters(param.address, param.getOutOfRangeValue());

                                System.out.println("Ok");

                            } catch (FuncException ex) {
                                System.err.print(ex.getMessage());
                                System.err.println(" Expected exception was thrown - we cannot write wrong data");
                            }
                            try {
                                //                                temporary stub
                                System.out.print("checkin' written wrong value...");
                                int[] dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                                if (dataArray[0] == tmpDataArray[0]) {
                                    System.out.println("wrong data wasn't written to param - Ok");
                                }
                            } catch (FuncException ex) {
                                System.err.println(ex.getMessage());
                            }
                            Thread.sleep(200);
                        }
                    }

                } catch (ModbusException | SerialPortException | SerialPortTimeoutException | SocketException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(writeOutOfRangeButton);
        saveButton = new JButton("save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStatus();
            }
        });
        northPanel.add(saveButton);
        add(northPanel, BorderLayout.NORTH);
        String[] colNames = {"parameter", "address", "readResult", "writeResult"};
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(colNames);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            FileFilter filter = new FileNameExtensionFilter("csv", "csv");
            JFileChooser fileDialog = new JFileChooser("D:/");
            fileDialog.setFileFilter(filter);
            int ret = fileDialog.showDialog(this, "Open");

            readButton.setEnabled(false);
            writeButton.setEnabled(false);
            writeOutOfRangeButton.setEnabled(false);

            if (ret == JFileChooser.APPROVE_OPTION) {
                String str = FileUtils.fileReader(fileDialog.getSelectedFile());
                parser = new ParserCSV(str);
                readButton.setEnabled(true);
                writeButton.setEnabled(true);
                writeOutOfRangeButton.setEnabled(true);
                saveButton.setEnabled(true);
                ipTextField.setText(parser.currentIP);
//                    formattedTextField.setValue(InetAddress.getByName(parser.currentIP));
//                formattedTextField.setValue(parser.currentIP);

                tableModel.setRowCount(0);

                for (Parameter param : parser.getParameterArray()) {
                    Object obj[] = {param.name, param.address, param.readResult, param.writeResult};
                    tableModel.addRow(obj);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Parameter> tmpArray;
                        while (true) {
                            try {
                                tmpArray = parser.getParameterArray();
                                for (int i = 0; i < tmpArray.size(); i++) {
                                    tableModel.setValueAt(tmpArray.get(i).readResult, i, 2);
                                    tableModel.setValueAt(tmpArray.get(i).writeResult, i, 3);
                                }
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }).start();
            }
        }
    }
    
    public void saveStatus(){
        
    }
}
