package cn.lightfish.ast;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

public class doWhileStmnt extends ASTList {
    public doWhileStmnt(List<ASTree> c) { super(c); }
    public ASTree body() { return child(0); }
    public ASTree condition() { return child(1); }
    public String toString() {
        return "(doWhile {"  + body() +"} "+ condition() +")";
    }
}
