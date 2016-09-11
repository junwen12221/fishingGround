package cn.lightfish.parser;

import cn.lightfish.ast.TypeTag;
import cn.lightfish.ast.VarStmnt;
import cn.lightfish.core.Parser;

public class TypedParser extends ClassParser {
    Parser typeTag = Parser.rule(TypeTag.class).sep(":").identifier(reserved);
    Parser variable = Parser.rule(VarStmnt.class)
                          .sep("var").identifier(reserved).maybe(typeTag)
                          .sep("=").ast(expr);
    public TypedParser() {
        reserved.add(":");
        param.maybe(typeTag);
        def.reset().sep("def").identifier(reserved).ast(paramList)
                   .maybe(typeTag).ast(block);
        statement.insertChoice(variable);
    }
}
