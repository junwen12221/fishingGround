package cn.lightfish.watch;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;

/**
 * @author chuer
 * @version V1.0
 * @Description: 代理类：这个类需要在mainfest.mf文件中指定
 * @date 2015年5月22日 下午3:30:57
 */
public class AgentMain {

    /**
     * 这个类需要在mainfest.mf文件中指定，而且此方法名以及参数都是固定的，不能随意指定。
     * 当我们告诉JVM需要热加载类的时候，JVM会自动调用此方法。
     *
     * @param agentArgs
     * @param inst
     * @throws ClassNotFoundException
     * @throws UnmodifiableClassException
     * @throws InterruptedException
     */
    public static void agentmain(String agentArgs, Instrumentation inst)
            throws ClassNotFoundException, UnmodifiableClassException,
            InterruptedException {
        System.out.println("Agent Main Start");
        inst.addTransformer(new Transformer(), true);
        ArrayList<Class<?>> classes = new ArrayList<>();

        for (Class<?> c : inst.getAllLoadedClasses()) {
            if (inst.isModifiableClass(c)) {
                classes.add(c);
            }
        }
        inst.retransformClasses(classes.toArray(new Class<?>[classes.size()]));
        System.out.println("Agent Main Done");
    }
}