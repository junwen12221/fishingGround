import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.functors.TransformerClosure;
import transaction.spi.TransactionClosureParm;
import transaction.spi.TransactionComposite;
import transaction.spi.TransactionHandle;
import transaction.spi.TransactionMapParm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karak on 16-9-10.
 */
public class TransactionTest {

    public static void main(String[] args) {
        TransactionClosureParm test1= TransactionHandle.build("test", "bed", () -> {
            System.out.println("=>TransactionClosureParm prepare");
            return Void.TYPE;
        }, "arg1", "arg2", "arg3")
                .setSubmit(() -> {
            System.out.println("=>TransactionClosureParm submit");
        }).setCancel(() -> {
            System.out.println("=>TransactionClosureParm cancel");
        }).setCheckLocalTransactionStateListener((id) -> {
            System.out.println("=>TransactionClosureParm listen");
            return TransactionComposite.SUBMIT;
        });
        test1.prepareAll();
        test1.submit();
///////////////////////////////////////////////////////////事务名,事务类型,事务参数
        TransactionMapParm test2=  TransactionHandle.build("test", "bed", (map) -> {
            System.out.println("=>TransactionMapParm prepare");
            return Void.TYPE;
        }).setSubmit((map) -> {
            System.out.println("=>TransactionMapParm submit");
        }).setCancel((map) -> {
            System.out.println("=>TransactionMapParm cancel");
        }).setCheckLocalTransactionStateListener((id) -> {//事务id
            System.out.println("=>TransactionMapParm listen");
            return TransactionComposite.SUBMIT;
        });
        test2.apply(MapUtils.EMPTY_MAP);//使用map传参
        test2.prepareAll();
        test2.submit();

    }

    ;


}
