package cn.lightfish.ast;


import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.runtime.EnvEx;
import cn.lightfish.runtime.NestedEnv;

import java.util.List;

public class BlockStmnt extends ASTList {
    protected EnvEx env;
    public BlockStmnt(List<ASTree> c) { super(c); }
    public EnvEx makeEnv() { return new NestedEnv(env); }
}
