package cn.lightfish.runtime;

import cn.lightfish.ast.BlockStmnt;
import cn.lightfish.ast.ParameterList;
import cn.lightfish.visitor.LookupVisitor;

public class Function {
    protected ParameterList parameters;
    protected BlockStmnt body;
    protected Environment env;
    public Function(ParameterList parameters, BlockStmnt body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }
    public ParameterList parameters() { return parameters; }
    public BlockStmnt body() { return body; }
    public NestedEnv makeEnv() { return new LookupVisitor(env); }
    @Override
    public String toString() { return "<fun:" + hashCode() + ">"; }
}
