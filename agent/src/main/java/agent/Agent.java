package agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class Agent {


    private static ClassFileTransformer transformer =new AgentClient("localhost", 8080,"other/Stuff");

    private static Instrumentation instrumentation;

    public static void premain(String options, Instrumentation inst) {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        instrumentation.addTransformer(transformer);
    }

    public static void agentmain(String options, Instrumentation inst) {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        instrumentation.addTransformer(transformer);
    }

    public static void main(String args){
        System.out.println("java -javaagent:agent/target/agent-0.1-SNAPSHOT.jar -jar jar_path");
        System.out.println("java -javaagent:agent/target/agent-0.1-SNAPSHOT.jar class_path");
    }


}

