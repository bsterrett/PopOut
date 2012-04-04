package popout.search;

import popout.board.BoardState;

public abstract class Search {

	protected BoardState p_board;
	protected final short p_player_number;
	protected final short p_computer_number;
	
	public Search(BoardState board){
		p_board = board;
		p_player_number = 1;
		p_computer_number = 2;
	}
	
	public void make_next_move(){
		//this should do something if called by a particular search algorithm
		System.err.println("Called generic Search.make_next_move(), need to specify search type!");
	}
}


