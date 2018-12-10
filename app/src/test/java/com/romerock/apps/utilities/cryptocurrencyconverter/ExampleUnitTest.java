package com.romerock.apps.utilities.cryptocurrencyconverter;

import org.junit.Test;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTime(){

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        final String time_chat_s = df.format(1520877250);
        System.out.println(time_chat_s);

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)1520877250*1000);

        System.out.print(currenTimeZone.getTime());


        long unix_seconds = 1520877250;
        //convert seconds to milliseconds

    }

    @Test
    public void addMonths(){


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        java.util.Date dt = cal.getTime();
        System.out.println(cal.getTime().getTime()/1000);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format1.format(cal.getTime());
        System.out.println(formatted);

    }

    @Test
    public void checkInstance(){
        String numberString="88888,33";
        double returnVal;
        Number number = new Number() {
            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0;
            }

            @Override
            public float floatValue() {
                return 0;
            }

            @Override
            public double doubleValue() {
                return 0;
            }
        };
        try {
            NumberFormat defaultFormat = NumberFormat.getNumberInstance();
            defaultFormat.setMaximumFractionDigits(4);
            defaultFormat.setMinimumFractionDigits(2);
            number = defaultFormat.parse(numberString);
        } catch (Exception e) {
            try {
                returnVal = Double.parseDouble(numberString);
            } catch (Exception val) {
            }
        }
        System.out.println(""+number);     }

    }