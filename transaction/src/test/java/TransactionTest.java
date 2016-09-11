import org.apache.commons.collections.MapUtils;
import transaction.exception.TransactionSubmitException;
import transaction.impl.TransactionOperateImpl;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionFactory;
import transaction.spi.TransactionOperate;
import transaction.spi.entries.*;

/**
 * Created by karak on 16-9-10.
 */
public class TransactionTest {
    public static void main(String[] args) {

        TransactionOperate operate = new TransactionOperateImpl();

        TransactionFactory transactionFactory = new TransactionFactory(operate);

        Transaction base = transactionFactory.createTransaction();
        base.setPrepare((map) -> {
            System.out.println("=>base prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>base submit");
        });
        base.deploy();
        base.apply(MapUtils.EMPTY_MAP);

        TccTransaction tcc = transactionFactory.createTccTransaction();
        tcc.setPrepare((map) -> {
            System.out.println("=>tcc prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>tcc submit");
            throw new TransactionSubmitException();
        }).setCancel((map)->{
            System.out.println("=>tcc cancel");
        });
        tcc.deploy();
        tcc.apply(MapUtils.EMPTY_MAP);

        MessageTransaction asyn = transactionFactory.createMessageTransaction();
        asyn.setPrepare((map) -> {
            System.out.println("=>asyn prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>asyn submit");
        }).setCheckLocalListener((id) -> {
            return TransactionComposite.SUBMIT;
        }, 10).setCheckRemoteListener((id) -> {
            return TransactionComposite.SUBMIT;
        }, 10);
        asyn.deploy();
        asyn.apply(MapUtils.EMPTY_MAP);


        BEDTransaction bed = transactionFactory.createBEDTransaction();
        bed.setPrepare((map) -> {
            System.out.println("=>bed prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>bed submit");
        }).setTime(10);
        bed.deploy();
        bed.apply(MapUtils.EMPTY_MAP);

        TransactionList transactionList=transactionFactory.createTransactionList(base,tcc,asyn,bed);
        transactionList.deploy();
        transactionList.apply(MapUtils.EMPTY_MAP);

    }

    ;


}
