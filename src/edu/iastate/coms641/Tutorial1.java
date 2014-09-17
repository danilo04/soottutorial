package edu.iastate.coms641;

import java.util.Map;
import java.util.TreeMap;

import soot.PackManager;
import soot.Transform;


public class Tutorial1 {

	public static void main(String[] args) {
		StaticProfiler staticProfiler = new StaticProfiler();
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.staticprof", staticProfiler));
		soot.Main.main(args);
		
		int maxLength = 0;
		for (String sig : staticProfiler.getMethodToCounter().keySet()) {
			if (sig.length() > maxLength) {
				maxLength = sig.length();
			}
		}
		
		//sorted
		IntegerComparator comparator = new IntegerComparator(staticProfiler.getMethodToCounter());
		Map<String, Integer> sortedMethodToCounter = new TreeMap<String, Integer>(comparator);
		sortedMethodToCounter.putAll(staticProfiler.getMethodToCounter());
  		
		String tableFormat = "| %-" + maxLength + "s | %-6d |%n";
		System.out.format("+-------------------------------------------+--------+%n");
		System.out.printf("| Method Signature     | # calls   |%n");
		System.out.format("+-------------------------------------------+--------+%n");
		for (Map.Entry<String, Integer> method : 
			sortedMethodToCounter.entrySet()) {
			System.out.format(tableFormat, method.getKey(), method.getValue());
		}
		
		System.out.println("Total number of methods: " + staticProfiler.getMethodToCounter().size());
	}
}
