package transaction.spi.entries;

import transaction.exception.TransactionCompensationException;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionCancel;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.function.Function;

/**
 * Created by karak on 16-9-11.
 */
public class BEDTransaction extends Transaction {
    long cycle;//重试周期

    public BEDTransaction(TransactionOperate factory) {
        super(factory);
    }
    @Override
    public int count() {
        return super.count();
    }


    public BEDTransaction setPrepare(TransactionFunction prepare) throws TransactionCompensationException {
        this.prepare = prepare;
        return this;
    }

    public BEDTransaction setSubmit(TransactionSubmit submit) throws TransactionCompensationException {
        this.submit = submit;
        return this;
    }

    public long getCycle() {
        return cycle;
    }

    public void setTime(long cycle) {
        this.cycle = cycle;
    }
}
