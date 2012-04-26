package popout.search;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import popout.search.*;

@SuppressWarnings({"unchecked","rawtypes","unused"})

public class SearchWorkQueue {
	
	  public SearchWorkQueue(Search search) {
		  if(search.getClass() == Minimax.class){
			  
		  }
		  else if(search.getClass() == AlphaBeta.class){
			  
		  }
		  else if(search.getClass() == NegaScout.class){
			  
		  }
		  else{
			  System.err.println("SearchWorkQueue doesn't know what type of search to do!");
		  }
		    int nTasks = 10;
		    long n = 30;
		    int tpSize = 10;

		    ThreadPoolExecutor tpe = new ThreadPoolExecutor(tpSize, tpSize, 50000, TimeUnit.MILLISECONDS,
		        new LinkedBlockingQueue<Runnable>());

		    Task[] tasks = new Task[nTasks];
		    for (int i = 0; i < nTasks; i++) {
		      tasks[i] = new Task(n, "Task " + i);
		      n += 3;
		      tpe.execute(tasks[i]);
		    }
		    tpe.shutdown();
		  }
}


/*



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainClass {

public static void main(String[] args) {
 int nTasks = 10;
 long n = 30;
 int tpSize = 10;

 ThreadPoolExecutor tpe = new ThreadPoolExecutor(tpSize, tpSize, 50000, TimeUnit.MILLISECONDS,
     new LinkedBlockingQueue<Runnable>());

 Task[] tasks = new Task[nTasks];
 for (int i = 0; i < nTasks; i++) {
   tasks[i] = new Task(n, "Task " + i);
   n += 3;
   tpe.execute(tasks[i]);
 }
 tpe.shutdown();
}
}

class SingleThreadAccess {

private ThreadPoolExecutor tpe;

public SingleThreadAccess() {
 tpe = new ThreadPoolExecutor(1, 1, 50000L, TimeUnit.SECONDS,
     new LinkedBlockingQueue<Runnable>());
}

public void invokeLater(Runnable r) {
 tpe.execute(r);
}

public void invokeAneWait(Runnable r) throws InterruptedException, ExecutionException {
 FutureTask task = new FutureTask(r, null);
 tpe.execute(task);
 task.get();
}

public void shutdown() {
 tpe.shutdown();
}
}

class Task implements Runnable {
long n;

String id;

private long fib(long n) {
 if (n == 0)
   return 0L;
 if (n == 1)
   return 1L;
 return fib(n - 1) + fib(n - 2);
}

public Task(long n, String id) {
 this.n = n;
 this.id = id;
}

public void run() {
 Date d = new Date();
 DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
 long startTime = System.currentTimeMillis();
 d.setTime(startTime);
 System.out.println("Starting task " + id + " at " + df.format(d));
 fib(n);
 long endTime = System.currentTimeMillis();
 d.setTime(endTime);
 System.out.println("Ending task " + id + " at " + df.format(d) + " after "
     + (endTime - startTime) + " milliseconds");
}
}

*/