package transaction.spi;

import transaction.exception.TransactionCompensationException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by karak on 16-9-11.
 */
public class TransactionList extends TransactionComposite {
    protected List<TransactionComposite> children;
    TransactionList(TransactionComposite... list) {
        this.children = Arrays.asList(list);
    }

    @Override
    public int count() {
        return children.size();
    }

    @Override
    public Iterator<TransactionComposite> iterator() {
        return children.iterator();
    }
}
