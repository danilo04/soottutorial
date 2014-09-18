package edu.iastate.coms641;

import java.util.HashMap;
import java.util.Map;

public class MethodCounter {
	static Map<String, Integer> counter = new HashMap<String, Integer>();
	
	public static void increase(String method) {
		if (counter.containsKey(method)) {
			counter.put(method, counter.get(method) + 1);
		} else {
			counter.put(method, 1);
		}
	}
	
	public static void report() {
		Utils.reportCounter(counter);
	}
}
