package com.yqq.pushservice.utils;


import android.util.Log;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池辅助类，整个应用程序就只有一个线程池去管理线程。
 * 可以设置核心线程数、最大线程数、额外线程空状态生存时间，阻塞队列长度来优化线程池。
 * 
 * 
 *
 */
public class ThreadPoolUtils {
    private static  final String TAG="ThreadPoolUtils";
    
    private ThreadPoolUtils(){
        
    }

    private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //线程池核心线程数 cpu+1
    private static int CORE_POOL_SIZE =  Math.max(2, Math.min(CPU_COUNT - 1, 4));

    //线程池最大线程数
    private static int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors()*2+1;
    
    //额外线程空状态生存时间
    private static int KEEP_ALIVE_TIME = 30;
    
    //阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程。
    private static LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
//    private static final BlockingQueue<Runnable> workQueue =
//            new LinkedBlockingQueue<Runnable>();
    
    //线程工厂
    private static ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger(1);
 
        @Override
        public Thread newThread(Runnable r) {
            Log.e(TAG,"****************new thread create***************");
            return new Thread(r, "ThreadPoolUtils thread:" + integer.getAndIncrement());
        }
    };
    
    //线程池
   // private static ThreadPoolExecutor threadPool;
    
    private static ExecutorService threadPool;
    
    static {
    	//threadPool=	Executors.newCachedThreadPool();


       threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                threadFactory);

        
    }
    
   public static ExecutorService getInstance(){

           return threadPool;

   }
    
    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     * @param runnable
     */
    public static void execute(Runnable runnable){
        threadPool.execute(runnable);
      
    }



 
}
