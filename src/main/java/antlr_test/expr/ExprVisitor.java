package antlr_test.expr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by lanpay on 2017/7/3.
 */
public interface ExprVisitor<T> extends ParseTreeVisitor<T> {

    T visitParens(ExprParser.ParensContext ctx);
    T visitBlank(ExprParser.BlankContext ctx);
    T visitAddSub(ExprParser.AddSubContext ctx);
    T visitMulDiv(ExprParser.MulDivContext ctx);
    T visitId(ExprParser.IdContext ctx);
    T visitInt(ExprParser.IntContext ctx);
    T visitPrintExpr(ExprParser.PrintExprContext ctx);
    T visitAssign(ExprParser.AssignContext ctx);
}
