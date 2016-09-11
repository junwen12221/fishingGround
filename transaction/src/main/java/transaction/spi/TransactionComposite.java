package transaction.spi;

import transaction.spi.function.TransactionCancel;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by karak on 16-9-9.
 */
public abstract class TransactionComposite<T extends Serializable> implements Serializable, Iterable<TransactionComposite> {
    public static final String UNKNOW = "";
    public static final String PREPARE = "0";
    public static final String SUBMIT = "1";
    public static final String CANCEL = "-1";
    public static final String TX_STATE = "txState";
    public static final String TX_STACK = "txStack";
    public static final String TX_ARGS = "txArgs";
    public static final String TX_RESULT = "txResult";

    static TransactionFunction FUN_PREPARE = (map) -> Void.TYPE;
    static TransactionSubmit FUN_SUBMIT = (map) -> {
    };
    static TransactionCancel FUN_CANCEL = (map) -> {
    };

    public abstract int count();

    public abstract Iterator<TransactionComposite> iterator();

    public abstract Object apply(Map<String, Object> map);

    public abstract boolean deploy();



/*
    public void prepare(Map<String, Object> args) throws TransactionPrepareException {
            T result = null;
            try {
                result = this.prepare.accept(args);
            } catch (Exception e) {
                throw new TransactionPrepareException();
            }
            if (result == Void.TYPE) {
                // args.put(TX_RESULT,Void.TYPE);
            } else {
                args.put(TX_RESULT, result);
            }
    }
    public void submit(Map<String, Object> args,TransactionSubmit callbackFirst,TransactionSubmit callbackSecond) throws TransactionSubmitException {
            try {
                callbackFirst.accept(args);
       *//*         Deque<TransactionComposite<T>> deque=    ((Deque<TransactionComposite<T>>) args.get(TransactionComposite.TX_STACK));
                deque.push(this);*//*
                this.submit.accept(args);
                callbackSecond.accept(args);
            } catch (Exception e) {
                throw new TransactionSubmitException();
            }
    }*/
}
