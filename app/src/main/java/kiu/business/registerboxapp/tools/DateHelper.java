package kiu.business.registerboxapp.tools;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateHelper {

    public final static int DD = 0;
    public final static int MM = 1;
    public final static int MONTH = 2;
    public final static int YY = 3;
    public final static int YYYY = 4;
    public final static int hh = 5;
    public final static int mm = 6;
    public final static int ss = 7;
    public final static int SLASH = 8;
    public final static int UNDERSCOARD = 9;
    public final static int POINTS = 10;
    public final static int SPACE = 11;
    public final static int MINUS = 12;

    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    private static String[] allCalendarValues() {
        Calendar calendar = getCalendar();
        int iDay = calendar.get(Calendar.DAY_OF_MONTH);
        int iMonth = calendar.get(Calendar.MONTH) + 1;
        int iYear = calendar.get(Calendar.YEAR);
        int iHour = calendar.get(Calendar.HOUR_OF_DAY);
        int iMinute = calendar.get(Calendar.MINUTE);
        int iSecond = calendar.get(Calendar.SECOND);

        String day = (iDay < 10 ? "0" + iDay : String.valueOf(iDay));
        String month = (iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth));
        String fullYear = String.valueOf(iYear);
        String year = fullYear.substring(2);
        String hour = (iHour < 10 ? "0" + iHour : String.valueOf(iHour));
        String minute = (iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute));
        String second = (iSecond < 10 ? "0" + iSecond : String.valueOf(iSecond));

        return new String[] {
                day,
                month,
                "",
                year,
                fullYear,
                hour,
                minute,
                second,
                "/",
                "_",
                ":",
                " ",
                "-"
        };
    }

    public static String getDate(Integer... fields) {
        String[] calendarValues = allCalendarValues();
        StringBuilder date = new StringBuilder();
        for (int field : fields)
            date.append(calendarValues[field]);

        return date.toString();
    }

}
