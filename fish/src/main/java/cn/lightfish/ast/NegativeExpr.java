package cn.lightfish.ast;

import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

public class NegativeExpr extends ASTList {
    public NegativeExpr(List<ASTree> c) { super(c); }
    public ASTree operand() { return child(0); }
    public String toString() {
        return "-" + operand();
    }
}
