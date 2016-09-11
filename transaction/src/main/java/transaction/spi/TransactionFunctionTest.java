package transaction.spi;

import transaction.exception.TransactionException;

import java.io.Serializable;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionFunctionTest<T extends Serializable> extends Serializable {
    public T accept() throws TransactionException;
}
