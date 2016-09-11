package cn.lightfish.core;

import java.lang.reflect.Method;
import java.util.Iterator;

public abstract class ASTree implements Iterable<ASTree> {
    public final Object accept(Object visitor) throws Exception {


        Method method = findMethod(visitor, getClass());
        if(method!=null){
            try {
                return method.invoke(visitor, this);
            }catch (Exception e){
                String name=getClass().getName();
                System.out.print("visitor error=>"+name);
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }
    public abstract ASTree child(int i);

    public abstract int numChildren();

    public abstract Iterator<ASTree> children();

    public abstract String location();

    public Iterator<ASTree> iterator() {
        return children();
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
