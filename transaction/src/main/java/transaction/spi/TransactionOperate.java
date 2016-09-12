package transaction.spi;

import transaction.spi.entries.*;

import java.util.Map;

/**
 * Created by karak on 16-9-11.
 */
public interface TransactionOperate {
    Object visit(Transaction transaction, Map map);

    Object visit(TccTransaction transaction, Map map);

    Object visit(OnePCTransaction transaction, Map map);

    Object visit(MessageTransaction transaction, Map map);

    Object visit(BEDTransaction transaction, Map map);

    Object visit(TccTransactionList transaction, Map map);

}
