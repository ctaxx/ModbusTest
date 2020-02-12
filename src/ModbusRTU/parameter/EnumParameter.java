/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU.parameter;

//import static ModbusRTU.DataUtils.dataArrayToInt;
/**
 *
 * @author bykov_sp
 */
public class EnumParameter extends Parameter {

    @Override
    public void setMinValue(long minValue) {
        this.minValue = 0;
    }

    @Override
    public boolean checkInterval() {
        return (value >= minValue && value <= maxValue);
    }
}
