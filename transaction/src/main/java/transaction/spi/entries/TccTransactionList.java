package transaction.spi.entries;

import transaction.exception.TransactionCompensationException;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;

import java.util.*;

/**
 * Created by karak on 16-9-11.
 */
public class TccTransactionList extends TransactionComposite {
    protected List<TccTransaction> children;
    TransactionOperate operate;

    public TccTransactionList(TransactionOperate operate, TccTransaction... list) {
        this.operate=operate;this.children = Arrays.asList(list);
    }
    @Override
    public int count() {
        return children.size();
    }

    @Override
    public Iterator<TccTransaction> iterator() {
        return children.iterator();
    }

    @Override
    public boolean deploy() {
        return true;
    }

    @Override
    public Object apply(Map map) {
        return operate.visit(this,map);
    }

    public void rollback(Map map) throws TransactionCompensationException {
        Deque<TccTransaction> stack = (Deque<TccTransaction>) map.get(TransactionComposite.TX_STACK);
        do {
            TccTransaction tccTransaction = stack.poll();
            if (tccTransaction != null) {
                try {
                    tccTransaction.getCancel().accept(map);
                } catch (Exception e) {
                    stack.push(tccTransaction);
                    throw new TransactionCompensationException("");
                }
            } else break;
        } while (true);
        if (stack.size() == 0) {
            map.remove(TransactionComposite.TX_STACK);
        }
    }

}
