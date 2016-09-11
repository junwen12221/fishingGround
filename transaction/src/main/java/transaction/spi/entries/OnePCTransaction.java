package transaction.spi.entries;

import transaction.spi.TransactionComposite;
import transaction.spi.TransactionFactory;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by karak on 16-9-11.
 */
public class OnePCTransaction extends TransactionComposite {

    TransactionOperate operate;
    public OnePCTransaction(TransactionOperate operate){
     this.operate=operate;
    }
    protected List<TransactionComposite> prepare;
    protected List<TransactionComposite> submit;
    protected List<TransactionComposite> cancel;
    List<MessageTransaction> messageTransactionList;

    public TransactionOperate getOperate() {
        return operate;
    }

    public List<TransactionComposite> getPrepare() {
        return prepare;
    }

    public List<TransactionComposite> getSubmit() {
        return submit;
    }

    public List<TransactionComposite> getCancel() {
        return cancel;
    }

    public List<MessageTransaction> getMessageTransactionList() {
        return messageTransactionList;
    }

    @Override
    public int count() {
        return submit.size();
    }

    @Override
    public Iterator<TransactionComposite> iterator() {
        return submit.iterator();
    }
}
