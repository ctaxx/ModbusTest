/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import lombok.Builder;

/**
 *
 * @author s.bikov
 */
@Builder
public class Parameter { 
    
//    static final long MAX_UNSIGNED32_VALUE = Integer.MAX_VALUE * 2;
    static final String MAX_UNSIGNED32_VALUE = "4294967295";
    
    String name;
    String comment;
    int address;
    short numOfRegs;
    short funcToRead;
    short funcToWrite;
    String dataType;
    
    @Override
    public String toString(){
        return name
                + " " + comment
                + " address:" + address
                + " numOfRegs:" + numOfRegs
                + " funcToRead:" + funcToRead
                + " funcToWrite:" + funcToWrite
                + " dataType:" + dataType;
    }
}
