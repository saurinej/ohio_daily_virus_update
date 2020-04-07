package com.joey.ohio_daily_virus_update;
import java.io.Serializable;
import java.util.GregorianCalendar;

public class SingleDayCount implements Serializable {
	
	private GregorianCalendar date;
	private int caseCount;
	
	public SingleDayCount(GregorianCalendar date, int caseCount) {
		this.date = date;
		this.caseCount = caseCount;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public int getCaseCount() {
		return caseCount;
	}
	
	
	
}
