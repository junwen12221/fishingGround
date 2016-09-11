package cn.lightfish.ast;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.Token;

public class StringLiteral extends ASTLeaf {
    public StringLiteral(Token t) { super(t); }
    public String value() { return token().getText(); }
}
