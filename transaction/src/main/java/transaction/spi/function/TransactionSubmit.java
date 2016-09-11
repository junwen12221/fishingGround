package transaction.spi.function;

import transaction.exception.TransactionCompensationException;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionSubmit extends Serializable{
    void accept(Map<String,Object> t)throws TransactionCompensationException;
}
