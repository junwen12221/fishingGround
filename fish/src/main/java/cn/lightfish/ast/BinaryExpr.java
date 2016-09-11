package cn.lightfish.ast;

import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;
import cn.lightfish.exception.FishException;
import cn.lightfish.runtime.Environment;
import cn.lightfish.runtime.StoneObject;
import cn.lightfish.visitor.LookupVisitor;

import java.util.List;

public class BinaryExpr extends ASTList {
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public BinaryExpr(List<ASTree> c) {
        super(c);
    }

    public ASTree left() {
        return child(0);
    }

    public String operator() {
        return ((ASTLeaf) child(1)).token().getText();
    }

    public ASTree right() {
        return child(2);
    }

    public Object eval(Environment env, LookupVisitor visitor)throws Exception {
        try {
            String op = operator();
            if ("=".equals(op)) {
                Object right = right().accept(visitor);
                return computeAssign(visitor, right);
            } else {
                Object left = left().accept(visitor);
                Object right = right().accept(visitor);
                return computeOp(left, op, right);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    protected Object computeAssign(LookupVisitor env, Object rvalue) throws Exception{
        ASTree le = left();
        if (le instanceof PrimaryExpr) {
            PrimaryExpr p = (PrimaryExpr)le;
            if (p.hasPostfix(0) && p.postfix(0) instanceof Dot) {
                Object t = ((PrimaryExpr)le).evalSubExpr(env, 1);
                if (t instanceof StoneObject)
                    return setField((StoneObject)t, (Dot)p.postfix(0),
                            rvalue);
            }
        }
        return computeArrayAssign(env, rvalue);
    }
    protected Object computeArrayAssign(LookupVisitor env, Object rvalue) throws Exception{
        ASTree le = left();
        if (le instanceof PrimaryExpr) {
            PrimaryExpr p =(PrimaryExpr) le;
            if (p.hasPostfix(0) && p.postfix(0) instanceof ArrayRef) {
                Object a = p.evalSubExpr(env, 1);
                if (a instanceof Object[]) {
                    ArrayRef aref = (ArrayRef)p.postfix(0);
                    Object index = aref.index().accept(env);
                    if (index instanceof Integer) {
                        ((Object[])a)[(Integer)index] = rvalue;
                        return rvalue;
                    }
                }
                throw new FishException("bad array access", this);
            }
        }
        return computeNormalAssign(env, rvalue);
    }
    protected Object computeNormalAssign(Environment env, Object rvalue) {
        ASTree l = left();
        if (l instanceof Name) {
            env.put(((Name)l).name(), rvalue);
            return rvalue;
        }
        else
            throw new FishException("bad assignment", this);
    }
    protected Object computeOp(Object left, String op, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return computeNumber((Integer)left, op, (Integer)right);
        }
        else
        if (op.equals("+"))
            return String.valueOf(left) + String.valueOf(right);
        else if (op.equals("==")) {
            if (left == null)
                return right == null ? TRUE : FALSE;
            else
                return left.equals(right) ? TRUE : FALSE;
        }
        else
            throw new FishException("bad type", this);
    }
    protected Object computeNumber(Integer left, String op, Integer right) {
        int a = left.intValue();
        int b = right.intValue();
        if (op.equals("+"))
            return a + b;
        else if (op.equals("-"))
            return a - b;
        else if (op.equals("*"))
            return a * b;
        else if (op.equals("/"))
            return a / b;
        else if (op.equals("%"))
            return a % b;
        else if (op.equals("=="))
            return a == b ? TRUE : FALSE;
        else if (op.equals(">"))
            return a > b ? TRUE : FALSE;
        else if (op.equals("<"))
            return a < b ? TRUE : FALSE;
        else
            throw new FishException("bad operator", this);
    }
    protected Object setField(StoneObject obj, Dot expr, Object rvalue) {
        String name = expr.name();
        try {
            obj.write(name, rvalue);
            return rvalue;
        } catch (StoneObject.AccessException e) {
            throw new FishException("bad member access " + location()
                    + ": " + name);
        }
    }
}
