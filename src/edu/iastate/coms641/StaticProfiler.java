package edu.iastate.coms641;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.SwitchStmt;
import soot.options.Options;
import soot.util.Chain;

public class StaticProfiler extends SceneTransformer {
	private Map<String, Integer> methodToCounter;
	private int noClasses;
	private int noConditions;
	

	public StaticProfiler() {
		methodToCounter = new HashMap<String, Integer>();
		noClasses = 0;
		noConditions = 0;
	}

	@Override
	protected void internalTransform(String phaseName,
			Map<String, String> options) {
		// if verbose show the analysis we are performing
		if (Options.v().verbose()) {
			G.v().out.println("StaticProfiler transforming phase " + phaseName);
		}

		// Scene manages the classes of the application under analysis
		// getApplicationClasses return the classes defined in the application
		// under analysis
		Iterator<SootClass> clazzes = Scene.v().
										getApplicationClasses().iterator();
		
		// iterate through each class defined in the application
		while (clazzes.hasNext()) {
			// SootClass is a Soot representation of a class 
			// it contains methods, fields, static fields
			SootClass clazz = clazzes.next();
			noClasses += 1;
			
			// get all the methods defined in the class
			Iterator<SootMethod> methods = clazz.getMethods().iterator();
			while (methods.hasNext()) {
				analyzeMethod(methods.next());
			}
		}
	}
	
	/**
	 * This method should be called after Soot finishes. It returns the 
	 * maps from method signature to the number of calls
	 */
	public Map<String, Integer> getMethodToCounter() {
		return methodToCounter;
	}	
	
	/**
	 * Return the number of classes in the application
	 */
	public int getNoClasses() {
		return noClasses;
	}

	/**
	 * Return the number of conditions in the applications
	 */
	public int getNoConditions() {
		return noConditions;
	}

	/**
	 *  SootMethod is a Soot representation of a method in Java it contains all 
	 *  the information about the method
	 */
	private void analyzeMethod(SootMethod method) {
		// check if it is not an abstract method
		if (!method.isConcrete()) {	
			return;
		}
		
		try {
			// get the body of the method and generate CFG
			Body body = method.retrieveActiveBody();
			
			// Chain objects are kind of lists with O(1) insertion and removal
			Chain<Unit> units = body.getUnits();
			// we can iterate chains as normal collections
			for (Unit unit : units) {
				// each unit represents an statement in the code
				// remember we are using Jimple code not Java code
				Stmt stmt = (Stmt) unit;
				// check is the statement has an invocation
				updateInvocation(stmt);
				//check for conditions
				if (isCondition(stmt)) {
					noConditions += 1;
				}
			}
		} catch (Exception e) {
			G.v().out.println("An error has occurred analyzing method " + 
								method.getName());
		}
	}

	private boolean isCondition(Stmt stmt) {
		return  stmt instanceof IfStmt || stmt instanceof SwitchStmt;
	}

	private void updateInvocation(Stmt stmt) {
		if (stmt.containsInvokeExpr()) {
			InvokeExpr expr = stmt.getInvokeExpr();
			SootMethod methodInvoked = expr.getMethod();
			// a signature is the combination of class name 
			// and method name
			String sig = methodInvoked.getDeclaringClass().getName() +
						"." + methodInvoked.getName(); 
			
			updateSigCounter(sig);
		}
	}

	private void updateSigCounter(String sig) {
		//update counter using the signature as key
		if (methodToCounter.containsKey(sig)) {
			methodToCounter.put(sig, methodToCounter.get(sig) + 1);
		} else {
			methodToCounter.put(sig, 1);
		}
	}

}
