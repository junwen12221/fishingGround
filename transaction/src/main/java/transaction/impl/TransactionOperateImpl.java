package transaction.impl;

import transaction.exception.TransactionCancelException;
import transaction.exception.TransactionPrepareException;
import transaction.exception.TransactionSubmitException;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;
import transaction.spi.entries.*;

import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public class TransactionOperateImpl implements TransactionOperate {
    String type_Transaction = Transaction.class.getSimpleName();

    @Override
    public Object visit(Transaction transaction, Map map) {
        Object result = null;
        try {
            result = transaction.getPrepare().accept(map);
        } catch (Exception e) {
            throw new TransactionPrepareException();
        }
        try {
            transaction.getSubmit().accept(map);
        } catch (Exception e) {
            throw new TransactionSubmitException();
        }
        return result;
    }

    static final String type_TccTransaction = TccTransaction.class.getSimpleName();

    @Override
    public Object visit(TccTransaction transaction, Map map) {
        Object result = null;
        try {
            result = transaction.getPrepare().accept(map);
        } catch (Exception e) {
            throw new TransactionPrepareException();
        }
        try {
            transaction.getSubmit().accept(map);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transaction.getCancel().accept(map);
            } catch (Exception cancelException) {
                throw new TransactionCancelException();
            }
        }
        return result;
    }

    String type_OnePCTransaction = OnePCTransaction.class.getSimpleName();

    @Override
    public Object visit(OnePCTransaction transaction, Map map) {
        for(Transaction it:transaction)
        return null;
    }

    String type_MessageTransaction = MessageTransaction.class.getSimpleName();

    @Override
    public Object visit(MessageTransaction transaction, Map map) {
        return visit((Transaction) transaction, map);
    }

    String type_BEDTransaction = BEDTransaction.class.getSimpleName();

    @Override
    public Object visit(BEDTransaction transaction, Map map) {
        return visit((Transaction) transaction, map);
    }

    String type_TransactionList = TransactionList.class.getSimpleName();

    @Override
    public Object visit(TransactionList transactionList, Map map) {
        Object result = null;
        int i = 0;
        int size = transactionList.count();
        for (Object it : transactionList) {
            String type = it.getClass().getSimpleName();
            switch (type) {
                case "Transaction": {
                    Transaction action = (Transaction) it;
                    try {
                        action.getPrepare().accept(map);
                    }catch (Exception e){
                        throw new TransactionPrepareException();
                    }
                    break;
                }
                case "TccTransaction": {
                    TccTransaction action = (TccTransaction) it;
                    try {
                        action.getPrepare().accept(map);
                    }catch (Exception e){
                        throw new TransactionPrepareException();
                    }
                    break;
                }
                case "OnePCTransaction": {
                    OnePCTransaction action = (OnePCTransaction) it;
                    try {

                    }catch (Exception e){
                        throw new TransactionPrepareException();
                    }
                    break;
                }
                case "MessageTransaction": {
                    Transaction action = (Transaction) it;
                    break;
                }
                case "BEDTransaction": {
                    BEDTransaction action = (BEDTransaction) it;
                    break;
                }
                case "TransactionList": {
                    TransactionList action = (TransactionList) it;
                    break;
                }
                default: {
                    break;
                }
            }

            ++i;
        }
        return result;
    }
}
