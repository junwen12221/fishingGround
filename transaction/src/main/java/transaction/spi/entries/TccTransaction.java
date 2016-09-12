package transaction.spi.entries;

import transaction.exception.TransactionCompensationException;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionCancel;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public class TccTransaction extends Transaction {

    TransactionCancel cancel;

    public TccTransaction(TransactionOperate factory) {
        super(factory);
    }

    @Override
    public int count() {
        return super.count();
    }

    public TccTransaction setPrepare(TransactionFunction prepare) throws TransactionCompensationException {
        this.prepare = prepare;
        return this;
    }

    public TccTransaction setSubmit(TransactionSubmit submit) throws TransactionCompensationException {
        this.submit = submit;
        return this;
    }

    @Override
    public Object apply(Map map) {
        return operate.visit(this,map);
    }

    @Override
    public boolean deploy() {
        return true;
    }

    public TransactionCancel getCancel() {
        return cancel;
    }

    public TccTransaction setCancel(TransactionCancel cancel) throws TransactionCompensationException {
        this.cancel = cancel;
        return this;
    }
}
