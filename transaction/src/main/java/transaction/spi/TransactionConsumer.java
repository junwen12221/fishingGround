package transaction.spi;

import transaction.exception.TransactionCompensationException;

import java.util.Map;

/**
 * Created by karak on 16-9-10.
 */
@FunctionalInterface
public interface TransactionInterface {
      public   void accept(Map<String,Object> t)throws TransactionCompensationException;
}
