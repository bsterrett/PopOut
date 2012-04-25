package popout.search;

import popout.PlayerNum;
import popout.board.*;

public class NegaScout extends Search {

	private static final long serialVersionUID = 12345678910L;


	public NegaScout(BoardState board) {
		super(board);
	}
	
	public void get_computer_move(){
		int alpha = Short.MIN_VALUE;
		int beta = Short.MAX_VALUE;
		int best_move = -1;
		short current_board_short[][] = p_board.get_state();
		BoardState current_board = new BoardState(current_board_short);
		final Move valid_next_moves[] = get_heuristic_ordered_moves(current_board, PlayerNum.COMPUTER);
		for (int i = 0; i < valid_next_moves.length; i++) {
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move, PlayerNum.COMPUTER);			
			final short next_board[][] = current_board.get_state();
			int temp_score = -1 * negascout(next_board, p_depth, PlayerNum.HUMAN, next_move, -1 * beta, -1 * alpha);
			next_move.utility = (short) temp_score;
			current_board.set_state(temp_board);
			if(temp_score > alpha){
				alpha = temp_score;
				best_move = i;
			}
			beta = alpha + 1;
			if (p_depth > 5 && i != valid_next_moves.length - 1) System.out.printf("Computer is %2d percent done with search.%n", ((100 * (i + 1)) / valid_next_moves.length));
		}
		if (-1 == best_move) {
			// couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		} else {
			// the search found one or more best moves, committing to a random one
			p_board.make_move(valid_next_moves[best_move], PlayerNum.COMPUTER);
		}
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			System.out.print(valid_next_moves[i].type + "" + valid_next_moves[i].col + " : " + valid_next_moves[i].utility + "     ");
		}
		System.out.println("");
			
	}
	
	private int negascout(final short[][] test_board_short, final int depth, final short turn, final Move current_move, final int start_alpha, final int start_beta){
		BoardState current_board = new BoardState(test_board_short);
		
		if (depth <= 0 || current_board.compute_win() != PlayerNum.EMPTY_SPACE) {
			return evaluate_board(current_board, current_move);
		}
		
		int  alpha = start_alpha;
		int  beta = start_beta;
		
		Move valid_next_moves[] = null;
		if( depth > 4 ) valid_next_moves = get_heuristic_ordered_moves(current_board, turn);
		else valid_next_moves = get_cheap_ordered_moves(current_board, turn);
		
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move, turn);			
			final short next_board[][] = current_board.get_state();			
			int temp_score = -1 * negascout(next_board, depth - 1, PlayerNum.opposite(turn), next_move, -1 * beta, -1 * alpha);
			current_board.set_state(temp_board);
			
			if(0 != i && temp_score > alpha && temp_score < start_beta){
				temp_score = -1 * negascout(next_board, depth - 1, PlayerNum.opposite(turn), next_move, -1 * start_beta, -1 * alpha);
			}
			alpha = Math.max(alpha, temp_score);			
			if(alpha >= start_beta){
				return alpha;
			}			
			beta = alpha + 1;
		}
		return alpha;
	}
}
