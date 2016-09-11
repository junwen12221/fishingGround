package cn.lightfish.ast;

import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.runtime.Environment;
import cn.lightfish.runtime.NestedEnv;

import java.util.List;

public class DefStmnt extends ASTList {
    public DefStmnt(List<ASTree> c) {
        super(c);
    }

    public String name() {
        return ((ASTLeaf) child(0)).token().getText();
    }

    public ParameterList parameters() {
        return (ParameterList) child(1);
    }

    public TypeTag typeTag() {
        return (TypeTag) child(2);
    }

    public BlockStmnt body() {
        return (BlockStmnt) child(3);
    }

    public String toString() {
        return "(def " + name() + " " + parameters() + " " +typeTag()+""+ body() + ")";
    }
    public NestedEnv makeEnv(Environment env) { return new NestedEnv(env); }

}
