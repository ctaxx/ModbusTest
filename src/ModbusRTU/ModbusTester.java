/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import de.re.easymodbus.exceptions.ModbusException;
import java.awt.BorderLayout;
//import de.re.easymodbus.modbusclient.ModbusClient;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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

    public static void main(String[] args) {
        new ModbusTester();
    }

    public boolean init() throws IOException, SerialPortException, ModbusException, SerialPortTimeoutException, MqttPersistenceException, MqttException, InterruptedException {
        modbusClient = new ModbusClient("10.6.18.33", 502);
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
//        long result = DataUtils.prepareData(resultArray);
//        System.out.println(result);
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
        int[] resultArray;
        long result = 0;
        String tmpStr;
        resultArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);

        if (param.dataType.contains("Unsigned 32")
                || param.dataType.contains("Unsigned 16")
                || param.dataType.contains("Enum 2")
                || param.dataType.contains("Enum 3")
                || param.dataType.contains("Enum 4")
                || param.dataType.contains("Enum 5")) {
            result = DataUtils.prepareData(resultArray);
            tmpStr = param.name + " address=" + param.address + " value=" + result;
            System.out.println(tmpStr);
            Object[] obj = {param.name, param.address, result, checkInterval(param.dataType, result)};
            tableModel.addRow(obj);
        }

        if (param.dataType.contains("Unsigned 8")) {
            System.err.println("Unsigned 8 is not yet implemented");
        }

        if (param.dataType.contains("Unsigned 48")) {
            System.err.println("Unsigned 48 is not yet implemented");
        }

        if (param.dataType.contains("Signed 16")) {
            System.err.println("Signed 16 is not yet implemented");
        }

        if (param.dataType.contains("Date time 32")) {
            result = DataUtils.prepareData(resultArray);
            tmpStr = param.name + " address=" + param.address + " value=" + DataUtils.convertLongToDateTime32(result);
            Object[] obj = {param.name, param.address, DataUtils.convertLongToDateTime32(result), checkInterval(param.dataType, result)};
            tableModel.addRow(obj);
        }
        return result;
    }

    public ModbusTester() {
        super("modbus tester");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        Panel northPanel = new Panel();
        northPanel.setLayout(new FlowLayout());

        Button runButton = new Button("run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = FileUtils.fileReader();
                ParserCSV parser = new ParserCSV(str);

                try {
                    if (init()) {
                        for (Parameter param : parser.getParameterArray()) {
                            long l = 0;
                            if (param.funcToRead == 3) {
                                l = readFunc3(param);
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
