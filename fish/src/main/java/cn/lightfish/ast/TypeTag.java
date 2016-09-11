package cn.lightfish.ast;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

public class TypeTag extends ASTList {
    public static final String UNDEF = "<Undef>";
    public TypeTag(List<ASTree> c) { super(c); }
    public String type() {
        if (numChildren() > 0)
            return ((ASTLeaf)child(0)).token().getText();
        else
            return UNDEF;
    }
    public String toString() { return ":" + type(); }
}
