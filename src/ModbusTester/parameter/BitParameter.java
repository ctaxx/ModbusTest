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
public class BitParameter extends Parameter{
    
    @Override
    public String getRange(){
        return "0/1";
    }
    
    @Override
    public int [][] getValidValue(){
        return new int[][]{{0}, {1}};
    }
}
