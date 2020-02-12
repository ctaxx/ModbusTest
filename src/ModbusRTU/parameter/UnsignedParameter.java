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

    @Override
    public boolean checkInterval() {
        return true;
    }
}
