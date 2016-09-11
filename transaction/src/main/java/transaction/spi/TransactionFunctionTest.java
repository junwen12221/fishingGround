package transaction.spi;

import transaction.exception.TransactionCompensationException;
import transaction.exception.TransactionPrepareException;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionFunction<T> {
      public   T accept(Map<String, Object> t)throws TransactionPrepareException;

}
