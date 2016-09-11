package cn.lightfish.ast;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.Token;

public class NumberLiteral extends ASTLeaf {
    public NumberLiteral(Token t) { super(t); }
    public int value() { return token().getNumber(); }
}
