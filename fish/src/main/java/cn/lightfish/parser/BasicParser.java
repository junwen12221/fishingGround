package cn.lightfish.parser;

import cn.lightfish.ast.*;
import cn.lightfish.core.ASTree;
import cn.lightfish.core.Lexer;
import cn.lightfish.core.Parser;
import cn.lightfish.core.Token;
import cn.lightfish.exception.ParseException;

import java.util.HashSet;

public class BasicParser {
    HashSet<String> reserved = new HashSet<>();

    Parser.Operators operators = new Parser.Operators();
    Parser expr0 = Parser.rule();
    Parser constValue = Parser.rule().or(
            Parser.rule().number(NumberLiteral.class),
            Parser.rule().identifier(Name.class, reserved),
            Parser.rule().string(StringLiteral.class));
    Parser primary = Parser.rule(PrimaryExpr.class).or(Parser.rule().sep("(").ast(expr0).sep(")"), constValue);
    Parser factor = Parser.rule().or(Parser.rule(NegativeExpr.class).sep("-").ast(primary), primary);
    Parser expr = expr0.expression(BinaryExpr.class, factor, operators);
    Parser statement0 = Parser.rule();
    Parser block = Parser.rule(BlockStmnt.class).sep("{").option(statement0).repeat(Parser.rule().sep(";").option(statement0)).sep("}");
    Parser simple = Parser.rule(PrimaryExpr.class).ast(expr);
    Parser ifStatement = Parser.rule(IfStmnt.class).sep("if").ast(expr).ast(block);
    Parser ifElseStatement = Parser.rule().ifElse(ifStatement, block, "if", "else");
    Parser whileStatement = Parser.rule(WhileStmnt.class).sep("while").ast(expr).ast(block);
    Parser doWhileStatement = Parser.rule(doWhileStmnt.class).sep("do").ast(block).sep("while").ast(expr);
    Parser caseParser = Parser.rule(Switch.Case.class).sep("case").or(constValue).sep(":").ast(block);
    Parser switchStatement = Parser.rule(Switch.class).sep("switch").or(constValue).sep("{").repeat(caseParser).sep("}");
    Parser simpleList = Parser.rule(PrimaryExprList.class).option(expr).option(Parser.rule().sep(",").ast(expr));
    Parser forStatement = Parser.rule(ForStmnt.class).sep("for").sep("(").ast(simpleList).sep(";").ast(expr).sep(";").ast(simpleList).sep(")").ast(block);
    Parser statement = statement0.or(
            doWhileStatement,
            ifElseStatement,
            whileStatement,
            switchStatement,
            forStatement,
            simple,
            Parser.rule().sep("break"),
            Parser.rule().sep("continue")
    );


    Parser program = Parser.rule().or(statement, Parser.rule(NullStmnt.class))
            .sep(";", Token.EOL);


    public BasicParser() {

        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);

        operators.add("=", 1, Parser.Operators.RIGHT);
        operators.add("==", 2, Parser.Operators.LEFT);
        operators.add("!=", 2, Parser.Operators.LEFT);
        operators.add(">", 2, Parser.Operators.LEFT);
        operators.add("<", 2, Parser.Operators.LEFT);
        operators.add("+", 3, Parser.Operators.LEFT);
        operators.add("-", 3, Parser.Operators.LEFT);
        operators.add("*", 4, Parser.Operators.LEFT);
        operators.add("/", 4, Parser.Operators.LEFT);
        operators.add("%", 4, Parser.Operators.LEFT);
    }

    public ASTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }

}


