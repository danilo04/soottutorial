package edu.iastate.coms641;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;


public class Tutorial2 {

	public static void main(String[] args) {
		Scene.v().addBasicClass("MethodCounter", SootClass.SIGNATURES);
		MethodInstrumenter instrumenter = new MethodInstrumenter();
		PackManager.v().getPack("jtp").add(
				new Transform("jtp.methodinstrumenter", instrumenter));
		soot.Main.main(args);
		
		System.out.println("Finished code instrumentation");
	}
}
