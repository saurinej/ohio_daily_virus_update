package com.joey.ohio_daily_virus_update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class County implements Comparable<County>, Serializable {

	private String name;
	private ArrayList<CaseInstance> cases;
	int count;
	int deathCount;
	int hospitalizedCount;
	
	public County(String name) {
		this.name = name;
		this.cases = new ArrayList<>();
	}
	
	public void addCaseInstance(CaseInstance instance) {
		cases.add(instance);
		this.count += instance.getCount();
		this.deathCount += instance.getDeathCount();
		this.hospitalizedCount += instance.getHospitalizedCount();
	}
	
	public static class CountyCaseNumberComparator implements Comparator<County> {
		//sort from counties from largest to smallest relative to the amount of confirmed cases
		@Override
		public int compare(County county1, County county2) {
			int county1Count = county1.getCount();
			int county2Count = county2.getCount();
			if (county1Count > county2Count) {
				return -1;
			} else if (county1Count == county2Count) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	
	@Override
	public int compareTo(County o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		County other = (County) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public ArrayList<CaseInstance> getCases() {
		return cases;
	}

	public int getCount() {
		return count;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int getHospitalizedCount() {
		return hospitalizedCount;
	}

	
	
	
	
	
	
}
