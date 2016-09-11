package transaction.spi.entries;

import transaction.exception.TransactionCompensationException;
import transaction.spi.TransactionOperate;
import transaction.spi.function.TransactionFunction;
import transaction.spi.function.TransactionSubmit;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by karak on 16-9-11.
 */
public class MessageTransaction extends Transaction {
    Function<String, String> localListener;
    Function<String, String> remoteListener;
    long localcycle;//检查周期
    long remotecycle;//检查周期
    public MessageTransaction(TransactionOperate factory){
       super(factory);
    }
    //本地对外发布接口,对下游提供接口,查询事务状态,这个函数主动访问这个接口,这个函数部署在下游,对一定时间的prepare消息的数据检验,检查提交了没有
    public MessageTransaction setCheckLocalListener(Function<String, String> listener, long cycle) throws TransactionCompensationException {
        this.localListener = listener;
        this.localcycle=cycle;
        return this;
    }
    //本地主动查询事务下游的状态,减少重复发送消息,如果下游没有回复,补偿写这里
    public MessageTransaction setCheckRemoteListener(Function<String, String> listener, long remotecycle) throws TransactionCompensationException {
        this.remoteListener = listener;
        this.remotecycle=remotecycle;
        return this;
    }
    public MessageTransaction setPrepare(TransactionFunction prepare) throws TransactionCompensationException {
        this.prepare = prepare;
        return this;
    }
    public MessageTransaction setSubmit(TransactionSubmit submit) throws TransactionCompensationException {
        this.submit = submit;
        return this;
    }
    @Override
    public Object apply(Map map) {
        return operate.visit(this,map);
    }
    @Override
    public boolean deploy() {
        return true;
    }
}
