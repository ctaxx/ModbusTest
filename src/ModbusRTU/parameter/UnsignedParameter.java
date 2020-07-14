/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU.parameter;

/**
 *
 * @author bykov_sp
 */
public class UnsignedParameter extends Parameter {

    public UnsignedParameter(int size) {
        this.physicalMinValue = 0;
        switch (size) {
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

}
