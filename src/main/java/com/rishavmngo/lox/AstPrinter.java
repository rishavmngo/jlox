package com.rishavmngo.lox;

import com.rishavmngo.lox.Expr.Assign;
import com.rishavmngo.lox.Expr.Binary;
import com.rishavmngo.lox.Expr.Call;
import com.rishavmngo.lox.Expr.Grouping;
import com.rishavmngo.lox.Expr.Literal;
import com.rishavmngo.lox.Expr.Logical;
import com.rishavmngo.lox.Expr.Unary;
import com.rishavmngo.lox.Expr.Variable;

public class AstPrinter implements Expr.Visitor<String> {

  // public static void main(String[] args) {
  // Expr expression = new Expr.Binary(
  // new Expr.Unary(
  // new Token(TokenType.MINUS, "-", null, 1),
  // new Expr.Literal(123)),
  // new Token(TokenType.STAR, "*", null, 1),
  // new Expr.Grouping(
  // new Expr.Literal(45.67)));
  //
  // System.out.println(new AstPrinter().print(expression));
  // }

  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Binary expr) {
    return parenthesize(expr.operator.lexeme,
        expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Literal expr) {
    if (expr.value == null)
      return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();
    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  @Override
  public String visitVariableExpr(Variable expr) {
    throw new UnsupportedOperationException("Unimplemented method 'visitVariableExpr'");
  }

  @Override
  public String visitAssignExpr(Assign expr) {
    throw new UnsupportedOperationException("Unimplemented method 'visitAssignExpr'");
  }

  @Override
  public String visitLogicalExpr(Logical expr) {
    throw new UnsupportedOperationException("Unimplemented method 'visitLogicalExpr'");
  }

  @Override
  public String visitCallExpr(Call expr) {
    throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
  }

}
