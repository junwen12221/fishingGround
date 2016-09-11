package cn.lightfish.ast;
import cn.lightfish.core.ASTLeaf;
import cn.lightfish.core.ASTree;
import cn.lightfish.exception.FishException;
import cn.lightfish.runtime.ClassInfo;
import cn.lightfish.runtime.Environment;
import cn.lightfish.runtime.NestedEnv;
import cn.lightfish.runtime.StoneObject;
import cn.lightfish.visitor.LookupVisitor;

import java.lang.reflect.Method;
import java.util.List;

public class Dot extends Postfix {
    public Dot(List<ASTree> c) { super(c); }
    public String name() { return ((ASTLeaf)child(0)).token().getText(); }
    public String toString() { return "." + name(); }
    public  Object eval(LookupVisitor callerEnv , Object value)throws Exception{
        String member = name();
        if (value instanceof ClassInfo) {
            if ("new".equals(member)) {
                ClassInfo ci = (ClassInfo)value;
                NestedEnv e = new LookupVisitor(ci.environment());
                StoneObject so = new StoneObject(e);
                e.putNew("this", so);
                initObject(ci, e);
                return so;
            }
        }
        else if (value instanceof StoneObject) {
            try {
                return ((StoneObject)value).read(member);
            } catch (StoneObject.AccessException e) {}
        }else{
            try {
                return value.getClass().getMethod(member);
            }catch (NoSuchMethodException e){

            }
        }
        throw new FishException("bad member access: " + member, this);
    }
    protected void initObject(ClassInfo ci, Environment env) throws Exception{
        if (ci.superClass() != null)
            initObject(ci.superClass(), env);
        ((ClassBody)ci.body()).accept(env);
    }
    private static Method findMethod(Object visitor, Class<?> type) {
        for (;;) {
            if (type == Object.class) {
                return null;
            } else try {
                return visitor.getClass().getMethod("visit", type);
            } catch (NoSuchMethodException e) {
                type = type.getSuperclass();
            }
        }
    }
}
