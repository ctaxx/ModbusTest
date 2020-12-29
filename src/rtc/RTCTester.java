/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtc;

import ModbusRTU.ModbusClient;
import ModbusTester.ReadWriteRegisters;
import ModbusTester.parameter.Parameter;
import ModbusTester.utils.DataUtils;
import ModbusTester.utils.FuncException;
import de.re.easymodbus.exceptions.ModbusException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class RTCTester {

    static ModbusClient modbusClient;

    public native static long QueryPerformanceCounter();

    public native static int QueryPerformanceCounterInt32();

    public native static long QueryPerformanceFrequency();

    public static void main(String[] args) {
        RTCTester tester = new RTCTester();
        long tmpCounter;
        final int DURATION = 10_000;
        tester.init("COM28");

        System.loadLibrary("libJNI_RTC");
        long queryPerformanceFrequency = QueryPerformanceFrequency();
        System.out.println("freq " + queryPerformanceFrequency);

        try {
            int[] initialDataArray = modbusClient.ReadHoldingRegisters(61568, 2);
            int[] primaryDataArray = Arrays.copyOf(initialDataArray, initialDataArray.length);
            int[] dataArray = Arrays.copyOf(initialDataArray, initialDataArray.length);
            long millis;
            System.out.println("Primary values");
            tmpCounter = QueryPerformanceCounter();
            millis = System.currentTimeMillis();
            System.out.println("dev sec " + DataUtils.prepareData(dataArray));
            System.out.println("queryPerfCounter " + tmpCounter);
            System.out.println("System.millis()" + millis);
            System.out.println("==============================");
            
            while ((DataUtils.prepareData(dataArray) - DataUtils.prepareData(initialDataArray)) < DURATION) {
                dataArray = modbusClient.ReadHoldingRegisters(61568, 2);
                if (DataUtils.prepareData(dataArray) > DataUtils.prepareData(primaryDataArray)) {
                    tmpCounter = QueryPerformanceCounter(); 
                    primaryDataArray = Arrays.copyOf(dataArray, dataArray.length);
                }
            }
            
            System.out.println("End values");
            System.out.println("queryPerfCounter " + tmpCounter);
            System.out.println("dev sec " + DataUtils.prepareData(dataArray));
            millis = System.currentTimeMillis();
            System.out.println("System.millis()" + millis);

        } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RTCTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int readSec() {
        return 0;
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
}
