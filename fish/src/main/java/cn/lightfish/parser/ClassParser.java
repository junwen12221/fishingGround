package cn.lightfish.parser;

import cn.lightfish.ast.*;
import cn.lightfish.core.Parser;
import cn.lightfish.core.Token;

import static cn.lightfish.core.Parser.rule;

public class ClassParser extends ArrayParser {
    Parser member = Parser.rule().or(def, simple);
    Parser class_body = Parser.rule(ClassBody.class).sep("{").option(member)
                            .repeat(Parser.rule().sep(";", Token.EOL).option(member))
                            .sep("}");
    Parser defclass = Parser.rule(ClassStmnt.class).sep("class").identifier(reserved)
                          .option(Parser.rule().sep("extends").identifier(reserved))
                          .ast(class_body);

    Parser elements = rule(ArrayLiteral.class)
            .ast(expr).repeat(rule().sep(",").ast(expr));

    public ClassParser() {
        postfix.insertChoice(Parser.rule(Dot.class).sep(".").identifier(reserved));
        program.insertChoice(defclass);

        reserved.add("]");
        primary.insertChoice(rule().sep("[").maybe(elements).sep("]"));
        postfix.insertChoice(rule(ArrayRef.class).sep("[").ast(expr).sep("]"));
    }
}
