package popout.search;

import popout.board.BoardState;

public class AlphaBeta extends Search {

	protected final short p_depth;
	private static final long serialVersionUID = 112358L;

	public AlphaBeta(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number) {
		super(board, empty_space_number, player_number, computer_number);
		p_depth = 9;
	}

	public AlphaBeta(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number,
			final short depth) {
		super(board, empty_space_number, player_number, computer_number);
		p_depth = depth;
	}
	
	public void compute(){
		
	}

	public void get_computer_move() {
		short alpha = Short.MIN_VALUE;
		short beta = Short.MAX_VALUE;
		int best_move = -1;
		short current_board_short[][] = p_board.get_state();
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board.get_ordered_available_moves(p_computer_number);
		final short move_utilities[] = new short[valid_next_moves.length];
		int utilities_iter = 0;
		for (int i = 0; i < valid_next_moves.length; i++) {
			int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_score = 0;
			short temp_board[][] = current_board.get_state();
			short next_board[][] = null;
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop				
				current_board.drop(move_col, p_computer_number);
				next_board = current_board.get_state();
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop
				current_board.pop(move_col, p_computer_number);
				next_board = current_board.get_state();		
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
			}			
			temp_score = alpha_beta(next_board, p_depth, p_player_number, valid_next_moves[i], alpha, beta);
			move_utilities[utilities_iter++] = temp_score;
			current_board.set_state(temp_board);
			
			if (temp_score > alpha) {
				// if this move is better than the best known move so far,
				// change the list of best moves to be only this move
				alpha = temp_score;
				best_move = i;
			}
			if (p_depth > 5 && i != valid_next_moves.length - 1) System.out.printf("Computer is %2d percent done with search.%n", ((100 * (i + 1)) / valid_next_moves.length));
		}

		if (-1 == best_move) {
			// couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		} else {
			// the search found one or more best moves, committing to a random
			// one
			if ('D' == valid_next_moves[best_move].charAt(0)) p_board.drop(Integer.parseInt(valid_next_moves[best_move].substring(2)), p_computer_number);
			if ('P' == valid_next_moves[best_move].charAt(0)) p_board.pop(Integer.parseInt(valid_next_moves[best_move].substring(2)), p_computer_number);
		}
		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			// System.out.print(valid_next_moves[i] + " : " + move_utilities[i]
			// + "     ");
		}
		System.out.println("");
	}

	private final short alpha_beta(final short[][] test_board_short,
			final int depth, final short turn, final String move,
			final short start_alpha, final short start_beta) {
		// Recursive function which will create a complete game tree up to a
		// certain depth, then search the tree for good moves
		short test_board_temp[][] = test_board_short;
		BoardState current_board = new BoardState(test_board_temp);

		if (depth <= 0 || current_board.compute_win() != p_empty_space_number) {
			return p_heuristic_func.evaluate_board(current_board, move);
		}

		short alpha = start_alpha;
		short beta = start_beta;

		final String valid_next_moves[] = current_board
				.get_ordered_available_moves(turn);
		// final String valid_next_moves[] =
		// current_board.fake_next_moves(debug_node++, turn);
		for (int i = 0; i < valid_next_moves.length; i++) {
			short temp_score = 0;
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
				return 0;
			}
			
			temp_score = alpha_beta(next_board, depth - 1, (turn == p_player_number ? p_computer_number	: p_player_number), valid_next_moves[i], alpha,	beta);
			current_board.set_state(temp_board);
			
			if (p_computer_number == turn) {
				alpha = (short) Math.max(alpha, temp_score);
				if (alpha >= beta)
					return alpha;
			} else if (p_player_number == turn) {
				beta = (short) Math.min(beta, temp_score);
				if (alpha >= beta)
					return beta;
			} else {
				System.err.println("Alpha Beta doesn't know whose turn it is!");
			}
		}

		if (p_computer_number == turn) {
			return alpha;
		} else {
			return beta;
		}

	}

}
