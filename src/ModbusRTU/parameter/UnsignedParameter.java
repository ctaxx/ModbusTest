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
        switch ((int)size) {
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
    public String getRange(){
        return logicalMinValue + ".." + logicalMaxValue;
    }
    
    @Override
    public int [] getValidValue(){
        int[] ia = DataUtils.ConvertLongToRegisters(logicalMinValue + 3L);
//        int[] ia = {0xA};
        return ia;
    }

}
