package popout.search;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import popout.PlayerNum;
import popout.board.*;

//@SuppressWarnings({"unchecked","rawtypes","unused"})

public class ThreadedIDS {

	private BoardState p_board;
	private ThreadPoolExecutor p_tpe;
	private final int p_min_depth = 6;
	private final int p_depth_increment = 1;
	private final int thread_pool_size = 7;
	private final long thread_resize_time = 10;
	private final int thread_search_type;
	public final static int Minimax = 0;
	public final static int AlphaBeta = 1;
	public final static int NegaScout = 2;
	
	public ThreadedIDS(BoardState board, int search_type){
		p_board = board;
		thread_search_type = search_type;
		
	}
	
	public void start_search(){
		p_tpe = new ThreadPoolExecutor(thread_pool_size, thread_pool_size,
				thread_resize_time, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		final short[][] current_board = p_board.get_state();
		Search[] searches = new Search[thread_pool_size];
		switch(thread_search_type){
		case Minimax:
			//Minimax here
			break;
			
		case AlphaBeta:
			//AlphaBeta here
			for(int i = 0; i < thread_pool_size; i++){
				searches[i] = new AlphaBeta(new BoardState(current_board), (short) (p_min_depth + p_depth_increment*i) );
				p_tpe.execute(searches[i]);
			}
			break;
			
		case NegaScout:
			//Negascout here
			for(int i = 0; i < thread_pool_size; i++){
				searches[i] = new AlphaBeta(new BoardState(current_board), (short) (p_min_depth + p_depth_increment*i) );
				p_tpe.execute(searches[i]);
			}
			break;
			
		default:
			System.err.println("Threaded IDS doesn't know what kind of search to do!");
			return;
		}
		
		p_tpe.shutdown();
		
		try{
			if(!p_tpe.awaitTermination(20, TimeUnit.SECONDS)){
				p_tpe.shutdownNow();
			}
		}
		catch(InterruptedException e){
			p_tpe.shutdownNow();
			Thread.currentThread().interrupt();
		}
		
		for(int i = thread_pool_size - 1; i >= 0; i--){
			if(searches[i].get_stashed_move().col == -1 || searches[i].get_stashed_move().col == -1){
				searches[i].interrupt();
				System.out.println("Found a thread that didnt complete!");
			}
			else{
				System.out.println("Moved from search of depth: " + String.valueOf(p_min_depth + p_depth_increment*i));
				p_board.make_move(searches[i].get_stashed_move(), PlayerNum.COMPUTER);
				return;
			}
		}
	}
	
}


/*





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