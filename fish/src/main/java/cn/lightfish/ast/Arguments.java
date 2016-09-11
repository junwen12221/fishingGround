package cn.lightfish.ast;
import cn.lightfish.core.ASTree;
import cn.lightfish.exception.FishException;
import cn.lightfish.runtime.Function;
import cn.lightfish.runtime.NativeFunction;
import cn.lightfish.runtime.NestedEnv;
import cn.lightfish.visitor.LookupVisitor;

import java.util.List;

public class Arguments extends Postfix {
    public Arguments(List<ASTree> c) { super(c); }
    public int size() { return numChildren(); }
    public Object eval(LookupVisitor callerEnv, Object value)throws Exception {
        if (!(value instanceof NativeFunction)){
            if (!(value instanceof Function)){

            }
                throw new FishException("bad function", this);
          //  return  evalFunction(callerEnv,(Function)value);
        }else{
            return evalNativeFunction(callerEnv,(NativeFunction)value);
        }

    }

    public Object evalFunction(LookupVisitor callerEnv, Function value)throws Exception {
        Function func = (Function)value;
        ParameterList params = func.parameters();
        if (size() != params.size())
            throw new FishException("bad number of arguments", this);
        NestedEnv newEnv = func.makeEnv();
        int num = 0;
        for (ASTree a: this)
        {
            params.eval(newEnv, num++, a.accept(callerEnv));
        }
        return func.body().accept(newEnv);
    }
    public Object evalNativeFunction(LookupVisitor callerEnv, NativeFunction value)throws Exception {
        NativeFunction func = (NativeFunction)value;
        int nparams = func.numOfParameters();
        if (size() != nparams)
            throw new FishException("bad number of arguments", this);
        Object[] args = new Object[nparams];
        int num = 0;
        for (ASTree a: this) {
            args[num++] = a.accept(callerEnv);
        }
        return func.invoke(args, this);
    }

}
