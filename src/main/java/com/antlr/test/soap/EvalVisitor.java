package com.antlr.test.soap;

import com.antlr.test.ArithmeticBaseVisitor;
import com.antlr.test.ArithmeticLexer;
import com.antlr.test.ArithmeticParser;
import com.antlr.test.ArithmeticVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Soap
 * @Date 2025/6/8 15:40
 * @Version 1.0
 */
public class EvalVisitor  extends ArithmeticBaseVisitor<Double> {
    // 使用一个Map来存储变量名和值的映射关系
    Map<String, Double> memory = new HashMap<String, Double>();


    // 重写visitExpr方法，用于计算加法和减法
    @Override
    public Double visitExpr(ArithmeticParser.ExprContext ctx) {
        Double result = visit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Double term = visit(ctx.term(i));
            if (op.equals("+")) {
                result += term;
            } else {
                result -= term;
            }
        }
        return result;
    }

    // 重写visitTerm方法，用于计算乘法和除法
    @Override
    public Double visitTerm(ArithmeticParser.TermContext ctx) {
        Double result = visit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Double factor = visit(ctx.factor(i));
            if (op.equals("*")) {
                result *= factor;
            } else {
                result /= factor;
            }
        }
        return result;
    }

    // 重写visitFactor方法，用于计算数字和括号内的表达式
    @Override
    public Double visitFactor(ArithmeticParser.FactorContext ctx) {
        if (ctx.NUMBER() != null) {
            // 如果是一个数字，直接返回其值
            return Double.parseDouble(ctx.NUMBER().getText());
        } else {
            // 如果是括号内的表达式，递归调用visit方法
            return visit(ctx.expr());
        }
    }

    public static void main(String[] args) throws Exception {
        String input = "(1+2*3)-4";
        // 创建一个词法分析器，用于将输入转换为标记
        ArithmeticLexer lexer = new ArithmeticLexer(new ANTLRInputStream(input));

        // 创建一个标记流，用于将标记传递给解析
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 创建一个解析器，用于将标记转换为AST
        ArithmeticParser parser = new ArithmeticParser(tokens);

        // 调用解析器的parse方法，生成AST
        // 实际要掉用expr方法
        ParseTree tree = parser.expr();

        // 创建一个AST遍历器，用于计算表达式的值
        EvalVisitor eval = new EvalVisitor();

        // 遍历AST，并计算表达式的值
        double result = eval.visit(tree);

        // 打印计算结果
        System.out.println(result);

//        ArithmeticLexer lexer = new ArithmeticLexer(new ANTLRInputStream(input));
//        ArithmeticParser parser = new ArithmeticParser(new CommonTokenStream(lexer));
//        EvalVisitor visitor = new EvalVisitor();
//
//        System.out.println(visitor.visit(parser.expr()));
    }
}


