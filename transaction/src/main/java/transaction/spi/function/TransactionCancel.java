package transaction.spi.function;

import transaction.exception.TransactionException;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionCancel extends Serializable {
      public   void accept(Map<String, Object> t)throws TransactionException;
}
