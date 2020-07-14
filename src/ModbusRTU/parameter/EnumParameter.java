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
    public void setPhysicalMinValue(long minValue) {
        this.physicalMinValue = 0;
    }

    @Override
    public void checkInterval(int[] dataArray) {
        long l = collectDataToLong(dataArray);
        if (l >= physicalMinValue && l <= physicalMaxValue) {
//            this.readResult = "R+ Ch+";
        }
    }
    
    @Override
    public int [] getOutOfRangeValue(){
        int i = (int)this.physicalMaxValue + 1;
        int ia [] = {i};
        return ia;
    }
    
    @Override
    public String getRange(){
        return physicalMinValue + ".."+ physicalMaxValue;
    }
}
