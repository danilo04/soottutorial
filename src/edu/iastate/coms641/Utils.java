package edu.iastate.coms641;

import java.util.Map;

import dnl.utils.text.table.TextTable;

public class Utils {
	public static void reportCounter(Map<String, Integer> counter) {
		String[] columns = {
				"Method Signature", "# of calls"
		};
		Object[][] data = new Object[counter.size()][2];
		int c = 0;
		for (Map.Entry<String, Integer> entry : counter.entrySet()) {
			data[c][0] = entry.getKey();
			data[c][1] = entry.getValue();
			c += 1;
		}
		
		TextTable tt = new TextTable(columns, data);
		tt.setAddRowNumbering(true);
		tt.setSort(1);
		tt.printTable();
	}
}
