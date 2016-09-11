package cn.lightfish.exception;


import cn.lightfish.core.ASTree;

public class TypeException extends Exception {
    public TypeException(String msg, ASTree t) {
        super(msg + " " + t.location()); 
    }
}
