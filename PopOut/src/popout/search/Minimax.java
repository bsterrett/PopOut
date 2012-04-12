package popout.search;

import popout.board.BoardState;
import java.util.ArrayList;

public class Minimax extends Search implements Runnable {
	
	protected final short p_heuristic;
	protected final short p_depth;
	protected final boolean p_multithreaded;
	protected final short p_max_thread_runtime;
	protected int p_thread_iter;
	protected ArrayList<short[][]> p_next_boards;
	protected ArrayList<String> p_valid_next_moves;
	protected short[] p_temp_scores;
	protected short p_thread_depth;
	protected short p_thread_turn;
	
	public Minimax(BoardState board, final short empty_space_number, final short player_number, final short computer_number){
		super(board, empty_space_number, player_number, computer_number);
		p_heuristic = 4;
		p_depth = 5;
		p_multithreaded = false;
		p_max_thread_runtime = 10000;
		p_thread_iter = 0;
		p_next_boards = new ArrayList<short[][]>();
		p_valid_next_moves = new ArrayList<String>();
		p_temp_scores = new short[0];
		p_thread_depth = 0;
		p_thread_turn = 0;
	}
	
	public Minimax(	BoardState board, final short empty_space_number, final short player_number, final short computer_number,
					final short depth, final short heuristic){
		super(board, empty_space_number, player_number, computer_number);
		p_heuristic = heuristic;
		p_depth = depth;
		p_multithreaded = true;
		p_max_thread_runtime = 10000;
		p_thread_iter = 0;
		p_next_boards = new ArrayList<short[][]>();
		p_valid_next_moves = new ArrayList<String>();
		p_temp_scores = new short[0];
		p_thread_depth = 0;
		p_thread_turn = 0;
	}
	
	public void run(){
		int thread_iter = p_thread_iter++;
		short temp_score = minimax(p_next_boards.get(thread_iter),p_thread_depth,p_thread_turn,p_valid_next_moves.get(thread_iter));
		
	}
	
