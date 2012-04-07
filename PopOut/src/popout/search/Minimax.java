package popout.search;

import popout.board.BoardState;

public class Minimax extends Search {
	
	protected final short p_heuristic;
	protected final short p_depth;
	
	public Minimax(BoardState board){
		super(board);
		p_heuristic = 3;
		p_depth = 6;
	}
	
	public Minimax(BoardState board, short depth, short heuristic){
		super(board);
		p_heuristic = heuristic;
		p_depth = depth;
	}
	
	public void make_next_move(){
		// Call this to make the computer move
		short depth = p_depth;
		short alpha = -32000;
		int best_move = -1;		
		short current_board_short[][] = new short[p_column_count][p_row_count];
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				current_board_short[col_iter][row_iter] = p_board.get_state()[col_iter][row_iter];
			}
		}
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board.get_available_moves(p_computer_number);	
		final short move_utilities[] = new short[valid_next_moves.length];
		int utilities_iter = 0;
		for(int i = 0; i < valid_next_moves.length; i++){
			int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
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
				short temp_score = minimax(next_board, depth-1, p_player_number, valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;
				if(temp_score >= alpha){				
					alpha = temp_score;
					best_move = i;
				}
						
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
				short temp_score = minimax(next_board, depth-1, p_player_number, valid_next_moves[i]);
				move_utilities[utilities_iter++] = temp_score;
				if(temp_score >= alpha){				
					alpha = temp_score;
					best_move = i;
				}
			}
			else{
				//this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
			}
			if(p_depth > 6 && i != valid_next_moves.length-1 )	System.out.printf("Computer is %2d percent done with search.%n", ((100*(i+1))/valid_next_moves.length));
		}
		
		if(best_move == -1){
			//couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		}
		else{
			//the search returned a best move, now carrying it out on the actual game board
			if('D' == valid_next_moves[best_move].charAt(0)) p_board.drop(Integer.parseInt(valid_next_moves[best_move].substring(2)), p_computer_number);
			if('P' == valid_next_moves[best_move].charAt(0)) p_board.pop(Integer.parseInt(valid_next_moves[best_move].substring(2)), p_computer_number);
		}
		for(int i = 0; i < valid_next_moves.length; i++){
			System.out.print(valid_next_moves[i] + "  " + move_utilities[i] + "    ");
		}
		System.out.println("");
	}
	
	private final short minimax(final short[][] test_board_short, final int depth, final short turn, final String move){
		// Recursive function which will create a complete game tree up to a certain depth, then search the tree for good moves
		short test_board_temp[][] = new short[p_column_count][p_row_count];		//paranoid sanitation of references, can probably remove for performance boost
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				test_board_temp[col_iter][row_iter] = test_board_short[col_iter][row_iter];
			}
		}		
		BoardState current_board = new BoardState(test_board_temp);	
		
		if(depth <= 0 || current_board.compute_win() != p_empty_space_number){
			switch(p_heuristic){
			case 1:
				return evaluate_board_one(current_board);
			case 2:
				return evaluate_board_two(current_board);
			case 3:
				return evaluate_board_three(current_board, move);
			case 101:
				return evaluate_move_one(move);
			case 102:
				return evaluate_move_two(current_board, move);
			default:
				return evaluate_board_three(current_board, move);
			}			
		}
		
		
		short alpha = 0;
		if(p_player_number == turn){
			alpha = 20000;
		}
		else if(p_computer_number == turn){
			alpha = -20000;
		}
		else{
			System.err.println("Minimax is unsure of whose turn it is!");
			return 0;
		}
		
		
		final String valid_next_moves[] = current_board.get_available_moves(turn);
		for(int i = 0; i < valid_next_moves.length; i++){
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
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
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));	
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
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
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
			}
			else{
				//this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
				return 0;
			}
			alpha += 0;
		}
		return (short) (alpha-2);
	}
	
	

}
