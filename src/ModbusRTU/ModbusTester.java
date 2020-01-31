/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import de.re.easymodbus.exceptions.ModbusException;
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

    public static void main(String[] args) {
        new ModbusTester();
    }

    public void init() throws IOException, SerialPortException, ModbusException, SerialPortTimeoutException, MqttPersistenceException, MqttException, InterruptedException {
        modbusClient = new ModbusClient("10.6.18.33", 502);
        System.out.println(modbusClient.Available(2500));
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

    public long readFunc3(Parameter param) throws ModbusException, SocketException, IOException, UnknownHostException, SerialPortException, SerialPortTimeoutException {
        int[] resultArray;
        long result = 0;
        resultArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);

        if (param.dataType.contains("Unsigned 32")
                || param.dataType.contains("Unsigned 16")
                || param.dataType.contains("Enum 2")
                || param.dataType.contains("Enum 3")
                || param.dataType.contains("Enum 4")
                || param.dataType.contains("Enum 5")) {
            result = DataUtils.prepareData(resultArray);
            System.out.println(param.name + " address=" + param.address + " value=" + result);
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
            System.err.println("Date time 32 is not yet implemented");
        }
        return result;
    }

    public ModbusTester() {
        super("modbus tester");
        setSize(200, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new FlowLayout());

        Button runButton = new Button("run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = FileUtils.fileReader();
                ParserCSV parser = new ParserCSV(str);

                try {
                    init();
                    for (Parameter param : parser.getParameterArray()) {
                        long l = 0;
                        if (param.funcToRead == 3) {
                            l = readFunc3(param);
                            Thread.sleep(200);
                        }

                        if (param.funcToWrite != 0) {
                            System.out.println("going to write");
//                            writeFunc16(param, DataUtils.ConvertLongToRegisters(l));
//                            Thread.sleep(2000);
                        }
                    }
                } catch (IOException | SerialPortException | ModbusException | SerialPortTimeoutException | MqttException | InterruptedException ex) {
                    Logger.getLogger(ModbusTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(runButton);
        setVisible(true);
    }
}
