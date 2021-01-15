/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author s.bikov
 */
public class String256Parameter extends Parameter {

    @Override
    public String getRange() {
        return "String 256";
    }

    @Override
    public String getValueString(int[] dataArray) {
//        TODO avoid creating a lot of String objects
        byte [] resultBytes = new byte[dataArray.length * 2];
        for (int i = 0; i < dataArray.length; i++) {
            byte[] b = ByteBuffer.allocate(4).putInt(dataArray[i]).array();
            resultBytes[i * 2] = b[3];
            resultBytes[i * 2 + 1] = b[2];
        }
        
        return new String(resultBytes, "win1251");

//        return Arrays.stream(dataArray)
//                .mapToObj(v -> Character.toString((char) (v & 0xFF)) + Character.toString((char) (v >> 8)))
//                .collect(Collectors.joining());
    }
}
