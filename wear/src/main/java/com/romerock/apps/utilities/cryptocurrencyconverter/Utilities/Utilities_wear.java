package com.romerock.apps.utilities.cryptocurrencyconverter.Utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Ebricko on 13/03/2018.
 */

public class Utilities_wear {

    public static String getHour(long time, Context context) {
        Date date = new Date(time * 1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm");
        jdf.setTimeZone(TimeZone.getDefault());
        String dateSend = jdf.format(date);
        return dateSend;
    }

    public static void openKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            if (((Activity) context).getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) context).getCurrentFocus().getWindowToken(), 0);
    }



    public static void addArrow(Context context, float val, ImageView view) {
        int resID = 0;
        String uri = "@drawable/";
        if (val < 0) {
            resID = context.getResources().getIdentifier(uri + "path_2", null, context.getPackageName());
            view.setRotation(0);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(resID);
        } else if (val > 0) {
            view.setVisibility(View.VISIBLE);
            resID = context.getResources().getIdentifier(uri + "path_2_copy", null, context.getPackageName());
            view.setImageResource(resID);
        } else
            view.setVisibility(View.INVISIBLE);
    }



/*
    public static String formatAmountOperation(String amount) {
        String loc = String.valueOf(Resources.getSystem().getConfiguration().locale);
        Locale locale = new Locale(loc);
        String pattern;
        pattern = "###,##0.00##";
        DecimalFormat decimalFormat = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(Double.parseDouble(amount));
    }
*/

 /*   public static String makeOperationWithFormat(double baseValue', Double currency, Double currencyDiv) {
        String result = "";
        result = formatAmountOperation(String.valueOf((baseValue * currency) / currencyDiv));
        return result;
    }*/

    public static double eval(final String str, final Context context) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {

                    //showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x = 0;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    try {
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } catch (Exception e) {
                        x = 0;
                        //  showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                    }
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else {
                        //    showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                    }
                } else {
                    //showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                }
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }



    public static String getCurrencyDefault() {
        String currency;
        try {
            currency = String.valueOf(Currency.getInstance(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            currency = "?"; // default symbol
        }
        return currency;
    }

    public static String getFormatedDate(String date) {
        Date oneWayTripDate = null;
        SimpleDateFormat input = null;
        SimpleDateFormat output = null;
        input = new SimpleDateFormat("yyyy-MM-dd");
        output = new SimpleDateFormat("MMM dd''yy");
        try {
            oneWayTripDate = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = output.format(oneWayTripDate);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        return date;
    }


    public static String getFormatedDateOnlyTime(String date) {
        double dateToUse= Double.valueOf(date.replace(",","."))*1000;

        Date d = new Date((long) dateToUse);
        DateFormat f = new SimpleDateFormat("MMM dd''yy'T'HH:mm a");
        date =f.format(d);
        return date;
    }

    public static String getCurrentLocalDateTimeStamp(boolean isWithTime) {
        SimpleDateFormat sdf;
        if(!isWithTime)
            sdf= new SimpleDateFormat("MMM dd''yy");
        else
            sdf= new SimpleDateFormat("MMM dd''yy HH:mm a");
        Date now = new Date();
        String strDate = sdf.format(now);
        strDate = strDate.substring(0, 1).toUpperCase() + strDate.substring(1);
        return strDate;
    }

    public static float getDateToUnixTimeStamp(String str_date) {
        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = (Date) formatter.parse(str_date);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getDateFromUnixTimestamp(long unixSeconds) {
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static long getCurrentTimeStamp(){
       return  System.currentTimeMillis()/1000;
    }


    public static final String TestDevice(final String s) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

}
