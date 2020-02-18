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
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
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

    JButton openButton, readButton;
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

    public void writeFunc16(Parameter param, int[] values) {
        try {
            modbusClient.WriteMultipleRegisters(param.address, values);
        } catch (ModbusException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                int [] dataArray = null;
                try {
//                    if (init("10.6.18.33", 502)) {
                    if (init(ipTextField.getText(), 502)) {
                        for (Parameter param : parser.getParameterArray()) {
                            if (param.funcToRead == 3) {
                                try {
//                                    param.setResultArray(modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs));
                                    dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                                    System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
                                    param.readResult = "data was read, but haven't been checked";
                                    Thread.sleep(200);
                                } catch (FuncException ex) {
                                    System.err.println(ex.getMessage());
                                    param.readResult = "cannot read";
                                }
                            } else {
                                System.err.println(param.name + " reading impossible or haven't implemented yet");
                            }
                            Object obj[] = {param.name, param.address, param.getValueString(dataArray), param.readResult, param.writeResult}; 
//                            tableModel.addRow(param.toObjectArray());
                            tableModel.addRow(obj);
                        }
                    }
                } catch (IOException | SerialPortException | ModbusException | SerialPortTimeoutException | MqttException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(readButton);
        JButton writeButton = new JButton("write");
        writeButton.setEnabled(false);
        writeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] values = {0x1};
                try {
                    modbusClient.WriteMultipleRegisters(96, values);
                } catch (ModbusException | SerialPortException | SerialPortTimeoutException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SocketException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(writeButton);
        add(northPanel, BorderLayout.NORTH);
        String[] colNames = {"parameter", "address", "value", "readResult", "writeResult"};
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

            if (ret == JFileChooser.APPROVE_OPTION) {
                String str = FileUtils.fileReader(fileDialog.getSelectedFile());
                parser = new ParserCSV(str);
                readButton.setEnabled(true);
                ipTextField.setText(parser.currentIP);
//                    formattedTextField.setValue(InetAddress.getByName(parser.currentIP));
//                formattedTextField.setValue(parser.currentIP);

            }
        }
    }
}
