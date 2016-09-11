package transaction.spi;

/**
 * Created by karak on 16-9-10.
 */

public class TransactionHandle {

/*
    public static TransactionComposite ofList(TransactionComposite... fun) {
        return new TransactionList(fun);
    }

   private static void  traverse(TransactionComposite fun, List<TransactionFunction<?>> prepareList, List<TransactionSubmit> submitList, List<TransactionCancel> cancelList){

        if(!fun.prepare.equals(PREPARE)){
            prepareList.add(fun.prepare);
        }


        for(Object it :fun.children){
            TransactionComposite  innerFun=(TransactionComposite) it;
            if(!innerFun.prepare.equals(PREPARE)){
                prepareList.add(fun.prepare);
            }
            traverse(innerFun,prepareList,submitList,cancelList);
            if(!innerFun.submit.equals(SUBMIT)){
                submitList.add(fun.submit);
            }
            if(!innerFun.cancel.equals(CANCEL)){
                cancelList.add(fun.cancel);
            }
        }
       if(!fun.cancel.equals(CANCEL)){
           cancelList.add(fun.cancel);
       }

   };

    public static
    <T extends Serializable>
     map1PCOptimization(final TransactionComposite<T> fun) {

        List<TransactionFunction<?>> prepareList = new ArrayList<>();
        List<TransactionSubmit> submitList = new ArrayList<>();
        List<TransactionCancel> cancelList = new ArrayList<>();

        traverse(fun,prepareList,submitList,cancelList);



        if (fun.count() == 0) {
            return (args) -> {
                args.put(TransactionComposite.TX_STATE, TransactionComposite.UNKNOW);
                T result=null;
                try {
                    result= fun.prepare.accept(args);
                    args.put(TransactionComposite.TX_STATE, TransactionComposite.PREPARE);
                    fun.submit.accept(args);
                    args.put(TransactionComposite.TX_STATE, TransactionComposite.SUBMIT);
                } catch (Exception e) {
                    e.printStackTrace();
                    fun.cancel.accept(args);
                }
                if (TransactionComposite.SUBMIT.equals(args.get(TransactionComposite.TX_STATE))) {
                    return Optional.ofNullable( result);
                } else {
                    return Optional.empty();
                }
            };
        } else {
            return (args) -> {
                //args.put(TransactionComposite.TX_STATE, TransactionComposite.UNKNOW);
                Deque<TransactionComposite> txStack = new ArrayDeque<>();
                args.put(TransactionComposite.TX_STACK, txStack);
                try {
                    fun.prepare(args);
                   for(TransactionComposite it:fun.children){
                       it.prepare.accept(args);
                   }
                    fun.submit(args);
                } catch (Exception e) {
                    e.printStackTrace();
                    do {
                        TransactionComposite transactionComposite = txStack.pollLast();
                        if (transactionComposite == null) {
                            break;
                        } else {
                            transactionComposite.cancel.accept(args);
                        }
                    } while (true);

                }
                if (TransactionComposite.SUBMIT.equals(args.get(TransactionComposite.TX_STATE))) {
                    return Optional.ofNullable((T) args.get(TransactionComposite.TX_RESULT));
                } else {
                    return Optional.empty();
                }
            };
        }

    }*/
/*    public static
    <T extends Serializable>
    Function<Map<String, Object>, Optional<T>> mapPrepare(final TransactionComposite<T> fun) {
        if (fun.count() == 0) {
            return (args) -> {
                args.put(TransactionComposite.TX_STATE, TransactionComposite.UNKNOW);
                T result=null;
                try {
                    result= fun.prepare.accept(args);
                    args.put(TransactionComposite.TX_STATE, TransactionComposite.PREPARE);
                    fun.submit.accept(args);
                    args.put(TransactionComposite.TX_STATE, TransactionComposite.SUBMIT);
                } catch (Exception e) {
                    e.printStackTrace();
                    fun.cancel.accept(args);
                }
                if (TransactionComposite.SUBMIT.equals(args.get(TransactionComposite.TX_STATE))) {
                    return Optional.ofNullable( result);
                } else {
                    return Optional.empty();
                }
            };
        } else {
            return (args) -> {
                //args.put(TransactionComposite.TX_STATE, TransactionComposite.UNKNOW);
                Deque<TransactionComposite> txStack = new ArrayDeque<>();
                args.put(TransactionComposite.TX_STACK, txStack);
                try {
                    fun.prepare(args);
                    for(TransactionComposite it:fun.children){
                        it.prepare.accept(args);

                    }
                    fun.submit(args);
                } catch (Exception e) {
                    e.printStackTrace();
                    do {
                        TransactionComposite transactionComposite = txStack.pollLast();
                        if (transactionComposite == null) {
                            break;
                        } else {
                            transactionComposite.cancel.accept(args);
                        }
                    } while (true);

                }
                if (TransactionComposite.SUBMIT.equals(args.get(TransactionComposite.TX_STATE))) {
                    return Optional.ofNullable((T) args.get(TransactionComposite.TX_RESULT));
                } else {
                    return Optional.empty();
                }
            };
        }

    }*/
}
