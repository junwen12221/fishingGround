package transaction.spi;

import transaction.exception.TransactionCompensationException;

/**
 * Created by karak on 16-9-10.
 */
public interface TransactionIdempotent {
      void setSubmit(Runnable submit) throws TransactionCompensationException;

      void setCancel(Runnable cancel) throws TransactionCompensationException;
}
