package popout.search;

import popout.PlayerNum;
import popout.board.BoardState;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Minimax extends Search {
	
	private static final long serialVersionUID = 31415L;

	protected final short p_depth;
	protected final boolean p_multithreaded;
	protected final short p_max_thread_runtime;
	
	protected short p_thread_alpha;
	protected int p_thread_depth;
	protected short p_thread_turn;
	protected String p_thread_move;

	public Minimax(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number) {
		super(board, empty_space_number, player_number, computer_number);
		p_depth = 41;
		p_multithreaded = true;
		p_max_thread_runtime = 10000;

	}

	public Minimax(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number,
			final short depth) {
		super(board, empty_space_number, player_number, computer_number);
		p_depth = depth;
		p_multithreaded = true;
		p_max_thread_runtime = 10000;
	}
	
	public Minimax(final short[][] target_board, final short empty_space_number,
			final short player_number, final short computer_number, final short target_depth,
			final int current_depth, final short turn, final String move){
		super(new BoardState(target_board), empty_space_number, player_number, computer_number);
		p_depth = target_depth;
		p_thread_depth = current_depth;
		p_thread_turn = turn;
		p_thread_move = move;
		p_multithreaded = true;
		p_max_thread_runtime = 10000;
	}
	
	private final short get_alpha(){
		return p_thread_alpha;
	}
	
	public void compute(){
		//p_thread_alpha = minimax(p_board.get_state(), p_thread_depth, p_thread_turn, p_thread_move);
		
		BoardState current_board = p_board;
		final String valid_next_moves[] = current_board.get_cheap_ordered_available_moves(p_thread_turn);

		if (p_player_number == p_thread_turn)
			p_thread_alpha = Short.MAX_VALUE;
		else if (p_computer_number == p_thread_turn)
			p_thread_alpha = Short.MIN_VALUE;
		else {
			System.err.println("Minimax is unsure of whose turn it is!");
			return;
		}

		if (p_thread_depth <= 0 || current_board.compute_win() != p_empty_space_number) {
			// if this board is a terminal node
			p_thread_alpha = evaluate_board(current_board, p_thread_move);
			return;
		}		
		
		
		ForkJoinPool pool = new ForkJoinPool();			
		ArrayList<Minimax> threads = new ArrayList<Minimax>();
		for (int i = 0; i < valid_next_moves.length; i++) {
			final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			Minimax new_thread = null;
			short next_board[][] = null;
			short temp_board[][] = current_board.get_state();
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop
				current_board.drop(move_col, p_thread_turn);
				next_board = current_board.get_state();							
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop				
				current_board.pop(move_col, p_thread_turn);
				next_board = current_board.get_state();
								
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
				return;
			}			
			new_thread = new Minimax(next_board, p_empty_space_number, p_player_number, p_computer_number, p_depth, p_thread_depth-1, (p_thread_turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);
			current_board.set_state(temp_board);
			threads.add(new_thread);
			pool.invoke(new_thread);		
		}	
		
		
		while(pool.getActiveThreadCount() > 0);
		
		for(int i = 0; i < threads.size(); i++){
			threads.get(i).join();
			short temp_score = threads.get(i).get_alpha();
			p_thread_alpha = (short) (p_thread_turn == p_player_number ? Math.min(p_thread_alpha, temp_score) : Math.max(p_thread_alpha, temp_score));
		}

	}

	public void get_computer_move() {
		// Call this to make the computer move
		short alpha = Short.MIN_VALUE;
		ArrayList<String> best_moves = new ArrayList<String>();
		short current_board_short[][] = new short[p_column_count][p_row_count];
		for (int col_iter = 0; col_iter < p_column_count; col_iter++) {
			for (int row_iter = 0; row_iter < p_row_count; row_iter++) {
				current_board_short[col_iter][row_iter] = p_board.get_state()[col_iter][row_iter];
			}
		}
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board
				.get_cheap_ordered_available_moves(p_computer_number);
		final short move_utilities[] = new short[valid_next_moves.length];
		int utilities_iter = 0;
		for (int i = 0; i < valid_next_moves.length; i++) {
			int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_score = 0; // evaluate_move_two(current_board,
									// valid_next_moves[i]);
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop
				short temp_board[][] = current_board.get_state();
				current_board.drop(move_col, p_computer_number);
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical drop
				temp_score = minimax(next_board, p_depth, p_player_number,
						valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop
				short temp_board[][] = current_board.get_state();
				current_board.pop(move_col, p_computer_number);
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical pop
				temp_score += minimax(next_board, p_depth, p_player_number,
						valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: "
						+ valid_next_moves[i]);
			}
			if (temp_score > alpha) {
				// if this move is better than the best known move so far,
				// change the list of best moves to be only this move
				alpha = temp_score;
				best_moves.clear();
				best_moves.add(valid_next_moves[i]);
			} else if (temp_score == alpha) {
				// if this move equals the best known move so far, add it to the
				// list of best moves
				best_moves.add(valid_next_moves[i]);
			}
			if (p_depth > 5 && i != valid_next_moves.length - 1)
				System.out.printf(
						"Computer is %2d percent done with search.%n",
						((100 * (i + 1)) / valid_next_moves.length));
		}

		if (1 > best_moves.size()) {
			// couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		} else {
			// the search found one or more best moves, committing to a random
			// one
			int random_best = p_random.nextInt(best_moves.size());
			if ('D' == best_moves.get(random_best).charAt(0))
				p_board.drop(
						Integer.parseInt(best_moves.get(random_best).substring(
								2)), p_computer_number);
			if ('P' == best_moves.get(random_best).charAt(0))
				p_board.pop(
						Integer.parseInt(best_moves.get(random_best).substring(
								2)), p_computer_number);
		}

		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			System.out.print(valid_next_moves[i] + " : " + move_utilities[i]
					+ "     ");
		}
		System.out.println("");
	}

	protected final short minimax(final short[][] test_board_short,
			final int depth, final short turn, final String move) {
		// Recursive function which will create a complete game tree up to a
		// certain depth, then search the tree for good moves
		short test_board_temp[][] = test_board_short;
		BoardState current_board = new BoardState(test_board_temp);
		final String valid_next_moves[] = current_board
				.get_available_moves(turn);

		short alpha = 0;
		if (p_player_number == turn)
			alpha = Short.MAX_VALUE;
		else if (p_computer_number == turn)
			alpha = Short.MIN_VALUE;
		else {
			System.err.println("Minimax is unsure of whose turn it is!");
			return 0;
		}

		if (depth <= 0 || current_board.compute_win() != p_empty_space_number) {
			// if this board is a terminal node
			return evaluate_board(current_board, move);
		} else if (p_multithreaded && depth >= 4) {
			// if multithreading is on

			ForkJoinPool pool = new ForkJoinPool();			
			ArrayList<Minimax> threads = new ArrayList<Minimax>();
			for (int i = 0; i < valid_next_moves.length; i++) {
				final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				Minimax new_thread = null;
				short next_board[][] = null;
				short temp_board[][] = current_board.get_state();
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
				current_board.set_state(temp_board);
				//recursive thread call
				new_thread = new Minimax(next_board, p_empty_space_number, p_player_number, p_computer_number, p_depth, p_thread_depth-1, (p_thread_turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);
				threads.add(new_thread);
				pool.invoke(new_thread);
			}
			
			
			while(pool.getActiveThreadCount() > 0);
			
			for(int i = 0; i < threads.size(); i++){
				threads.get(i).join();
				short temp_score = threads.get(i).get_alpha();
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
			}
			return alpha;
			
		} else {
			// multithreading is off
			for (int i = 0; i < valid_next_moves.length; i++) {
				short temp_score = 0;
				final int move_col = Integer.parseInt(valid_next_moves[i]
						.substring(2));
				if ('D' == valid_next_moves[i].charAt(0)) {
					// this move is a drop
					short temp_board[][] = current_board.get_state();
					current_board.drop(move_col, turn);
					short next_board[][] = current_board.get_state();
					current_board.set_state(temp_board);
					// recursive call
					temp_score = minimax(next_board, depth - 1,
							(turn == p_player_number ? p_computer_number
									: p_player_number), valid_next_moves[i]);
				} else if ('P' == valid_next_moves[i].charAt(0)) {
					// this move is a pop
					short temp_board[][] = current_board.get_state();
					current_board.pop(move_col, turn);
					short next_board[][] = current_board.get_state();
					current_board.set_state(temp_board);
					// recursive call
					temp_score = minimax(next_board, depth - 1,
							(turn == p_player_number ? p_computer_number
									: p_player_number), valid_next_moves[i]);
				} else {
					// this move is not recognized
					System.err.println("Unrecognized available move: "
							+ valid_next_moves[i]);
					return 0;
				}
				alpha = (short) (turn == p_player_number ? Math.min(alpha,
						temp_score) : Math.max(alpha, temp_score));
			}
			alpha += 1;
			return alpha;
		}

	}

}
