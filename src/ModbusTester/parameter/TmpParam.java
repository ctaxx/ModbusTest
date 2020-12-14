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
public class TmpParam {
    public int address;
    public long value;

    public TmpParam(int address, long value) {
        this.address = address;
        this.value = value;
    }
}
