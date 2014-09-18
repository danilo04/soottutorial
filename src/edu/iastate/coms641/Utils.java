package edu.iastate.coms641;

import java.util.Map;
import java.util.TreeMap;

public class Utils {
	public static void reportCounter(Map<String, Integer> counter) {
		int maxLength = computeMaxLength(counter);
		IntegerComparator comparator = new IntegerComparator(counter);
		Map<String, Integer> sortedMethodToCounter = new TreeMap<String, Integer>(comparator);
		sortedMethodToCounter.putAll(counter);
  		
		String tableFormat = "| %-" + maxLength + "s | %-6d |%n";
		System.out.format("+-------------------------------------------+--------+%n");
		System.out.printf("| Method Signature     | # calls   |%n");
		System.out.format("+-------------------------------------------+--------+%n");
		for (Map.Entry<String, Integer> method : 
			sortedMethodToCounter.entrySet()) {
			System.out.format(tableFormat, method.getKey(), method.getValue());
		}
		
		System.out.println("Total number of methods: " + counter.size());
	}

	private static int computeMaxLength(Map<String, Integer> counter) {
		int maxLength = 0;
		for (String sig : counter.keySet()) {
			if (sig.length() > maxLength) {
				maxLength = sig.length();
			}
		}
		
		return maxLength;
	}
}
