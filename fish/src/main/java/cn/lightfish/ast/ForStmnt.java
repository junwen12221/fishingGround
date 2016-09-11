package cn.lightfish.ast;

import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

/**
 * Created by karak on 16-8-28.
 */
public class ForStmnt extends ASTList {
    public ForStmnt(List<ASTree> c) {
        super(c);
    }
    public ASTree condition() { return child(1); }
    public ASTree firstStatementList() { return child(0); }
    public ASTree thenStatementList() {
        return child(2);
    }
    public ASTree block() {
        return child(3);
    }
    public String toString() {
        return "(for " +firstStatementList() +";"+condition() + ";" + thenStatementList()+"{"+block()+"}"+")" ;
    }
}
