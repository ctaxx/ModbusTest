/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.device;

import ModbusTester.parameter.Parameter;
import java.util.ArrayList;

/**
 *
 * @author s.bikov
 */
public class Device {
    public String ipAddress;
    public int port;
    public String protocol;
    public ArrayList<Parameter> parametersArray;
    
    public Parameter getParameterByAddress(int address){
        return parametersArray.stream().filter(p -> p.address == address).findAny().orElse(null);
    }
}
