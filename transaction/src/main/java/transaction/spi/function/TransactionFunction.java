package transaction.spi.function;

import transaction.exception.TransactionPrepareException;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionFunction<T extends Serializable> extends Serializable{
    public T accept(Map<String, Object> t) throws TransactionPrepareException;
}
