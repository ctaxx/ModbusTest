/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s.bikov
 */
public class String256Parameter extends Parameter {

    public static final String ENCODING = "Cp1251";
    public static final String[] TEST_STRINGS = {"Test", "Тест", "Dev"};

    @Override
    public String getRange() {
        return "String 256";
    }

    @Override
    public String getValueString(int[] dataArray) {
//        return Arrays.stream(dataArray)
//                .mapToObj(v -> Character.toString((char) (v & 0xFF)) + Character.toString((char) (v >> 8)))
//                .collect(Collectors.joining());        
        byte[] resultBytes = new byte[dataArray.length * 2];
        for (int i = 0; i < dataArray.length; i++) {
            byte[] b = ByteBuffer.allocate(4).putInt(dataArray[i]).array();
            resultBytes[i * 2] = b[3];
            resultBytes[i * 2 + 1] = b[2];
        }

        try {
            return new String(resultBytes, ENCODING);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(String256Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public int[][] getValidValue() {
        try {
            byte[] testStringArray = TEST_STRINGS[1].getBytes(ENCODING);
            int[] dataToRegister = new int[16];
            for (int i = 0; i < testStringArray.length; i += 2) {
                byte[] tmp = {
                    0, 0,
                    (i + 1) < testStringArray.length ? testStringArray[i + 1] : 0,
                    testStringArray[i]
                };
                dataToRegister[i / 2] = ByteBuffer.wrap(tmp).getInt();
            }
            return new int[][]{dataToRegister};
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(String256Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new int[0][0];
    }

    @Override
    public int[][] getOutOfRangeValue() {
//        TODO too long strings
        return new int[0][0];
    }
    
    @Override
    public boolean isLogicalMatchesPhysical() {
//        TODO too long strings;
        return true;
    }
}
