import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
		reportCounter(counter);
	}
	
	public static void reportCounter(Map<String, Integer> counter) {
		int maxLength = computeMaxLength(counter);
		IntegerComparator comparator = new IntegerComparator(counter);
		Map<String, Integer> sortedMethodToCounter = new TreeMap<String, Integer>(comparator);
		sortedMethodToCounter.putAll(counter);
  		
		String tableFormat = "| %-" + (maxLength) + "s | %-11d |%n";
		String repeated = String.format(String.format("%%0%dd", maxLength + 2), 0).replace("0","-");
		String spaces = String.format(String.format("%%0%dd", maxLength > 17 ? maxLength - 15 : 0), 0).replace("0"," ");
		System.out.format("+" + repeated + "+-------------+%n");
		System.out.printf("| Method Signature" + spaces + "| # calls     |%n");
		System.out.format("+" + repeated + "+-------------+%n");
		for (Map.Entry<String, Integer> method : 
			sortedMethodToCounter.entrySet()) {
			System.out.format(tableFormat, method.getKey(), method.getValue());
		}
		System.out.format("+" + repeated + "+-------------+%n");
		
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
	
	static class IntegerComparator implements Comparator<String> {
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

}
