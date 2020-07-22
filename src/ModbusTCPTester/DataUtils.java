/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTCPTester;

import static de.re.easymodbus.modbusclient.ModbusClient.toByteArrayLong;
import java.nio.ByteBuffer;

/**
 *
 * @author s.bikov
 */
public class DataUtils {

    public static int dataArrayToInt(int[] dataArray) {
        int result = 0;
        for (int i = 0; i < dataArray.length; i++) {
            result |= (dataArray[i] << (i * 16));
        }
        return result;
    }

    public static long prepareData(int[] dataArray) {
        return Integer.toUnsignedLong(dataArrayToInt(dataArray));
    }

    public static int[] ConvertLongToRegisters(long longValue) {
        byte[] doubleBytes = toByteArrayLong(longValue);
        byte[] highRegisterBytes
                = {0, 0,
                    doubleBytes[4],
                    doubleBytes[5]};
        byte[] lowRegisterBytes
                = {0, 0,
                    doubleBytes[6],
                    doubleBytes[7]};
        int[] returnValue
                = {
                    ByteBuffer.wrap(lowRegisterBytes).getInt(),
                    ByteBuffer.wrap(highRegisterBytes).getInt()};
        return returnValue;
    }
    
    public static int[] ConvertARegister(long longValue) {
        byte[] doubleBytes = toByteArrayLong(longValue);
        byte[] highRegisterBytes
                = {0, 0,
                    doubleBytes[4],
                    doubleBytes[5]};
        byte[] lowRegisterBytes
                = {0, 0,
                    doubleBytes[6],
                    doubleBytes[7]};
        int[] returnValue
                = {
                    ByteBuffer.wrap(lowRegisterBytes).getInt(),
//                    ByteBuffer.wrap(highRegisterBytes).getInt()
                };
        return returnValue;
    }
}
