/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

/**
 *
 * @author s.bikov
 */
import de.re.easymodbus.modbusserver.*;
import java.io.IOException;

public class run {

    public static void main(String[] args) throws IOException {
        ModbusServer modbusServer = new ModbusServer();
        modbusServer.setPort(1522);//Note that Standard Port for Modbus TCP communication is 502 
        modbusServer.coils[1] = true;
        modbusServer.holdingRegisters[1] = 1234;
   
            modbusServer.Listen();
       
    }
}
