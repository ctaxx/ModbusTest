/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author bykov_sp
 */
public class DateTime32Parameter extends Parameter {

    @Override
    public String getValueString(int [] dataArray) {
        if (dataArray != null) {
            long secondsFrom2000 = collectDataToLong(dataArray);
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            calendar.set(2000, 0, 1, 0, 0, 0);
            int minutes = (int) secondsFrom2000 / 60;
            int seconds = (int) secondsFrom2000 % 60;
            calendar.add(GregorianCalendar.MINUTE, minutes);
            calendar.add(GregorianCalendar.SECOND, seconds);
            Instant instant = calendar.toInstant();
            return instant.toString();
        }
        return "-";
    }

}
