
package cn.lightfish.parser;

import cn.lightfish.ast.ArrayLiteral;
import cn.lightfish.ast.ArrayRef;
import cn.lightfish.core.Parser;

import static cn.lightfish.core.Parser.rule;

public class ArrayParser extends FuncParser {
    Parser elements = rule(ArrayLiteral.class)
                          .ast(expr).repeat(rule().sep(",").ast(expr));
    public ArrayParser() {
        reserved.add("]");
        primary.insertChoice(rule().sep("[").maybe(elements).sep("]"));
        postfix.insertChoice(rule(ArrayRef.class).sep("[").ast(expr).sep("]"));
    }
}
