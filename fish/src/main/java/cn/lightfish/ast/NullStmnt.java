package cn.lightfish.ast;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.Arrays;
import java.util.List;

public class NullStmnt extends ASTList {
    public NullStmnt(List<ASTree> c) { super(c); }
    public static NullStmnt EMPTY=new NullStmnt(Arrays.asList());
}
