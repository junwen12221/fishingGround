package cn.lightfish.ast;

import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.visitor.LookupVisitor;

import java.util.List;

public abstract class Postfix extends ASTList {
    public Postfix(List<ASTree> c) { super(c); }
    public abstract Object eval(LookupVisitor callerEnv , Object value)throws Exception;
}
