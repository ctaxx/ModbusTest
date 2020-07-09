/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import ModbusRTU.parameter.Parameter;
import de.re.easymodbus.exceptions.ModbusException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author bykov_s_p
 */
public class ModbusTCPTester {

    ModbusClient modbusClient;

    public boolean init(String ip, int port) {
        modbusClient = new ModbusClient(ip, port);
        boolean success = modbusClient.Available(2500);
        System.out.println(success);
        if (success) {
            try {
                modbusClient.Connect();
            } catch (IOException ex) {
                Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public void test(ArrayList<Parameter> array) {
        for (Parameter param : array) {
//                            int[] initialDataArray = tryToReadParam(param);
//                            checkParam(param);
//                            tryToWriteParam(param, param.getValidValue());
//                            checkParam(param);
//                            tryToWriteParam(param, param.getValidValue());
//                            checkParam(param);
//                            tryToWriteParam(param, param.getValidValue());
//                            checkParam(param);
//                            tryToWriteOutOfRangeData(param, param.getOutOfRangeValue());
//                            checkParam(param);
//                            tryToWriteOutOfRangeData(param, param.getOutOfRangeValue());
//                            checkParam(param);
//                            tryToWriteParam(param, initialDataArray);

        }

    }

    public int[] tryToReadParam(Parameter param) throws ModbusException, SocketException, IOException, UnknownHostException, SerialPortException, SerialPortTimeoutException, FuncException {
//        have to write result to param
        int[] dataArray = null;
        if (param.funcToRead == 3) {
            try {
                dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
                System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
//                param.checkInterval(dataArray);
                Thread.sleep(200);

            } catch (InterruptedException ex) {
                Logger.getLogger(ModbusTester.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println(param.name + " reading impossible or haven't implemented yet");
        }
        return dataArray;
    }

    public void tryToWriteParam(Parameter param, int[] value) throws ModbusException, SocketException, IOException, UnknownHostException, SerialPortException, SerialPortTimeoutException {
//        have to write result to param
        if (param.funcToWrite != 0) {
            try {
                System.out.println("going to write data " + value + " to param " + param.address + " " + param.name);
                modbusClient.WriteMultipleRegisters(param.address, value);

            } catch (FuncException ex) {
                System.err.print(ex.getMessage());
                System.err.println(" Exception - we cannot write data");
            }

            try {
                Thread.sleep(200);

            } catch (InterruptedException ex) {
                Logger.getLogger(ModbusTester.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void checkParam(Parameter param) {
//        unclear if we need this function
    }

}
