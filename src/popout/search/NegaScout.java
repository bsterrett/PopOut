package popout.search;

import popout.PlayerNum;
import popout.board.BoardState;

public class NegaScout extends Search {

	private static final long serialVersionUID = 12345678910L;
	private short p_stashed_score;

	public NegaScout(BoardState board) {
		super(board);
		//do the normal get_computer_move stuff here
	}
	
	public NegaScout(final short[][] test_board_short, final int depth, final short turn, final String move, final int start_alpha, final int start_beta){
		super(new BoardState(test_board_short));
		//do recursive negascout search here
		BoardState current_board = new BoardState(test_board_short);
		
		int alpha = start_alpha;
		int beta = start_beta;
		String valid_next_moves[] = null;
		
		if (depth <= 0 || current_board.compute_win() != PlayerNum.EMPTY_SPACE){
			p_stashed_score = evaluate_board(current_board, move);
			return;
		}
		else if(depth > 4){
			valid_next_moves = get_heuristic_ordered_moves(current_board, turn);
		}
		else{
			valid_next_moves = current_board.get_cheap_ordered_available_moves(turn);
		}
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_board[][] = current_board.get_state();
			short next_board[][] = null;
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop
				current_board.drop(move_col, turn);
				next_board = current_board.get_state();
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop
				current_board.pop(move_col, turn);
				next_board = current_board.get_state();
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
				p_stashed_score = 0;
				return;
			}
			
			NegaScout new_branch = new NegaScout(next_board, depth - 1, PlayerNum.opposite(turn), valid_next_moves[i], -1 * beta, -1 * alpha);
			short temp_score = new_branch.get_score();
			if(0 != i && temp_score > alpha && temp_score < start_beta){
				new_branch = new NegaScout(next_board, depth - 1, PlayerNum.opposite(turn), valid_next_moves[i], -1 * start_beta, -1 * alpha);
				temp_score = new_branch.get_score();
			}
			current_board.set_state(temp_board);
			alpha = Math.max(alpha, temp_score);
			if(alpha >= start_beta){
				p_stashed_score = (short) alpha;
				return;
			}
			beta = alpha + 1;
		}
	}
	
	public short get_score(){
		return p_stashed_score;
	}

}