	public void get_computer_move(){
		// Call this to make the computer move
		short alpha = -32000;
		ArrayList<String> best_moves = new ArrayList<String>();
		short current_board_short[][] = new short[p_column_count][p_row_count];
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				current_board_short[col_iter][row_iter] = p_board.get_state()[col_iter][row_iter];
			}
		}
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board.get_ordered_available_moves(p_computer_number);	
		final short move_utilities[] = new short[valid_next_moves.length];
		int utilities_iter = 0;
		for(int i = 0; i < valid_next_moves.length; i++){
			int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_score = 0; //evaluate_move_two(current_board, valid_next_moves[i]);
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop				
				short temp_board[][] = new short[p_column_count][p_row_count];
				for(int col_iter = 0; col_iter < p_column_count; col_iter++){
					for(int row_iter = 0; row_iter < p_row_count; row_iter++){
						temp_board[col_iter][row_iter] = current_board.get_state()[col_iter][row_iter];
					}
				}
				current_board.drop(move_col, p_computer_number);
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical drop
				temp_score = minimax(next_board, p_depth, p_player_number, valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;	
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				short temp_board[][] = new short[p_column_count][p_row_count];
				for(int col_iter = 0; col_iter < p_column_count; col_iter++){
					for(int row_iter = 0; row_iter < p_row_count; row_iter++){
						temp_board[col_iter][row_iter] = current_board.get_state()[col_iter][row_iter];
					}
				}
				current_board.pop(move_col,p_computer_number);
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical pop
				temp_score += minimax(next_board, p_depth, p_player_number, valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;
			}
			else{
				//this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
			}			
			if(temp_score > alpha){	
				//if this move is better than the best known move so far, change the list of best moves to be only this move
				alpha = temp_score;
				best_moves.clear();
				best_moves.add(valid_next_moves[i]);
			}
			else if(temp_score == alpha){
				//if this move equals the best known move so far, add it to the list of best moves
				best_moves.add(valid_next_moves[i]);
			}
			if(p_depth > 5 && i != valid_next_moves.length-1 )	System.out.printf("Computer is %2d percent done with search.%n", ((100*(i+1))/valid_next_moves.length));
		}
		
		if(1 > best_moves.size()){
			//couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		}
		else{
			//the search found one or more best moves, committing to a random one
			int random_best = p_random.nextInt(best_moves.size());
			if('D' == best_moves.get(random_best).charAt(0)) p_board.drop(Integer.parseInt(best_moves.get(random_best).substring(2)), p_computer_number);
			if('P' == best_moves.get(random_best).charAt(0)) p_board.pop(Integer.parseInt(best_moves.get(random_best).substring(2)), p_computer_number);
		}

		for(int i = 0; i < valid_next_moves.length; i++){
			//for debugging
			System.out.print(valid_next_moves[i] + " : " + move_utilities[i] + "     ");
		}
		System.out.println("");
	}
	
	protected final short minimax(final short[][] test_board_short, final int depth, final short turn, final String move){
		// Recursive function which will create a complete game tree up to a certain depth, then search the tree for good moves
		short test_board_temp[][] = new short[p_column_count][p_row_count];		//paranoid sanitation of references, can probably remove for performance boost
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				test_board_temp[col_iter][row_iter] = test_board_short[col_iter][row_iter];
			}
		}		
		BoardState current_board = new BoardState(test_board_temp);
		final String valid_next_moves[] = current_board.get_available_moves(turn);
		//final String valid_next_moves[] = current_board.fake_next_moves(debug_node++, turn);
		
		short alpha = 0;
		if(		p_player_number == turn)	alpha = 20000;
		else if(p_computer_number == turn)	alpha = -20000;
		else{
			System.err.println("Minimax is unsure of whose turn it is!");
			return 0;
		}
		
		if(depth <= 0 || current_board.compute_win() != p_empty_space_number){
			//if this board is a terminal node
			switch(p_heuristic){
			case 1:
				return evaluate_board_one(current_board);
			case 2:
				return evaluate_board_two(current_board);
			case 3:
				return evaluate_board_three(current_board, move);
			case 4:
				return evaluate_board_four(current_board, move);
			case 101:
				return evaluate_move_one(move);
			case 102:
				return evaluate_move_two(current_board, move);
			default:
				return evaluate_board_four(current_board, move);
			}
		}
		else if( (p_depth-2) == depth && p_multithreaded ){
			//if multithreading is on
			p_temp_scores = new short[valid_next_moves.length];
			p_thread_depth = (short) (depth - 1);
			p_thread_turn = (turn == p_player_number ? p_computer_number : p_player_number);
			ArrayList<Minimax> threads = new ArrayList<Minimax>();
			for(int i = 0; i < valid_next_moves.length; i++){
				final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				if('D' == valid_next_moves[i].charAt(0)){
					//this move is a drop				
					short temp_board[][] = new short[p_column_count][p_row_count];
					for(int col = 0; col < p_column_count; col++){
						for(int row = 0; row < p_row_count; row++){
							temp_board[col][row] = current_board.get_state()[col][row];
						}
					}				
					p_next_boards.add(current_board.get_state());
					p_valid_next_moves.add(valid_next_moves[i]);
					current_board.drop(move_col, turn);
					current_board.set_state(temp_board);
					//recursive call
					
				}
				else if('P' == valid_next_moves[i].charAt(0)){
					//this move is a pop
					short temp_board[][] = new short[p_column_count][p_row_count];
					for(int col = 0; col < p_column_count; col++){
						for(int row = 0; row < p_row_count; row++){
							temp_board[col][row] = current_board.get_state()[col][row];
						}
					}
					current_board.pop(move_col, turn);
					short next_board[][] = current_board.get_state();
					current_board.set_state(temp_board);
					//recursive call
				}
				else{
					//this move is not recognized
					System.err.println("Unrecognized available move: " + valid_next_moves[i]);
					return 0;
				}
			
			}
			
			int threads_running = 0;
			do{
				threads_running = 0;
				for(Minimax thread : threads){
					if(thread.isAlive()) threads_running++;
				}
			} while (threads_running > 0);
			
			for(Minimax thread : threads){
				short temp_score = thread.get_alpha();
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
			}
			return alpha;
		}
		else{
			// multithreading is off
			for(int i = 0; i < valid_next_moves.length; i++){
				short temp_score = 0;
				final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				if('D' == valid_next_moves[i].charAt(0)){
					//this move is a drop				
					short temp_board[][] = new short[p_column_count][p_row_count];
					for(int col = 0; col < p_column_count; col++){
						for(int row = 0; row < p_row_count; row++){
							temp_board[col][row] = current_board.get_state()[col][row];
						}
					}				
					short next_board[][] = current_board.get_state();
					current_board.drop(move_col, turn);
					current_board.set_state(temp_board);
					//recursive call
					temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);	
				}
				else if('P' == valid_next_moves[i].charAt(0)){
					//this move is a pop
					short temp_board[][] = new short[p_column_count][p_row_count];
					for(int col = 0; col < p_column_count; col++){
						for(int row = 0; row < p_row_count; row++){
							temp_board[col][row] = current_board.get_state()[col][row];
						}
					}
					current_board.pop(move_col, turn);
					short next_board[][] = current_board.get_state();
					current_board.set_state(temp_board);
					//recursive call
					temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);				
				}
				else{
					//this move is not recognized
					System.err.println("Unrecognized available move: " + valid_next_moves[i]);
					return 0;
				}
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
			}
			return alpha;		
		}	
		
	}	

}


