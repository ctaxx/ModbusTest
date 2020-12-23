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
        int [] dataArray;
        tester.init("COM28");



        System.loadLibrary("libJNI_RTC");
        long queryPerformanceFrequency = QueryPerformanceFrequency();
        System.out.println("freq");
        System.out.println(queryPerformanceFrequency);
//        for (int i = 0; i < 10; i++) {
//            System.out.println(QueryPerformanceCounter());
//        }
//        System.out.println(Long.toHexString(QueryPerformanceCounter()));

        

        try {
            int [] primaryDataArray = {0};
            int [] lastDataArray = {0};
            
            long primaryMillis = 0L;
            long tmpMillis;
            long lastMillis = 0L;
            
            long primaryQuery = 0L;
            long lastQuery = 0L;
            
            boolean flag = true;
            boolean primaryFlag = false;
            int counter = 0;
            while (flag) {
                dataArray = modbusClient.ReadHoldingRegisters(61568, 2);
                counter++;
//                System.out.println("sec " + DataUtils.prepareData(dataArray));
                tmpCounter = QueryPerformanceCounter();
//                System.out.println(tmpCounter);
                tmpMillis = System.currentTimeMillis();
//                System.out.println("System.millis()" + tmpMillis);
                
                if (!primaryFlag){
                    primaryDataArray = dataArray;
                    primaryMillis = tmpMillis;
                    primaryQuery = tmpCounter;
                    primaryFlag = true;
                }
                if (DataUtils.prepareData(dataArray) > DataUtils.prepareData(primaryDataArray)){
                    lastDataArray = dataArray;
                    lastMillis = tmpMillis;
                    lastQuery = tmpCounter;
                    flag = false;
                    
                } 
            }
           System.out.println("--------------------"); 
            System.out.print("millis delta ");
            System.out.println(lastMillis - primaryMillis);
            System.out.print("queryPerf delta ");
            System.out.println(lastQuery - primaryQuery);
            System.out.println("count = " + counter);
            
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
