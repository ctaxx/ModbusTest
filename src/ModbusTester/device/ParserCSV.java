/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.device;

import ModbusTester.parameter.Float32Parameter;
import ModbusTester.parameter.UnsignedParameter;
import ModbusTester.parameter.Parameter;
import ModbusTester.parameter.EnumParameter;
import ModbusTester.parameter.DateTime32Parameter;
import ModbusTester.parameter.BitParameter;
import ModbusTester.parameter.String256Parameter;
import de.re.easymodbus.datatypes.RegisterOrder;
import java.util.ArrayList;

/**
 *
 * @author s.bikov
 */
public class ParserCSV {

    private ArrayList<ArrayList<String>> myArray;
    private ArrayList<Parameter> parameterArray;
    public String currentIP;
    public String protocol;

    public ArrayList<Parameter> getParameterArray() {
        return parameterArray;
    }

    public ParserCSV(String s) {
        this.myArray = parse(s);
        this.parameterArray = arrayToParameters(this.myArray);
        this.currentIP = myArray.get(1).get(1);
        this.protocol = myArray.get(4).get(1);
    }

    public ArrayList<ArrayList<String>> parse(String s) {
        ArrayList<ArrayList<String>> yArrayList = new ArrayList<>();

        String[] arrayOfStrings = s.split("\r\n");

        for (int i = 0; i < arrayOfStrings.length; i++) {
            String[] arrayOfColumns = arrayOfStrings[i].split(";");
            ArrayList<String> xArrayList = new ArrayList<>();

            for (int j = 0; j < arrayOfColumns.length; j++) {
                xArrayList.add(arrayOfColumns[j]);
            }
            yArrayList.add(xArrayList);
        }

        return yArrayList;
    }

    public String arrayToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.myArray.size(); i++) {
            for (int j = 0; j < this.myArray.get(i).size(); j++) {
                if (j != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(this.myArray.get(i).get(j));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public String getCell(int x, int y) {
        return null;
    }

    public void setCell(int x, int y, String string) {
        this.myArray.get(y).set(x, string);
    }

    public void restoreState(ArrayList<ArrayList<String>> array) {
        this.myArray = array;
    }

    public boolean isCorrectCoords(int x, int y) {
        if (x < 0 || y < 0 || (y > this.myArray.size() - 1) || (x > this.myArray.get(y).size() - 1)) {
            System.out.println("you are wrong with your coords!");
            return false;
        }
        return true;
    }

    private ArrayList<Parameter> arrayToParameters(ArrayList<ArrayList<String>> myArray) {
        ArrayList<Parameter> parametersArrayList = new ArrayList();
//        Parameter param = null;
        for (int i = 8; i < myArray.size(); i++) {
            Parameter param = null;
            String dataType = myArray.get(i).get(7);
            if (dataType.contains("Unsigned")) {
                param = new UnsignedParameter();
                param.setPhysicalMaxValue(Integer.parseInt(dataType.replaceAll("\\D", "")));

                param.setFuncToWrite(myArray.get(i).get(6));

                if (myArray.get(i).get(8).matches("\\D")) {
                    param.setLogicalMinValue(param.getPhysicalMinValue());
                } else {
                    param.setLogicalMinValue(Long.parseLong(myArray.get(i).get(8)));
                }

                if (myArray.get(i).get(9).matches("\\D")) {
                    param.setLogicalMaxValue(param.getPhysicalMaxValue());
                } else {
                    param.setLogicalMaxValue(Long.parseLong(myArray.get(i).get(9)));
                }

            } else if (dataType.contains("Enum")) {
                param = new EnumParameter();
                param.setPhysicalMaxValue(Long.parseLong(dataType.replaceAll("Enum", "").trim()) - 1);
//            temporary stub - needs adaptive design
                param.setFuncToWrite(myArray.get(i).get(6));
            } else if (dataType.contains("Date time")) {
                param = new DateTime32Parameter();
//            temporary stub
                param.setFuncToWrite("-");
            } else if (dataType.contains("bit")) {
                param = new BitParameter();
            } else if (dataType.contains("Float 32")) {
                param = new Float32Parameter();
                //            param.setFuncToWrite(myArray.get(i).get(6));
            } else if (dataType.contains("String 256")) {
                param = new String256Parameter();
//            temporary stub - needs adaptive design
                param.setFuncToWrite(myArray.get(i).get(6));
            } else {
                param = new Parameter();
//            temporary stub                
                param.setFuncToWrite("-");
            }

            param.name = myArray.get(i).get(1) + " - " + myArray.get(i).get(0);
            param.comment = myArray.get(i).get(1);
            param.address = Integer.parseInt(myArray.get(i).get(2));
            param.numOfRegs = Short.parseShort(myArray.get(i).get(4));
            param.setFuncToRead(myArray.get(i).get(5));
//            param.setFuncToWrite(myArray.get(i).get(6));
            StringBuilder attrBuilder = new StringBuilder();
            if (!myArray.get(i).get(5).contains("-")) {
                attrBuilder.append("R");
            }
            if (!myArray.get(i).get(6).contains("-")) {
                attrBuilder.append("W");
            }
            param.attribute = attrBuilder.toString();
            param.dataType = myArray.get(i).get(7);
            parametersArrayList.add(param);

            if (myArray.get(i).size() >= 11) {
                if ("LowHigh".equals(myArray.get(i).get(10).trim())) {
                    param.registerOrder = RegisterOrder.LowHigh;
                }
            }

        }
        return parametersArrayList;
    }

    public Device getDevice() {
        Device device = new Device();
        device.parametersArray = this.parameterArray;
        device.ipAddress = this.currentIP;
//        device.port =
        device.protocol = this.protocol;
        return device;
    }
}
