package edu.iastate.coms641;

import soot.PackManager;
import soot.Transform;
import soot.options.Options;

public class Tutorial1 {

	public static void main(String[] args) {
		StaticProfiler staticProfiler = new StaticProfiler();
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.staticprof", staticProfiler));
		Options.v().set_output_format(Options.output_format_none);
		soot.Main.main(args);
		
		Utils.reportCounter(staticProfiler.getMethodToCounter());
		System.out.println("Total number of classes: " + staticProfiler.getNoClasses());
		System.out.println("Total number of conditions: " + staticProfiler.getNoConditions());
	}
}
