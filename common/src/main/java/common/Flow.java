package common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by karak on 16-9-6.
 */
public interface Flow {
    //客户端同步服务端异步
      default <T>  T futureToSyn(Future<T> future)throws InterruptedException, ExecutionException{
        synchronized(future){
            while(!future.isDone()){
                future.wait();//server处理结束后会notify这个future，并修改isdone标志
            }
        }
        return future.get();
    }
}
