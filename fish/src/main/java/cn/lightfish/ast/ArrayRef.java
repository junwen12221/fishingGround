package cn.lightfish.ast;
import cn.lightfish.core.ASTree;
import cn.lightfish.exception.FishException;
import cn.lightfish.visitor.LookupVisitor;

import java.util.List;

public class ArrayRef extends Postfix {
    public ArrayRef(List<ASTree> c) { super(c); }
    public ASTree index() { return child(0); }
    public String toString() { return "[" + index() + "]"; }
    public Object eval(LookupVisitor  env, Object value) throws Exception{
        if (value instanceof Object[]) {
            Object index = (index()).accept(env);
            if (index instanceof Integer)
                return ((Object[])value)[(Integer)index];
        }

        throw new FishException("bad array access", this);
    }
}
