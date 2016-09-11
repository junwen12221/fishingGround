package transaction.spi;

import transaction.spi.entries.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by karak on 16-9-11.
 */
public class TransactionFactory {
    TransactionOperate operate;
    ConcurrentMap<String, Transaction> context = new ConcurrentHashMap<>();

    public TransactionFactory(TransactionOperate operate) {
        this.operate = operate;
    }

    public MessageTransaction createMessageTransaction() {

        return new MessageTransaction(operate);
    }

    public TccTransaction createTccTransaction() {
        return new TccTransaction(operate);
    }

    public Transaction createTransaction() {
        return new Transaction(operate);
    }

    public BEDTransaction createBEDTransaction() {
        return new BEDTransaction(operate);
    }
    public TransactionList createTransactionList(TransactionComposite...list) {
        return new TransactionList(list);
    }
    public OnePCTransaction createOnePCTransaction(TransactionComposite...list) {
        return null;
    }
    public OnePCTransaction createOnePCTransaction(TransactionList...list) {
        return null;
    }
}
