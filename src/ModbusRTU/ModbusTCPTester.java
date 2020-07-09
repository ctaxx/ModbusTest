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
import java.util.ArrayList;
import java.util.Arrays;
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
    StringBuilder resultString = new StringBuilder("<html><style type=\"text/css\">\n"
            + "   TABLE {\n"
            + "    border-collapse: collapse; /* Убираем двойные линии между ячейками */\n"
            + "   }\n"
            + "   TD, TH {\n"
            + "    padding: 3px; /* Поля вокруг содержимого таблицы */\n"
            + "    border: 1px solid black; /* Параметры рамки */\n"
            + "   }\n"
            + "   TH {\n"
            + "    background: #b0e0e6; /* Цвет фона */\n"
            + "   }\n"
            + "  </style>\n"
            + "<table><tr><th>Название параметра</th><th>Адрес</th><th>Атрибут</th><th>Диапазон</th><th>Чтение</th><th>Запись в диапазоне</th><th>Запись вне диапазона</th></tr>");

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

//            if (param.address == 700) {
                resultString.append("<tr>");
                resultString.append("<td>" + param.name + "</td>");
                resultString.append("<td>" + param.address + "</td>");
                resultString.append("<td>" + "attribute" + "</td>");   //attribute must be here
                resultString.append("<td>" + "range" + "</td>");

                int[] initialDataArray = {0};
                try {
                    if (param.funcToRead == 3) {
                        initialDataArray = tryToReadParam(param);
                        resultString.append("<td>" + "+" + "</td>");
                    } else {
                        System.err.println(param.name + " reading impossible or haven't implemented yet");
                        resultString.append("<td>" + "NA" + "</td>");
                    }

                } catch (FuncException ex) {
                    resultString.append("<td>" + "–" + "</td>");
                    System.err.println(param.name + " reading finished with error " + ex.getMessage());
                }

                // writing valid value
                resultString.append("<td>");
                int[] validValueToWrite = param.getValidValue();
                if (param.funcToWrite == 16) {
                    resultString.append(tryToWriteParam(param, validValueToWrite));
                    resultString.append(" - ");

                    try {
                        if (param.funcToRead == 3) {
                            resultString.append(checkWritingResult(tryToReadParam(param), validValueToWrite));
//                        resultString.append("\n");
                        } else {
                            resultString.append("NA");
                        }
                    } catch (FuncException ex) {
                        Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    resultString.append("не проверялось");
//                }// if param ==
                resultString.append("</td>");

                
                // writing initial value
                if (param.funcToWrite == 16){
                    tryToWriteParam(param, initialDataArray);
 //                System.out.println(checkWritingResult(tryToReadParam(param), initialDataArray));                   
                }
                
//                --------------------------------------------------------------------------------------
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
                resultString.append("</tr>");
            }

        }
        resultString.append("</table></html>");
        System.out.println(resultString.toString());

    }

    public int[] tryToReadParam(Parameter param) throws FuncException {
//        have to write result to param
        int[] dataArray = null;
//        if (param.funcToRead == 3) {
        try {
            dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
            System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
//            resultString.append("<td>" + "+" + "</td>");
//                param.checkInterval(dataArray);
            Thread.sleep(200);

        } catch (InterruptedException | ModbusException | IOException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ModbusTesterMainFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
//            } catch (FuncException ex) {
//                System.err.println(param.name + " reading finished with error " + ex.getMessage());
        }
        //     } else {
        //         System.err.println(param.name + " reading impossible or haven't implemented yet");
        //         resultString.append("<td>" + "NA" + "</td>");
        //      }
        return dataArray;
    }

    public String tryToWriteParam(Parameter param, int[] value) {
//        have to write result to param
        StringBuilder result = new StringBuilder();
        try {
            result.append(param.getValueString(value));
            System.out.println("going to write data " + param.getValueString(value) + " to param " + param.address + " " + param.name);
            modbusClient.WriteMultipleRegisters(param.address, value);
        } catch (FuncException ex) {
            System.err.print(ex.getMessage());
            System.err.println(" Exception - we cannot write data");
        } catch (ModbusException | SocketException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(200);

        } catch (InterruptedException ex) {
            Logger.getLogger(ModbusTesterMainFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return result.toString();
    }

    public boolean checkWritingResult(int[] a1, int[] a2) {
        return Arrays.equals(a1, a2);
    }

}
