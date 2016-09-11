package transaction.spi.entries;

import transaction.spi.TransactionComposite;
import transaction.spi.TransactionOperate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public class TransactionList extends TransactionComposite {
    protected List<TransactionComposite> children;
    TransactionOperate operate;
    public TransactionList(TransactionOperate operate, TransactionComposite... list) {
        this.operate=operate;this.children = Arrays.asList(list);
    }

    @Override
    public int count() {
        return children.size();
    }

    @Override
    public Iterator<TransactionComposite> iterator() {
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
}
