package popout.search;

import popout.board.BoardState;

public abstract class Search {

	protected BoardState p_board;
	
	public Search(BoardState board){
		p_board = board;
	}
	
	public void make_next_move(){
		//this should do something if called by a particular search algorithm
		System.err.println("Called generic Search.make_next_move(), need to specify search type!");
	}
}


