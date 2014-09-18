package edu.iastate.coms641;

import soot.PackManager;
import soot.Transform;


public class Tutorial1 {

	public static void main(String[] args) {
		StaticProfilerMethodCalls staticProfiler = new StaticProfilerMethodCalls();
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.staticprof", staticProfiler));
		soot.Main.main(args);
		
		Utils.reportCounter(staticProfiler.getMethodToCounter());
	}
}
