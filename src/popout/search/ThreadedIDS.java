package popout.search;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import popout.board.*;

//@SuppressWarnings({"unchecked","rawtypes","unused"})

public class ThreadedIDS {

	private BoardState p_board;
	private ThreadPoolExecutor p_tpe;
	private final int p_min_depth = 6;
	private final int p_depth_increment = 1;
	private final int thread_pool_size = 6;
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
			if(!p_tpe.awaitTermination(1, TimeUnit.SECONDS)){
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
				//System.out.println("Found a thread that didnt complete!");
			}
			else{
				System.out.println("Moved from search of depth: " + String.valueOf(p_min_depth + p_depth_increment*i));
				p_board.make_move(searches[i].get_stashed_move());
				return;
			}
		}
	}
	
}

