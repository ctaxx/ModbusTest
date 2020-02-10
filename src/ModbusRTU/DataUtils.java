/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import com.sun.javafx.css.converters.DurationConverter;
import static de.re.easymodbus.modbusclient.ModbusClient.toByteArrayLong;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
