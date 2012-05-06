package popout.search;

import popout.PlayerNum;
import popout.board.*;

public class AlphaBeta extends Search {

	//private static final long serialVersionUID = 112358L;

	public AlphaBeta(final BoardState board) {
		super(board);
	}
	
	public AlphaBeta(final BoardState board, short depth){
		super(board, depth);
	}

	public void run(){
		get_computer_move();
	}

	public Move get_computer_move() {
		short alpha = Short.MIN_VALUE;
		short beta = Short.MAX_VALUE;
		int best_move = -1;
		short current_board_short[][] = p_board.get_state();
		BoardState current_board = new BoardState(current_board_short);
		final Move valid_next_moves[] = get_heuristic_ordered_moves(current_board, PlayerNum.COMPUTER);
		for (int i = 0; i < valid_next_moves.length; i++) {
			if(p_interrupted) return null;
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move);			
			final short next_board[][] = current_board.get_state();
			final short temp_score = alpha_beta(next_board, p_depth, PlayerNum.HUMAN, next_move, alpha, beta);
			next_move.utility = temp_score;
			current_board.set_state(temp_board);			
			if (temp_score > alpha) {
				// if this move is better than the best known move so far,
				// change the list of best moves to be only this move
				alpha = temp_score;
				best_move = i;
			}
			//if (p_depth > 5 && i != valid_next_moves.length - 1) System.out.printf("Computer is %2d percent done with search.%n", ((100 * (i + 1)) / valid_next_moves.length));
		}

		if (-1 == best_move) {
			// couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
			return null;
		}		
		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			//System.out.print(valid_next_moves[i].type + "" + valid_next_moves[i].col + " : " + valid_next_moves[i].utility + "     ");
		}
		//System.out.println("");
		p_stashed_move = valid_next_moves[best_move];
		return valid_next_moves[best_move];
	}

	public void make_computer_move(){
		p_board.make_move(get_computer_move());
	}
	
	private final short alpha_beta(final short[][] test_board_short, final int depth, final short turn, final Move current_move, final short start_alpha, final short start_beta) {
		// Recursive function which will create a complete game tree up to a
		// certain depth, then search the tree for good moves
		
		BoardState current_board = new BoardState(test_board_short);

		if (depth <= 0 || current_board.compute_win() != PlayerNum.EMPTY_SPACE) {
			return evaluate_board(current_board, current_move);
		}

		short alpha = start_alpha;
		short beta = start_beta;

		Move valid_next_moves[] = null;
		if( depth > 4 ) valid_next_moves = get_heuristic_ordered_moves(current_board, turn);
		else valid_next_moves = get_cheap_ordered_moves(current_board, turn);
		
		if(depth == p_depth){
			alpha += 0;
		}
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			if(p_interrupted) return 0;
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move);
			if(current_board.compute_win() == turn){
				alpha += 0;
				return evaluate_board(current_board, next_move);
			}
			else{
				alpha += 0;
			}
			final short next_board[][] = current_board.get_state();			
			final short temp_score = alpha_beta(next_board, depth - 1, PlayerNum.opposite(turn), next_move, alpha, beta);
			current_board.set_state(temp_board);
			
			if (PlayerNum.COMPUTER == turn) {
				alpha = (short) Math.max(alpha, temp_score);
				if (alpha >= beta) return alpha;
			} else if (PlayerNum.HUMAN == turn) {
				beta = (short) Math.min(beta, temp_score);
				if (alpha >= beta) return beta;
			} else {
				System.err.println("Alpha Beta doesn't know whose turn it is!");
			}
		}
		if (PlayerNum.COMPUTER == turn) {
			return alpha;
		} else {
			return beta;
		}
	}

}
