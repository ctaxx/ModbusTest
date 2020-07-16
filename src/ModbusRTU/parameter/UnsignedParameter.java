/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU.parameter;

import ModbusRTU.DataUtils;

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
    public int[] getValidValue() {
        if (numOfRegs == 1) {
            return DataUtils.ConvertARegister(logicalMinValue + 3L);
        }
        int[] ia = DataUtils.ConvertLongToRegisters(logicalMinValue + 3L);
//        int[] ia = {0xA};
        return ia;
    }

    @Override
    public int[] getOutOfRangeValue() {
        if (numOfRegs == 1) {
            return DataUtils.ConvertARegister(logicalMaxValue + 1L);
        }
        return DataUtils.ConvertLongToRegisters(logicalMaxValue + 1L);
    }

    @Override
    public boolean isLogicalMatchesPhysical() {
        return ((logicalMinValue == physicalMinValue) && (logicalMaxValue == physicalMaxValue));
    }
}
