package com.rishavmngo.lox;

import java.util.List;

import com.rishavmngo.lox.Expr.Assign;
import com.rishavmngo.lox.Expr.Binary;
import com.rishavmngo.lox.Expr.Grouping;
import com.rishavmngo.lox.Expr.Literal;
import com.rishavmngo.lox.Expr.Logical;
import com.rishavmngo.lox.Expr.Unary;
import com.rishavmngo.lox.Expr.Variable;
import com.rishavmngo.lox.Stmt.Block;
import com.rishavmngo.lox.Stmt.Expression;
import com.rishavmngo.lox.Stmt.If;
import com.rishavmngo.lox.Stmt.Print;
import com.rishavmngo.lox.Stmt.Var;
import com.rishavmngo.lox.Stmt.While;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private Environment environment = new Environment();

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  @Override
  public Object visitBinaryExpr(Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left <= (double) right;
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double) left * (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }

        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }
        throw new RuntimeError(expr.operator,
            "Operands must be two numbers or two strings.");
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
    }

    return null;
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double)
      return;

    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null)
      return true;
    if (a == null)
      return false;

    return a.equals(b);
  }

  private String stringify(Object obj) {
    if (obj == null)
      return "nil";
    if (obj instanceof Double) {
      String text = obj.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return obj.toString();
  }

  @Override
  public Object visitGroupingExpr(Grouping expr) {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double) right;
    }

    return null;
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double)
      return;

    throw new RuntimeError(operator, "Operand must be a number");
  }

  private boolean isTruthy(Object object) {
    if (object == null)
      return false;
    if (object instanceof Boolean)
      return (boolean) object;
    return true;
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitPrintStmt(Print stmt) {
    Object value = evaluate(stmt.expression);
    if (stmt.newLine)
      System.out.println(stringify(value));
    else
      System.out.print(stringify(value));
    return null;
  }

  @Override
  public Void visitVarStmt(Var stmt) {
    Object value = null;

    if (stmt.initilizer != null) {
      value = evaluate(stmt.initilizer);
    }
    environment.define(stmt.name.lexeme, value);
    return null;
  }

  @Override
  public Object visitVariableExpr(Variable expr) {
    return environment.get(expr.name);
  }

  @Override
  public Object visitAssignExpr(Assign expr) {
    Object value = evaluate(expr.value);
    environment.assign(expr.name, value);
    return value;
  }

  @Override
  public Void visitBlockStmt(Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }

  private void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;

      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  @Override
  public Void visitIfStmt(If stmt) {
    if (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Object visitLogicalExpr(Logical expr) {
    Object left = evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {
      if (isTruthy(left))
        return left;
    } else {
      if (!isTruthy(left))
        return left;
    }

    return evaluate(expr.right);
  }

  @Override
  public Void visitWhileStmt(While stmt) {
    while (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.body);
    }
    return null;
  }
}
