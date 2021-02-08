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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class RTCInspector2 {

    static ModbusClient modbusClient;
    public static final String RESULT_PATH = "D:\\RTCInspector result.txt";

    public static void main(String[] args) {
        RTCInspector2 tester = new RTCInspector2();
        tester.init("COM8");
        tester.readDataFromDevice();
    }

    public void readDataFromDevice() {
        StringBuilder resultString = new StringBuilder();

        try {
//            read name
            int[] devName;

//            read number
            int[] devNumber;

//            read rtc
            int[] yearDataArray = modbusClient.ReadHoldingRegisters(0x405, 1);
            int[] monthDataArray = modbusClient.ReadHoldingRegisters(0x404, 1);
            int[] dayDataArray = modbusClient.ReadHoldingRegisters(0x403, 1);
            int[] hoursDataArray = modbusClient.ReadHoldingRegisters(0x402, 1);
            int[] minutesDataArray = modbusClient.ReadHoldingRegisters(0x401, 1);
            int[] secondsDataArray = modbusClient.ReadHoldingRegisters(0x400, 1);
            
            LocalDateTime pcTime = LocalDateTime.now();

//            LocalDateTime devTime = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0).plusSeconds(DataUtils.prepareData(dataArray));
            LocalDateTime devTime = LocalDateTime.of((int)DataUtils.prepareData(yearDataArray) + 2000,
                    (int)DataUtils.prepareData(monthDataArray),
                    (int)DataUtils.prepareData(dayDataArray),
                    (int)DataUtils.prepareData(hoursDataArray),
                    (int)DataUtils.prepareData(minutesDataArray),
                    (int)DataUtils.prepareData(secondsDataArray));
            System.out.println("devTime: " + devTime);
            resultString.append("devTime: ").append(devTime).append("\t");

            
            System.out.println("pcTime: " + pcTime);
            resultString.append("pcTime: ").append(pcTime).append("\t");

            Duration d = Duration.between(pcTime, devTime);
            System.out.println("difference: " + d.getSeconds() + " sec");
            resultString.append("diff: ").append(d.getSeconds()).append("\n");

            writeDataToResultFile(RESULT_PATH, resultString.toString());

        } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(RTCInspector2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
