package cn.lightfish.runtime;
import java.util.HashMap;

public class BasicEnv implements Environment {
    protected HashMap<String,Object> values;
    public BasicEnv() { values = new HashMap<>(); }
    public void put(String name, Object value) { values.put(name, value); }
    public Object get(String name) { return values.get(name); }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BasicEnv{");
        sb.append("values=").append(values);
        sb.append('}');
        return sb.toString();
    }
}
