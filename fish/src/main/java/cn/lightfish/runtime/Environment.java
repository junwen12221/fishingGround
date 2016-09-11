package cn.lightfish.runtime;

public interface Environment {
    void put(String name, Object value);
    Object get(String name);
}
