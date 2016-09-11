package cn.lightfish.visitor;

import cn.lightfish.ast.*;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.core.Visitor;
import cn.lightfish.exception.FishException;
import cn.lightfish.runtime.ClassInfo;
import cn.lightfish.runtime.Environment;
import cn.lightfish.runtime.Function;
import cn.lightfish.runtime.NestedEnv;

import static java.lang.System.out;

public class LookupVisitor extends NestedEnv implements Visitor<Object, Object, Object> {
    // Logger logger = Logger.getLogger(Logger4JTest.class);
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    boolean isRoot;

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public void printEnv() {
        out.println(super.values.toString());
    }

    public LookupVisitor(Environment e) {
        super(e);
    }

    public LookupVisitor() {
        super();
    }

    @Override
    public Object visit(Arguments value) throws Exception {
        return value.accept(this);
    }

    @Override
    public Object visit(ArrayLiteral value) throws Exception {
        int s = value.numChildren();
        Object[] res = new Object[s];
        int i = 0;
        for (ASTree t : value)
            res[i++] = t.accept(this);
        return res;
    }

    @Override
    public Object visit(ArrayRef value) throws Exception {

        return null;
    }

    @Override
    public Object visit(ClassBody value) throws Exception {
        for (ASTree t : value) {
            t.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ClassStmnt value) throws Exception {
        ClassInfo ci = new ClassInfo(value, this);
        this.put(value.name(), ci);
        return value.name();
    }

    @Override
    public Object visit(DefStmnt value) throws Exception {
        if (isRoot()) {
            this.putNew(value.name(), new Function(value.parameters(), value.body(), this));
            return value.name();
        } else {
            return new Function(value.parameters(), value.body(), this);
        }
    }

    @Override
    public Object visit(Dot value) throws Exception {
        return null;
    }

    @Override
    public Object visit(ParameterList value) throws Exception {

        return null;
    }

    @Override
    public Object visit(Postfix value) throws Exception {

        return null;
    }


    @Override
    public Object visit(TypeTag value) throws Exception {

        return null;
    }

    @Override
    public Object visit(VarStmnt value) throws Exception {

        return null;
    }

    @Override
    public Object visit(WhileStmnt value) throws Exception {
        Object result = 0;
        for (; ; ) {
            Object c = value.condition().accept(this);
            if (c instanceof Integer && ((Integer) c) == FALSE)
                return result;
            else
                result = value.body().accept(this);
        }

    }

    @Override
    public Object visit(ASTLeaf value) throws Exception {
        throw new FishException("cannot eval: " + toString(), value);
    }

    @Override
    public Object visit(ASTList value) throws Exception {
        throw new FishException("cannot eval: " + toString(), value);
    }

    @Override
    public Object visit(ASTree value) throws Exception {
        throw new FishException("cannot eval: " + toString(), value);
    }

    @Override
    public Object visit(IfStmnt value) throws Exception {
        Object c = value.condition().accept(this);
        if (c instanceof Integer && ((Integer) c).intValue() != FALSE)
            return (value.thenBlock()).accept(this);
        else {
            ASTree b = value.elseBlock();
            if (b == null)
                return 0;
            else
                return b.accept(this);
        }
    }

    @Override
    public Object visit(ForStmnt value) throws Exception {

        ASTree first = value.firstStatementList();
        ASTree then = value.thenStatementList();
        first.accept(this);
        for (; ; ) {
            Object result = 0;
            result = value.condition().accept(this);
            if (result instanceof Integer && ((Integer) result) == FALSE)
                return result;
            else
                value.block().accept(this);


            then.accept(this);

        }
    }

    @Override
    public Object visit(Name name) throws Exception {
        Object value = this.get(name.name());
        if (value == null)
            throw new FishException("undefined name: " + name.name(), name);
        else
            return value;
    }

    @Override
    public Object visit(NegativeExpr value) throws Exception {
        Object v = value.operand().accept(this);
        if (v instanceof Integer)
            return -((Integer) v);
        else
            throw new FishException("bad type for -", value);
    }

    @Override
    public Object visit(NullStmnt value) throws Exception {

        return null;
    }

    @Override
    public Object visit(NumberLiteral value) throws Exception {
        return value.value();
    }

    @Override
    public Object visit(BlockStmnt value) throws Exception {
        Object result = 0;
        for (ASTree t : value) {
            if (!(t instanceof NullStmnt))
                result = t.accept(this);
        }
        return result;
    }

    @Override
    public Object visit(BinaryExpr value) throws Exception {
        return value.eval(this, this);
    }

    @Override
    public Object visit(StringLiteral value) throws Exception {
        return value.value();
    }

    @Override
    public Object visit(PrimaryExpr value) throws Exception {
        return value.evalSubExpr(this, 0);

    }

    public Object visit(PrimaryExprList value) throws Exception {
        Object result = 0;
        for (ASTree it : value) {
            result = it.accept(this);
        }
        return result;
    }

}



