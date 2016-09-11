package transaction.spi.function;

import java.io.Serializable;

/**
 * Created by karak on 16-9-11.
 */
@FunctionalInterface
public interface TransactionCheckListener <T extends Serializable> extends Serializable{
    String accept(T t);
}
