package cn.lightfish.exception;
import cn.lightfish.core.ASTree;

public class FishException extends RuntimeException {
    public FishException(String m) { super(m); }
    public FishException(String m, ASTree t) {
        super(m + " " + t.location());
    }
}
