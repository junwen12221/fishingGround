package cn.lightfish.ast;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.runtime.NestedEnv;

import java.util.List;

public class ParameterList extends ASTList {
    public ParameterList(List<ASTree> c) { super(c); }
    public String name(int i) {
        return ((ASTLeaf)child(i).child(0)).token().getText();
    }
    public TypeTag typeTag(int i) {
        return (TypeTag)child(i).child(1);
    }
    public int size() { return numChildren(); }
    public void eval(NestedEnv env, int index, Object value) {
        env.putNew(name(index), value);
    }
}
