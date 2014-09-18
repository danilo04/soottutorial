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
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.util.Chain;

/**
 * Insert count instructions at the entry point of each method to count the
 * number of calls
 * 
 * @author Danilo Dominguez Perez based on
 *         https://raw.githubusercontent.com/wiki
 *         /Sable/soot/code/profiler/InvokeStaticInstrumenter.java
 */
public class MethodInstrumenter extends BodyTransformer {
	// load our counter class
	static SootClass counterClass;
	static SootMethod increaseCounter, reportCounter;

	@Override
	protected void internalTransform(Body body, String phaseName,
			Map<String, String> options) {
		SootMethod method = body.getMethod();
		if (method.getDeclaringClass().getName().startsWith("MethodCounter")) {
			return;
		}
		
		counterClass = Scene.v().loadClassAndSupport("MethodCounter");
		increaseCounter = counterClass.getMethodByName("increase");
		reportCounter = counterClass.getMethodByName("report");

		String methodName = method.getDeclaringClass().getName() + "."
				+ method.getName();

		Chain<Unit> units = body.getUnits();

		String signature = method.getSubSignature();
		boolean isMain = signature.equals("void main(java.lang.String[])");

		Iterator<Unit> stmtIt = units.snapshotIterator();

		while (stmtIt.hasNext()) {
			Stmt stmt = (Stmt) stmtIt.next();

			if (stmt instanceof InvokeStmt) {
				InvokeExpr iexpr = ((InvokeStmt) stmt).getInvokeExpr();
				if (iexpr instanceof StaticInvokeExpr) {
					SootMethod tempm = ((StaticInvokeExpr) iexpr).getMethod();
					if (tempm.getSignature().equals(
							"<java.lang.System: void exit(int)>")) {
						InvokeExpr reportExpr = Jimple.v().newStaticInvokeExpr(
								reportCounter.makeRef());
						// 2. then, make a invoke statement
						Stmt reportStmt = Jimple.v().newInvokeStmt(reportExpr);
						units.insertBefore(reportStmt, stmt);
					}
				}
			}

			// check if the instruction is a return with/without value
			if ((stmt instanceof ReturnStmt)
					|| (stmt instanceof ReturnVoidStmt)) {
				// 1. make invoke expression of MyCounter.report()
				if (isMain) {
					InvokeExpr reportExpr = Jimple.v().newStaticInvokeExpr(
							reportCounter.makeRef());
					// 2. then, make a invoke statement
					Stmt reportStmt = Jimple.v().newInvokeStmt(reportExpr);

					// 3. insert new statement into the chain
					// (we are mutating the unit chain).
					units.insertBefore(reportStmt, stmt);
				} else {
					InvokeExpr expr = Jimple.v().newStaticInvokeExpr(
							increaseCounter.makeRef(),
							StringConstant.v(methodName));
					Stmt invokeStmt = Jimple.v().newInvokeStmt(expr);
					units.insertBefore(invokeStmt, stmt);
				}

			}
		}

		body.validate();
	}

}
