/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

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
        return Arrays.stream(dataArray)
                .mapToObj(v -> Character.toString((char) (v & 0xFF)) + Character.toString((char) (v >> 8)))
                .collect(Collectors.joining());
    }
}
