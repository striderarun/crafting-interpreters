package com.arun.interpreter.lox.ast;

public interface Visitor<R> {
    R visitBinaryExpr(Expr.Binary expr);
    R visitGroupingExpr(Expr.Grouping expr);
    R visitLiteralExpr(Expr.Literal expr);
    R visitUnaryExpr(Expr.Unary expr);
}
