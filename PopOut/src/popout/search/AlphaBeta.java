package popout.search;

import popout.board.BoardState;

public class AlphaBeta extends Search{
	
	protected final short p_heuristic;
	protected final short p_depth;

	public AlphaBeta(BoardState board, final short empty_space_number, final short player_number, final short computer_number){
		super(board, empty_space_number, player_number, computer_number);
		p_heuristic = 4;
		p_depth = 5;
	}
	
	public AlphaBeta(	BoardState board, final short empty_space_number, final short player_number, final short computer_number,
						final short depth, final short heuristic){
		super(board, empty_space_number, player_number, computer_number);
		p_heuristic = heuristic;
		p_depth = depth;
	}
	
	
}
