package cn.lightfish.ast;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

public class ArrayLiteral extends ASTList {
    public ArrayLiteral(List<ASTree> list) { super(list); }
    public int size() { return numChildren(); }
}
