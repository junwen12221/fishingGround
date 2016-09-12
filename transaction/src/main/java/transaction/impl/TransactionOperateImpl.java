package transaction.impl;

import transaction.exception.TransactionCancelException;
import transaction.exception.TransactionPrepareException;
import transaction.exception.TransactionSubmitException;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;
import transaction.spi.entries.*;
import transaction.spi.function.TransactionCancel;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public class TransactionOperateImpl implements TransactionOperate {
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
            result = null;
            throw new TransactionSubmitException();
        }
        return result;
    }

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
            result = null;
            e.printStackTrace();
            try {
                transaction.getCancel().accept(map);
            } catch (Exception cancelException) {
                throw new TransactionCancelException();
            }
        }
        return result;
    }

    @Override
    public Object visit(OnePCTransaction transaction, Map map) {
        Object result = null;
        for (TransactionFunction it : transaction.getPrepare()) {
            result = it.accept(map);
        }
        int i = 0;
        for (TransactionSubmit it : transaction.getSubmit()) {
            try {
                it.accept(map);
                ++i;
            } catch (Exception e) {
                result = null;
                e.printStackTrace();
                break;
            }
        }
        if (transaction.getSubmit().size() == i) {
            return result;
        }
        List<TransactionCancel> cancelList = transaction.getCancel();
        for (int j = i; j > -1; --j) {
            try {
                cancelList.get(j).accept(map);
            } catch (Exception e) {
                throw new TransactionCancelException();
            }
        }
        return result;
    }

    @Override
    public Object visit(MessageTransaction transaction, Map map) {
        return visit((Transaction) transaction, map);
    }
    @Override
    public Object visit(BEDTransaction transaction, Map map) {
        return visit((Transaction) transaction, map);
    }


    @Override
    public Object visit(TccTransactionList tccTransactionList, Map map) {
        Deque<TccTransaction> stack = (Deque<TccTransaction>) map.get(TransactionComposite.TX_STACK);
        if (stack == null) {
            stack = new ArrayDeque<>();
            map.put(TransactionComposite.TX_STACK, stack);
        }
        Object result = null;
        for (Object it : tccTransactionList) {
            if (it instanceof TccTransaction) {
                TccTransaction action = (TccTransaction) it;
                result = visit(action, map);
                if (result == null) {
                    break;
                }
                stack.push(action);
            } else {
                TccTransactionList action = (TccTransactionList) it;
                result = visit(action, map);
            }
        }
        return result;
    }
}
