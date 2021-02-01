/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import ModbusTester.ModbusTesterMainFrame;
import ModbusTester.utils.FuncException;
import ModbusTester.parameter.Parameter;
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
public class ReadWriteRegisters extends Task {

    public StringBuilder resultString = new StringBuilder("<html><style type=\"text/css\">\n"
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
            + "<table><tr><th>Название параметра</th><th>Адрес</th><th>Атрибут</th><th>Диапазон</th><th>Функция</th><th>Чтение</th><th>Запись в диапазоне</th><th>Запись вне диапазона</th></tr>");

    public void test(ArrayList<Parameter> array) {
        progress = 0.0;
        enableDoingDeals = true;
        double step = 1.0 / array.size();

        for (Parameter param : array) {
            if (!enableDoingDeals) {
                return;
            }
            resultString.append("<tr>");
            resultString.append("<td>").append(param.name).append("</td>");
            resultString.append("<td>").append(param.address).append("</td>");
            resultString.append("<td>").append(param.attribute).append("</td>");

            resultString.append("<td>");
            if (param.funcToRead != 0) {
                resultString.append(param.funcToRead);
            }
            if (param.funcToRead != 0 && param.funcToWrite != 0) {
                resultString.append("/");
            }
            if (param.funcToWrite != 0) {
                resultString.append(param.funcToWrite);
            }
            resultString.append("</td>");

            resultString.append("<td>").append(param.getRange()).append("</td>");

            int[] initialDataArray = {0};
            try {
                if (param.funcToRead == 1 || param.funcToRead == 3) {
                    initialDataArray = tryToReadAParam(param);
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
            resultString.append(writingValidValues(param));

            // writing wrong values
            resultString.append(writingWrongValues(param));

            // writing initial value
            if (param.funcToWrite == 5 || param.funcToWrite == 16) {
                System.out.println("going to write initial value");
                try {
                    tryToWriteAParam(param, initialDataArray);
                    //                System.out.println(checkWritingResult(tryToReadParam(param), initialDataArray));                   
                } catch (FuncException ex) {
                    Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            progress += step;
        }
        resultString.append("</table></html>");
        System.out.println(resultString.toString());
    }

    public String writingValidValues(Parameter param) {
        StringBuilder resultStr = new StringBuilder("<td>");

        StringBuilder bunchOfValidValues;

        if (param.funcToWrite == 5 || param.funcToWrite == 16) {

            bunchOfValidValues = new StringBuilder();
            int[][] validValuesToWrite = param.getValidValue();
            for (int i = 0; i < validValuesToWrite.length; i++) {
                int[] validValueToWrite = validValuesToWrite[i];
                boolean allowedToWriteValidValue = false;

                try {
                    tryToWriteAParam(param, validValueToWrite);

                    if (param.funcToRead == 1 || param.funcToRead == 3) {
                        allowedToWriteValidValue = checkWritingResult(tryToReadAParam(param), validValueToWrite);
                    } else {
                        bunchOfValidValues = new StringBuilder("NA");
                        allowedToWriteValidValue = true;
                    }
                } catch (FuncException ex) {
                    Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!allowedToWriteValidValue) {
                    bunchOfValidValues.append("<span style=\"color:red\">");
                }
                bunchOfValidValues.append(param.getValueString(validValueToWrite));
                if (!allowedToWriteValidValue) {
                    bunchOfValidValues.append("</span>");
                }

                bunchOfValidValues.append("<br>");
            }

        } else {
            bunchOfValidValues = new StringBuilder("–");
        }
        resultStr.append(bunchOfValidValues);
        resultStr.append("</td>");

        return resultStr.toString();
    }

    public String writingWrongValues(Parameter param) {
        StringBuilder resultStr = new StringBuilder("<td>");

        StringBuilder bunchOfWrongValues;

        if (param.funcToWrite == 16) {
            if (!param.isLogicalMatchesPhysical()) {

                bunchOfWrongValues = new StringBuilder();
                int[][] wrongValuesToWrite = param.getOutOfRangeValue();

                for (int i = 0; i < wrongValuesToWrite.length; i++) {
                    boolean allowedToWriteWrongValue = true;
                    int[] wrongValueToWrite = wrongValuesToWrite[i];

                    try {
                        tryToWriteAParam(param, wrongValueToWrite);
                        if (param.funcToRead == 3) {
                            if (!checkWritingResult(tryToReadAParam(param), wrongValueToWrite)) {
//                                throw new FuncException();
                            }
                        } else {
                            bunchOfWrongValues = new StringBuilder("NA");
                        }
                    } catch (FuncException ex) {
                        Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
                        allowedToWriteWrongValue = false;
                    }

                    if (allowedToWriteWrongValue) {
                        bunchOfWrongValues.append("<span style=\"color:red\">");
                    }
                    bunchOfWrongValues.append(param.getValueString(wrongValueToWrite));
                    if (allowedToWriteWrongValue) {
                        bunchOfWrongValues.append("</span>");
                    }
                    bunchOfWrongValues.append("<br>");
                }
            } else {
                bunchOfWrongValues = new StringBuilder("не проверялось");
            }

        } else {
            bunchOfWrongValues = new StringBuilder("–");
        }

        resultStr.append(bunchOfWrongValues).append("</td>");
        resultStr.append("</tr>\n");

        return resultStr.toString();
    }

    public int[] tryToReadAParam(Parameter param) throws FuncException {
        int[] dataArray = null;
        try {
            if (param.funcToRead == 1) {
                boolean[] tmpArr = modbusClient.ReadCoils(param.address, param.numOfRegs);
                dataArray = new int[tmpArr.length];
                for (int i = 0; i < dataArray.length; i++) {
                    dataArray[i] = tmpArr[i] ? 1 : 0;
                }
            }
            if (param.funcToRead == 3) {
                dataArray = modbusClient.ReadHoldingRegisters(param.address, param.numOfRegs);
            }
            System.out.println(param.name + " address=" + param.address + " value=" + param.getValueString(dataArray));
            Thread.sleep(200);

        } catch (InterruptedException | ModbusException | IOException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ModbusTesterMainFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return dataArray;
    }

    public void tryToWriteAParam(Parameter param, int[] value) throws FuncException {
        try {
            System.out.println("going to write data " + param.getValueString(value) + " to param " + param.address + " " + param.name);
            if (param.funcToWrite == 5) {
                modbusClient.WriteSingleCoil(param.address, (value[0] == 0) ? false : true);
            }
            if (param.funcToWrite == 16) {
                modbusClient.WriteMultipleRegisters(param.address, value);
            }
        } catch (ModbusException | SocketException | SerialPortException | SerialPortTimeoutException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
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
