package popout.search;

import popout.board.BoardState;

public class Minimax extends Search {
	
	protected final int p_heuristic;
	
	public Minimax(BoardState board){
		super(board);
		p_heuristic = 0;
	}
	
	public Minimax(BoardState board, int heuristic){
		super(board);
		p_heuristic = heuristic;
	}
	
	public void make_next_move(){
		final String valid_next_moves[] = p_board.get_available_moves();
		for(int i = 0; i < valid_next_moves.length; i++){
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			}
			else{
				//this move is not recognized
				System.out.println("Unrecognized available move: " + valid_next_moves[i]);
			}
		}
	}
	
}
