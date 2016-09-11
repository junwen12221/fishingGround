import transaction.exception.TransactionException;
import transaction.spi.*;
import transaction.spi.entries.BEDTransaction;
import transaction.spi.entries.MessageTransaction;
import transaction.spi.entries.TccTransaction;
import transaction.spi.entries.Transaction;

import java.util.HashMap;
import java.util.function.Supplier;

import static java.lang.System.out;

/**
 * Created by karak on 16-9-10.
 */
public class TransactionTest {
    public static void main(String[] args) {

        TransactionOperate operate = new TransactionOperate();

        TransactionFactory transactionFactory = new TransactionFactory(operate);

        Transaction base = transactionFactory.createTransaction();
        base.setPrepare((map) -> {
            System.out.println("=>base prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>base submit");
        });

        TccTransaction tcc = transactionFactory.createTccTransaction();
        tcc.setPrepare((map) -> {
            System.out.println("=>base prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>base submit");
        });

        MessageTransaction asyn = transactionFactory.createMessageTransaction();
        asyn.setPrepare((map) -> {
            System.out.println("=>base prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>base submit");
        }).setCheckLocalListener((id) -> {
            return TransactionComposite.SUBMIT;
        }, 10).setCheckRemoteListener((id) -> {
            return TransactionComposite.SUBMIT;
        }, 10);


        BEDTransaction bed = transactionFactory.createBEDTransaction();
        bed.setPrepare((map) -> {
            System.out.println("=>base prepare");
            return "ok";//事务的返回值
        }).setSubmit((map) -> {
            System.out.println("=>base submit");
        }).setTime(10);


    }

    ;


}
