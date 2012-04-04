package popout.search;

import popout.board.BoardState;

public abstract class Search {

	protected BoardState p_board;
	protected final short p_player_number;
	protected final short p_computer_number;
	protected final int p_column_count;
	protected final int p_row_count;
	
	public Search(BoardState board){
		p_board = board;
		p_player_number = 1;
		p_computer_number = 2;
		p_column_count = p_board.get_state().length;
		p_row_count = p_board.get_state()[0].length;
		
	}
	
	public void make_next_move(){
		//this should do something if called by a particular search algorithm
		System.err.println("Called generic Search.make_next_move(), need to specify search type!");
	}
}


