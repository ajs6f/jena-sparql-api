package org.aksw.jena_sparql_api.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprTransform;
import org.apache.jena.sparql.expr.ExprTransformer;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVars;

public class VarExprListUtils {


	public static VarExprList createFromVarMap(Map<Var, Var> varMap) {
		VarExprList result = new VarExprList();
		for(Entry<Var, Var> e : varMap.entrySet()) {
			Var v = e.getKey();
			Var w = e.getValue();

			if(v.equals(w)) {
				result.add(w);
			} else {
				result.add(w, new ExprVar(v));
			}
		}

		return result;
	}


    /**
     * Get the referenced variables
     *
     * @param vel
     * @return
     */
    public static Set<Var> getRefVars(VarExprList vel) {
        Set<Var> result = new HashSet<Var>();

        for(Entry<Var, Expr> entry : vel.getExprs().entrySet()) {
            if(entry.getValue() == null) {
                result.add(entry.getKey());
            } else {
                Set<Var> vs = ExprVars.getVarsMentioned(entry.getValue());
                result.addAll(vs);
            }
        }
        return result;
    }

    private static Expr transform(Expr expr, ExprTransform exprTransform)
    {
        if ( expr == null || exprTransform == null )
            return expr ;
        return ExprTransformer.transform(exprTransform, expr) ;
    }
    // Copied from package org.apache.jena.sparql.algebra.ApplyTransformVisitor;
    public static VarExprList transform(VarExprList varExpr, ExprTransform exprTransform)
    {
        List<Var> vars = varExpr.getVars() ;
        VarExprList varExpr2 = new VarExprList() ;
        boolean changed = false ;
        for ( Var v : vars )
        {
        	Expr newVE = exprTransform.transform(new ExprVar(v));
        	Var newV = newVE == null ? v : ((ExprVar)newVE).asVar();

        	changed = !v.equals(newV);

            Expr e = varExpr.getExpr(v) ;
            Expr e2 =  e ;
            if ( e != null )
                e2 = transform(e, exprTransform) ;
            if ( e2 == null )
                varExpr2.add(newV) ;
            else
                varExpr2.add(newV, e2) ;
            if ( e != e2 )
                changed = true ;
        }
        if ( ! changed )
            return varExpr ;
        return varExpr2 ;
    }

    public static void replace(VarExprList dst, VarExprList src) {
        if(dst != src) {
            dst.clear();
            copy(dst, src);
        }
    }

    public static void copy(VarExprList dst, VarExprList src) {
        for(Var v : src.getVars()) {
            Expr e = src.getExpr(v);
            if(e == null) {
                dst.add(v);
            } else {
                dst.add(v, e);
            }
        }
    }
}
