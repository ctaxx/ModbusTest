/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

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

    public static int[] trimInts(int[] arr) {
        int[] resultArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            resultArr[i] = arr[i] & 0x0000FFFF;
//            System.out.println(resultArr[i]);
        }
        return resultArr;
    }
    
    public static long prepareData(int [] dataArray){
        return Integer.toUnsignedLong(dataArrayToInt(trimInts(dataArray)));
    }
    
    public static int[] ConvertLongToRegisters(long longValue) {
        byte[] doubleBytes = toByteArrayLong(longValue);
        byte[] highhighRegisterBytes
                = {
                    0, 0,
                    doubleBytes[0],
                    doubleBytes[1],};
        byte[] highlowRegisterBytes
                = {
                    0, 0,
                    doubleBytes[2],
                    doubleBytes[3],};
        int[] returnValue
                = {
                    ByteBuffer.wrap(highlowRegisterBytes).getInt(),
                    ByteBuffer.wrap(highhighRegisterBytes).getInt()};
        return returnValue;
    }
}
