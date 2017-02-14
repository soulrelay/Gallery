
package com.soulrelay.gallery.utils;


import android.os.AsyncTask;


import java.util.concurrent.Executor;


/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  线程池辅助类，整个应用程序就只有一个线程池去管理线程。 可以设置核心线程数、最大线程数、额外线程空状态生存时间，阻塞队列长度来优化线程池。
 * 下面的数据都是参考Android的AsynTask里的数据。
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class ThreadPoolUtils {

    // 线程池
    private static Executor threadPool;

    static {
        threadPool = AsyncTask.THREAD_POOL_EXECUTOR;
    }

    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static Executor getExecutor(){
        return threadPool;
    }

}
