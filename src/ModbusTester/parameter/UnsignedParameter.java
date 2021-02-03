/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

import ModbusTester.utils.DataUtils;

/**
 *
 * @author bykov_sp
 */
public class UnsignedParameter extends Parameter {

    //physicalMinValue = 0;
    @Override
    public void setPhysicalMaxValue(long size) {
        switch ((int) size) {
            case 8:
                physicalMaxValue = 255L;
                break;
            case 16:
                physicalMaxValue = 65535L;
                break;
            case 32:
                physicalMaxValue = 4294967295L;
                break;
            case 48:
                break;
        }
    }

    @Override
    public String getRange() {
        return logicalMinValue + ".." + logicalMaxValue;
    }

    @Override
    public int[][] getValidValue() {
        int arraySize;
        long logicalMediumValue = 0;
        if ((logicalMaxValue - logicalMinValue) > 1) {
            arraySize = 3;
            logicalMediumValue = (logicalMaxValue + logicalMinValue) / 2;
        } else {
            arraySize = 2;
        }
        if (numOfRegs == 1) {
            if (arraySize == 3) {
                return new int[][]{DataUtils.ConvertARegister(logicalMinValue),
                    DataUtils.ConvertARegister(logicalMediumValue),
                    DataUtils.ConvertARegister(logicalMaxValue)};
            }
            if (arraySize == 2) {
                return new int[][]{DataUtils.ConvertARegister(logicalMinValue),
                    DataUtils.ConvertARegister(logicalMaxValue)};
            }
            return new int[][]{DataUtils.ConvertARegister(logicalMinValue + 3L)};
        }
        
 //        TODO       
        if (numOfRegs == 2) {
            if (arraySize == 3) {
                return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue),
                    DataUtils.ConvertLongToRegisters(logicalMediumValue),
                    DataUtils.ConvertLongToRegisters(logicalMaxValue)};
            }
            if (arraySize == 2) {
                return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue),
                    DataUtils.ConvertLongToRegisters(logicalMaxValue)};
            }
            return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue + 3L)};
        }
        return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue + 3L)};
//        int[] ia = {0xA};
//        return ia;
    }

    @Override
    public int[][] getOutOfRangeValue() {
        if (numOfRegs == 1) {
            if ((logicalMinValue > physicalMinValue) && (logicalMaxValue < physicalMaxValue)) {
                return new int[][]{DataUtils.ConvertARegister(logicalMinValue - 1L), DataUtils.ConvertARegister(logicalMaxValue + 1L)};
            }
            if (logicalMinValue > physicalMinValue) {
                return new int[][]{DataUtils.ConvertARegister(logicalMinValue - 1L)};
            }
            return new int[][]{DataUtils.ConvertARegister(logicalMaxValue + 1L)};
        }

        if ((logicalMinValue > physicalMinValue) && (logicalMaxValue < physicalMaxValue)) {
            return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue - 1L), DataUtils.ConvertLongToRegisters(logicalMaxValue + 1L)};
        }
        if (logicalMinValue > physicalMinValue) {
            return new int[][]{DataUtils.ConvertLongToRegisters(logicalMinValue - 1L)};
        }
        return new int[][]{DataUtils.ConvertLongToRegisters(logicalMaxValue + 1L)};
    }

    @Override
    public boolean isLogicalMatchesPhysical() {
        return ((logicalMinValue == physicalMinValue) && (logicalMaxValue == physicalMaxValue));
    }
    
    @Override
    public int [] prepareRegisterData(long data){
        if (numOfRegs == 1) {
            return DataUtils.ConvertARegister(data);
        }
        return DataUtils.ConvertLongToRegisters(data);
    }
}
