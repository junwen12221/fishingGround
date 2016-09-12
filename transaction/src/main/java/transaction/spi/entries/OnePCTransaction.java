package transaction.spi.entries;

import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionCancel;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public class OnePCTransaction extends TransactionComposite {

    protected List<TransactionFunction> prepare;
    protected List<TransactionSubmit> submit;
    protected List<TransactionCancel> cancel;
    TransactionOperate operate;
    public OnePCTransaction(TransactionOperate operate){
     this.operate=operate;
    }
/*    List<MessageTransaction> messageTransactionList;*/

    public TransactionOperate getOperate() {
        return operate;
    }

    public List<TransactionFunction> getPrepare() {
        return prepare;
    }

    public List<TransactionSubmit> getSubmit() {
        return submit;
    }

    public List<TransactionCancel> getCancel() {
        return cancel;
    }

/*    public List<MessageTransaction> getMessageTransactionList() {
        return messageTransactionList;
    }*/

    @Override
    public int count() {
        return submit.size();
    }

    @Override
    public Iterator<?> iterator() {
        return null;
    }
    @Override
    public Object apply(Map map) {
        return operate.visit(this,map);
    }
    @Override
    public boolean deploy() {
 /*       messageTransactionList.forEach(it->it.deploy());*/
        return true;
    }
}
