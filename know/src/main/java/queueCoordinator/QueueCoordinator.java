package queueCoordinator;

/**
 * Created by liuding on 5/11/16.
 *
 * 排重状态机
 */
public interface QueueCoordinator<T> {

    public boolean enQueue(T key);

    public void deQueue(T key);

}


