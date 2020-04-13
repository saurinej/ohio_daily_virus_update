package com.joey.ohio_daily_virus_update;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class CustomGregorianCalendarComparator implements Comparator<GregorianCalendar>, Serializable {

	private static final long serialVersionUID = -893072805876134852L;
	
	@Override
	public int compare(GregorianCalendar cal1, GregorianCalendar cal2) {
		
		int year1 = cal1.get(Calendar.YEAR);
		int month1 = cal1.get(Calendar.MONTH);
		int day1 = cal1.get(Calendar.DAY_OF_MONTH);
		int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
		
		if (hour1 >= 14) {
			GregorianCalendar startDate = new GregorianCalendar(year1, month1, day1, 14, 0, 0);
			GregorianCalendar endDate = new GregorianCalendar(year1, month1, (day1 + 1), 14, 0, 0);
			if (cal2.before(startDate)) {
				return 1;
			} else if (cal2.after(endDate)) {
				return -1;
			} else {
				return 0;
			}
		} else {
			GregorianCalendar startDate = new GregorianCalendar(year1, month1, (day1 - 1), 14, 0, 0);
			GregorianCalendar endDate = new GregorianCalendar(year1, month1, day1, 14, 0, 0);
			if (cal2.before(startDate)) {
				return 1;
			} else if (cal2.after(endDate)) {
				return -1;
			} else {
				return 0;
			}
		}
		
	}

}
