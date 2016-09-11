package transaction.spi;

import transaction.exception.TransactionCompensationException;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionPrepareHandle {
    void setPrepare() throws TransactionCompensationException;
    default TransactionSubmit setSubmit() throws TransactionCompensationException{
        return new TransactionSubmit(this::setPrepare);
    }

}
