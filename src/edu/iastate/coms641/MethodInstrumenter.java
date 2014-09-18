package edu.iastate.coms641;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.util.Chain;

/**
 * Insert count instructions at the entry point of each method
 * to count the number of calls
 * 
 * @author Danilo Dominguez Perez
 * based on https://raw.githubusercontent.com/wiki/Sable/soot/code/profiler/InvokeStaticInstrumenter.java
 */
public class MethodInstrumenter extends BodyTransformer {
	// load our counter class
	static SootClass counterClass;
	static SootMethod increaseCounter, reportCounter;
	
	{
		counterClass = Scene.v().loadClassAndSupport("edu.iastate.coms641.MethodCounter");
		increaseCounter = counterClass.getMethodByName("increase");
		reportCounter = counterClass.getMethodByName("report");
	}
	
	@Override
	protected void internalTransform(Body body, String phaseName,
			Map<String, String> options) {
		SootMethod method = body.getMethod();
		String methodName = method.getDeclaringClass().getName() + "." + 
							method.getName();
		
		InvokeExpr expr = Jimple.v().newStaticInvokeExpr(
					increaseCounter.makeRef(), StringConstant.v(methodName));
		Stmt invokeStmt = Jimple.v().newInvokeStmt(expr);
		
		Chain<Unit> units = body.getUnits();
		units.insertBefore(units.getFirst(), invokeStmt);
		
		String signature = method.getSubSignature();
		boolean isMain = signature.equals("void main(java.lang.String[])");

		
		if (isMain) {
			Iterator<Unit> stmtIt = units.snapshotIterator();

			while (stmtIt.hasNext()) {
				Stmt stmt = (Stmt) stmtIt.next();

				// check if the instruction is a return with/without value
				if ((stmt instanceof ReturnStmt)
						|| (stmt instanceof ReturnVoidStmt)) {
					// 1. make invoke expression of MyCounter.report()
					InvokeExpr reportExpr = Jimple.v().newStaticInvokeExpr(
							reportCounter.makeRef());

					// 2. then, make a invoke statement
					Stmt reportStmt = Jimple.v().newInvokeStmt(reportExpr);

					// 3. insert new statement into the chain
					// (we are mutating the unit chain).
					units.insertBefore(reportStmt, stmt);
				}
			}
		}
		
		body.validate();
	}

}
