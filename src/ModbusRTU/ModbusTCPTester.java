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
  //          if (param.address == 308) {
            resultString.append("<tr>");
            resultString.append("<td>").append(param.name).append("</td>");
            resultString.append("<td>").append(param.address).append("</td>");
            resultString.append("<td>").append(param.attribute).append("</td>");  
            resultString.append("<td>").append(param.getRange()).append("</td>");

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
   
            if (param.funcToWrite == 16) {
                int[] validValueToWrite = param.getValidValue();
                resultString.append(param.getValueString(validValueToWrite));
                resultString.append(" - ");

                try {
                    tryToWriteParam(param, validValueToWrite);
              //      tryToWriteParam(param, initialDataArray);
                    if (param.funcToRead == 3) {
                        resultString.append(checkWritingResult(tryToReadParam(param), validValueToWrite));
//                        resultString.append("\n");
                    } else {
                        resultString.append("NA");
                    }
                } catch (FuncException ex) {
                    Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
                    resultString.append("false");
                }

            } else {
                resultString.append("не проверялось");
            }
            resultString.append("</td>");    
            
            
            // writing wrong values
            resultString.append("<td>");
            int[] wrongValueToWrite = param.getOutOfRangeValue();
            if ((param.funcToWrite == 16)&&(!param.isLogicalMatchesPhysical())) {
                resultString.append(param.getValueString(wrongValueToWrite));
                resultString.append(" - ");
                
                try {
                    tryToWriteParam(param, wrongValueToWrite);
                    if (param.funcToRead == 3) {
                        resultString.append(checkWritingResult(tryToReadParam(param), wrongValueToWrite));
//                        resultString.append("\n");
                    } else {
                        resultString.append("NA");
                    }
                } catch (FuncException ex) {
                    Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
                    resultString.append("false");
                }

            } else {
                resultString.append("не проверялось");
            }
            resultString.append("</td>");

            resultString.append("</tr>");

            // writing initial value
            if (param.funcToWrite == 16) {
                System.out.println("going to write initial value");
                try {
                    tryToWriteParam(param, initialDataArray);
                    //                System.out.println(checkWritingResult(tryToReadParam(param), initialDataArray));                   
                } catch (FuncException ex) {
                    Logger.getLogger(ModbusTCPTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
 //       }
        }
        resultString.append("</table></html>");
        System.out.println(resultString.toString());

    }

    public int[] tryToReadParam(Parameter param) throws FuncException {
        int[] dataArray = null;
        try {
            dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
            System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
            Thread.sleep(200);

        } catch (InterruptedException | ModbusException | IOException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ModbusTesterMainFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return dataArray;
    }

    public void tryToWriteParam(Parameter param, int[] value) throws FuncException {
        try {
            System.out.println("going to write data " + param.getValueString(value) + " to param " + param.address + " " + param.name);
            modbusClient.WriteMultipleRegisters(param.address, value);
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
    }

    public boolean checkWritingResult(int[] a1, int[] a2) {
        return Arrays.equals(a1, a2);
    }

}
