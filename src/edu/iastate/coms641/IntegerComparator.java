package edu.iastate.coms641;

import java.util.Comparator;
import java.util.Map;

public class IntegerComparator implements Comparator<String> {
	private Map<String, Integer> base;
	
	public IntegerComparator(Map<String, Integer> base) {
		this.base = base;
	}
	
	@Override
	public int compare(String o1, String o2) {
		if (base.get(o1) >= base.get(o2)) {
			return -1;
		}
		
		return 1;
	}

}
