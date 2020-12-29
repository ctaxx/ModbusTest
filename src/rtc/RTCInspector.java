/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtc;

import ModbusRTU.ModbusClient;
import ModbusTester.ReadWriteRegisters;
import ModbusTester.utils.DataUtils;
import ModbusTester.utils.FuncException;
import de.re.easymodbus.exceptions.ModbusException;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class RTCInspector {

    static ModbusClient modbusClient;
    public static final String RESULT_PATH = "D:\\RTCInspector result.txt";

    public static void main(String[] args) {
        RTCInspector tester = new RTCInspector();
        tester.init("COM28");

        try {
//            read name
            int[] devName;

//            read number
            int[] devNumber;

//            read rtc
            int[] dataArray = modbusClient.ReadHoldingRegisters(61568, 2);
            long millis = System.currentTimeMillis();
            System.out.println("dev sec " + DataUtils.prepareData(dataArray));
            System.out.println("System.millis()" + millis);
            String tmpString = "System.millis()=" + millis;
            writeDataToResultFile(RESULT_PATH, tmpString);

        } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readSec() {

    }

    public boolean init(String port) {
        if ((modbusClient != null) && modbusClient.isConnected()) {
            return true;
        }
        modbusClient = new ModbusClient(port, 115200, 8);

        try {
            modbusClient.Connect();
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modbusClient.isConnected();
    }

    public static void writeDataToResultFile(String path, String data) {
        try {
            if (Files.exists(Paths.get(path))) {
                Files.write(Paths.get(path), data.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException ex) {
            Logger.getLogger(RTCInspector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
