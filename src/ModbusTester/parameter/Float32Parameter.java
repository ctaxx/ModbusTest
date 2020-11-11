/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

/**
 *
 * @author s.bikov
 */
public class Float32Parameter extends Parameter {
    @Override
    public String getRange() {
        return Float.toString(Float.MIN_VALUE) + ".." + Float.toString(Float.MAX_VALUE);
    }
    
    
}
