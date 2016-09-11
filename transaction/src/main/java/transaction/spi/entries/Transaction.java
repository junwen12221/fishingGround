package transaction.spi.entries;

import org.apache.commons.collections.IteratorUtils;
import transaction.exception.TransactionCompensationException;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */

public class Transaction extends TransactionComposite {
    TransactionFunction prepare;
    TransactionSubmit submit;
    TransactionOperate operate;
    public Transaction(TransactionOperate operate) {
        this.operate=operate;
    }
    public Transaction setPrepare(TransactionFunction prepare) throws TransactionCompensationException {
        this.prepare = prepare;
        return this;
    }

    public Transaction setSubmit(TransactionSubmit submit) throws TransactionCompensationException {
        this.submit = submit;
        return this;
    }
    public MessageTransaction toAsynTransaction(){
        return new MessageTransaction(operate).setPrepare(prepare).setSubmit(submit);
    }
    public TccTransaction toTCCTransaction(){
        return new TccTransaction(operate).setPrepare(prepare).setSubmit(submit);
    }

    public TransactionOperate getOperate() {
        return operate;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Iterator<TransactionComposite> iterator() {
        return IteratorUtils.EMPTY_ITERATOR;
    }

    @Override
    public Object apply(Map map) {
        return operate.visit(this,map);
    }

    @Override
    public boolean deploy() {
        return true;
    }

    public TransactionFunction getPrepare() {
        return prepare;
    }

    public TransactionSubmit getSubmit() {
        return submit;
    }
}
