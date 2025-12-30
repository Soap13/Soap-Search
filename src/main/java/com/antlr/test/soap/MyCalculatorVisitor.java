package com.antlr.test.soap;

import com.antlr.test2.CalculatorBaseVisitor;
import com.antlr.test2.CalculatorLexer;
import com.antlr.test2.CalculatorParser;
import com.antlr.test2.CalculatorVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * @author Soap
 * @Date 2025/6/8 17:13
 * @Version 1.0
 */
public class MyCalculatorVisitor extends CalculatorBaseVisitor<Object> {
    @Override
    public Object visitParenExpr(CalculatorParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitMultOrDiv(CalculatorParser.MultOrDivContext ctx) {
        Object obj0 = ctx.expr(0).accept(this);
        Object obj1 = ctx.expr(1).accept(this);

        if ("*".equals(ctx.getChild(1).getText())) {
            return (Float) obj0 * (Float) obj1;
        } else if ("/".equals(ctx.getChild(1).getText())) {
            return (Float) obj0 / (Float) obj1;
        }
        return 0f;
    }

    @Override
    public Object visitAddOrSubstract(CalculatorParser.AddOrSubstractContext ctx) {
        Object obj0 = ctx.expr(0).accept(this);
        Object obj1 = ctx.expr(1).accept(this);

        if ("+".equals(ctx.getChild(1).getText())) {
            return (Float) obj0 + (Float) obj1;
        } else if ("-".equals(ctx.getChild(1).getText())) {
            return (Float) obj0 - (Float) obj1;
        }
        return 0f;
    }

    @Override
    public Object visitFloat(CalculatorParser.FloatContext ctx) {
        return Float.parseFloat(ctx.getText());
    }

    public static void main(String[] args) {
        String query = "3.1 * (6.3 - 4.51) + 5 * 4";
        //创建一个词法分析器，用于将输入转换为标记
        CalculatorLexer lexer = new CalculatorLexer(new ANTLRInputStream(query));
        // 创建一个解析器，用于将标记转换为AST
        CalculatorParser parser = new CalculatorParser(new CommonTokenStream(lexer));
        // 创建一个AST遍历器，用于计算表达式的值
        CalculatorVisitor visitor = new MyCalculatorVisitor();
        System.out.println(visitor.visit(parser.expr()));  // 25.549
    }
}