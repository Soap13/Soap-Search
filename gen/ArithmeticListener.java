// Generated from D:/BaiduNetdiskDownload/学习笔记/java/工具算法DEMO/Soap-Search/src/main/resources/Arithmetic.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArithmeticParser}.
 */
public interface ArithmeticListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(ArithmeticParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(ArithmeticParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ArithmeticParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ArithmeticParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(ArithmeticParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(ArithmeticParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(ArithmeticParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(ArithmeticParser.FactorContext ctx);
}