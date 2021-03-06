/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

//import lombok.Builder;

import ModbusTester.utils.DataUtils;
import de.re.easymodbus.datatypes.RegisterOrder;

/**
 *
 * @author s.bikov
 */
//@Builder
public class Parameter {

//    static final long MAX_UNSIGNED32_VALUE = Integer.MAX_VALUE * 2;
//    static final String MAX_UNSIGNED16_VALUE = "";
//    static final String MAX_UNSIGNED32_VALUE = "4294967295";

    public String name;
    public String comment;
    public int address;
    public short numOfRegs;
    public short funcToRead;
    public short funcToWrite;
    public String dataType;
    public long physicalMaxValue;
    public long physicalMinValue;
    
    public long logicalMinValue;
    public long logicalMaxValue;
    
    public String attribute;
    public RegisterOrder registerOrder = RegisterOrder.HighLow;

//    public String readResult = "R0 Ch0";
//    public String writeResult = "W0";
    
    public String getName(){
        return this.name;
    }
    
    public Integer getAddress(){
        return this.address;
    }

    public void setPhysicalMaxValue(long physicalMaxValue) {
        this.physicalMaxValue = physicalMaxValue;
    }
    
    public long getPhysicalMaxValue(){
        return this.physicalMaxValue;
    }

    public void setPhysicalMinValue(long physicalMinValue) {
        this.physicalMinValue = physicalMinValue;
    }
    
    public long getPhysicalMinValue(){
        return this.physicalMinValue;
    }
    
    public void setLogicalMaxValue(long logicalMaxValue) {
        this.logicalMaxValue = logicalMaxValue;
    }
    
    public long getLogicalMaxValue(){
        return logicalMaxValue;
    }

    public void setLogicalMinValue(long logicalMinValue) {
        this.logicalMinValue = logicalMinValue;
    } 
    
    public long getLogicalMinValue(){
        return logicalMinValue;
    }

//    public void setResultArray(int [] resultArray){
//        this.resultArray = resultArray;
//        this.value = collectDataToLong(this.resultArray);
//    }
    public void setFuncToRead(String funcToRead) {
        if (funcToRead.equals("-")) {
            this.funcToRead = 0;
        } else {
            this.funcToRead = Short.parseShort(funcToRead);
        }
    }

    public void setFuncToWrite(String funcToWrite) {
        if (funcToWrite.equals("-")) {
            this.funcToWrite = 0;
        } else {
            this.funcToWrite = Short.parseShort(funcToWrite);
        }
    }

    @Override
    public String toString() {
        return name
                + " " + comment
                + " address:" + address
                + " numOfRegs:" + numOfRegs
                + " funcToRead:" + funcToRead
                + " funcToWrite:" + funcToWrite
                + " dataType:" + dataType;
    }

//    public Object[] toObjectArray() {
//        Object[] obj = {name, address, getValueString(), readResult, writeResult};
//        return obj;
//    }
    public void checkInterval(int[] dataArray) {

    }

    protected int dataArrayToInt(int[] dataArray) {
        int res = 0;
        for (int i = 0; i < dataArray.length; i++) {
            res |= (dataArray[i] << (i * 16));
        }
        return res;
    }

    protected long collectDataToLong(int[] dataArray) {
        return Integer.toUnsignedLong(dataArrayToInt(dataArray));
    }

    public String getValueString(int[] dataArray) {
        if (dataArray != null) {
            return Long.toString(collectDataToLong(dataArray));
        } else {
            return "-";
        }
    }

    public int[][] getValidValue() {
        int[][] ia = {{0x1}};
        return ia;
    }

    public int[][] getOutOfRangeValue() {
        int[][] ia = {{0xA}};
        return ia;
    }

    public String getRange() {
        return "range";
    }
    
    public boolean isLogicalMatchesPhysical(){
        return true;
    }
    
    public int [] prepareRegisterData(long data){
        return DataUtils.ConvertLongToRegisters(data);
    }
}
