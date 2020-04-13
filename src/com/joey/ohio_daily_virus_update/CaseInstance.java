package com.joey.ohio_daily_virus_update;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaseInstance implements Serializable {
	
	private static final long serialVersionUID = -7897437942988507454L;
	
	private String sex;
	private String ageRange;
	private Date onsetDate;
	private Date deathDate;
	private int count;
	private int deathCount;
	private int hospitalizedCount;
	
	public CaseInstance(String sex, 
						String ageRange, 
						String onsetDate, 
						String deathDate, 
						int count, 
						int deathCount, 
						int hospitalizedCount) {
		this.setSex(sex);
		this.setAgeRange(ageRange);
		this.setOnsetDate(this.parseDate(onsetDate));
		this.setDeathDate(this.parseDate(deathDate));
		this.setCount(count);
		this.setDeathCount(deathCount);
		this.setHospitalizedCount(hospitalizedCount);
	}
	
	public Date parseDate(String date) {
		if(date.isBlank()) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy");
		try {
			Date parsedDate = format.parse(date);
			return parsedDate;
		} catch (ParseException e) {
			return null;
		}
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}

	public int getHospitalizedCount() {
		return hospitalizedCount;
	}

	public void setHospitalizedCount(int hospitalizedCount) {
		this.hospitalizedCount = hospitalizedCount;
	}
	
	
	
}
