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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/**
 *
 * @author s.bikov
 */
public class ModbusTester extends JFrame {

    ModbusClient modbusClient;

    JTable resultTable;
    DefaultTableModel tableModel;
    
    Button runButton;

    public static void main(String[] args) {
        new ModbusTester();
    }

    public boolean init(String ip, int port) throws IOException, SerialPortException, ModbusException, SerialPortTimeoutException, MqttPersistenceException, MqttException, InterruptedException {
        modbusClient = new ModbusClient(ip, port);
        boolean success = modbusClient.Available(2500);
        System.out.println(success);
        modbusClient.Connect();

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

    public boolean checkInterval(String dataType, long value) {
        return false;
    }

    public long readFunc3(Parameter param) throws ModbusException, SocketException, IOException, UnknownHostException, SerialPortException, SerialPortTimeoutException {
        long result = 0;
        param.resultArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
        System.out.println(param.name + " address=" + param.address + " value=" + param.getResultString());
        tableModel.addRow(param.toObjectArray());
        return result;
    }

    public ModbusTester() {
        super("modbus tester");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        
        Button openButton = new Button("open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                runButton.setEnabled(true);
            }
        });
        northPanel.add(openButton);
        
        runButton = new Button("run");
        runButton.setEnabled(false);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = FileUtils.fileReader();
                ParserCSV parser = new ParserCSV(str);

                try {
                    if (init("10.6.18.33", 502)) {
                        for (Parameter param : parser.getParameterArray()) {                   
                            if (param.funcToRead == 3) {
                                readFunc3(param);
                                Thread.sleep(200);
                            }
                        }
                    }
                } catch (IOException | SerialPortException | ModbusException | SerialPortTimeoutException | MqttException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        northPanel.add(runButton);
        Button simpleTestButton = new Button("test");
        simpleTestButton.setEnabled(false);
        simpleTestButton.addActionListener(new ActionListener() {
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
        northPanel.add(simpleTestButton);
        add(northPanel, BorderLayout.NORTH);
        String[] colNames = {"parameter", "address", "value", "testResult"};
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(colNames);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
        setVisible(true);
    }
}
