package transaction.spi;

import transaction.spi.entries.*;

import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public interface TransactionOperate {
    public Object visit(Transaction transaction,Map map);
    public Object visit(TccTransaction transaction,Map map);
    public Object visit(OnePCTransaction transaction,Map map);
    public Object visit(MessageTransaction transaction,Map map);
    public Object visit(BEDTransaction transaction,Map map);
    public Object visit(TransactionList transaction,Map map);

}
