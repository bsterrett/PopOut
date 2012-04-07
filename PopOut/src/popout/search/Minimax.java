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
		final short minimax_outputs[] = new short[valid_next_moves.length];
		int output_counter = 0;
		for(int i = 0; i < valid_next_moves.length; i++){
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				
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
				minimax_outputs[output_counter++] = temp_score;
				if(temp_score >= alpha){				
					alpha = temp_score;
					best_move = i;
				}
						
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
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
				minimax_outputs[output_counter++] = temp_score;
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
			System.out.print(valid_next_moves[i] + " " +  minimax_outputs[i] + "    ");
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
		
		if(depth <= 0){
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
		
		
		final String valid_next_moves[] = current_board.get_available_moves(p_computer_number);
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
				current_board.pop(move_col,p_computer_number);
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
		}
		return alpha;
	}
	
	
	private final short evaluate_board_one(final BoardState target_board){
		//Returns a poorly adjusted utility for the computer player
		// 20 for computer win, -20 for player win
		// 5, 10, 15   for    1, 2, 3   three-in-a-rows   respectively
		
		// This sucks, dont use it
		
		short current_winner = target_board.compute_win();
		if(p_player_number == current_winner) return -20;
		if(p_computer_number == current_winner) return 20;
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		short positive_board_utility = 0;
		short negative_board_utility = 0;
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count; row++){
				final short compare_against = board[col][row];
				if(p_player_number == compare_against){
					//compute the utility of this position if owned by the player
					if(		col >= 2 && row <=row_count-3 &&
							board[col-1][row+1] == compare_against &&
							board[col-2][row+1] == compare_against){
						negative_board_utility += negative_board_utility < -10 ? 0 : -5;
					}
					if(		row <= row_count-3 &&
							board[col][row+1] == compare_against &&
							board[col][row+2] == compare_against){
						negative_board_utility += negative_board_utility < -10 ? 0 : -5;
					}
					if(		col <= column_count-3 && row <= row_count-3 &&
							board[col+1][row+1] == compare_against &&
							board[col+2][row+2] == compare_against){
						negative_board_utility += negative_board_utility < -10 ? 0 : -5;
					}
					if(		col <= column_count-3 &&
							board[col+1][row] == compare_against &&
							board[col+2][row] == compare_against){
						negative_board_utility += negative_board_utility < -10 ? 0 : -5;
					}
				}
				if(p_computer_number == compare_against){
					//compute the utility of this position if owned by the computer
					if(		col >= 2 && row <=row_count-3 &&
							board[col-1][row+1] == compare_against &&
							board[col-2][row+1] == compare_against){
						positive_board_utility += positive_board_utility > 10 ? 0 : 5;
					}
					if(		row <= row_count-3 &&
							board[col][row+1] == compare_against &&
							board[col][row+2] == compare_against){
						positive_board_utility += positive_board_utility > 10 ? 0 : 5;
					}
					if(		col <= column_count-3 && row <= row_count-3 &&
							board[col+1][row+1] == compare_against &&
							board[col+2][row+2] == compare_against){
						positive_board_utility += positive_board_utility > 10 ? 0 : 5;
					}
					if(		col <= column_count-3 &&
							board[col+1][row] == compare_against &&
							board[col+2][row] == compare_against){
						positive_board_utility += positive_board_utility > 10 ? 0 : 5;
					}
				}
				// more than 3 three-in-a-rows is not a significant strategic advantage
				if(positive_board_utility - negative_board_utility >= 30) return (short) (positive_board_utility + negative_board_utility);
			}
		}
		return (short) (positive_board_utility + negative_board_utility);
	}
	
	private final short evaluate_board_two(final BoardState target_board){
		
		// Dont use this, its bad!
		
		short current_winner = target_board.compute_win();
		if(p_player_number == current_winner) return -19000;
		if(p_computer_number == current_winner) return 19000;
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		short utility = 0;
		
		//iterate over all columns, looking for 3 in a row with an empty space on top
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col][row+1] && 
						board[col][row] == board[col][row+2] && 
						board[col][row+3] == p_empty_space_number){
					utility += (board[col][row] == p_player_number ? -12 : 12);
				}
			}
		}
		
		//iterate over all rows, looking for 3 in a row with an empty space OR a chip that could be popped to make a connect 4
		for(int row = 0; row < row_count; row++){
			for(int col = 0; col < column_count-3; col++){
				if(		row_count-1 > row && board[col][row] != p_empty_space_number &&
						((board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] &&
						(board[col+3][row] == p_empty_space_number || board[col][row] == board[col+3][row+1]))
						||
						(board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+3][row] &&
						(board[col+2][row] == p_empty_space_number || board[col][row] == board[col+2][row+1]))						
						||
						(board[col][row] == board[col+3][row] &&
						board[col][row] == board[col+2][row] &&
						(board[col+1][row] == p_empty_space_number || board[col][row] == board[col+1][row+1])))){
					//this could be broken up to look for a good pop and a good drop separately
					//could increase the efficacy of the evaluation function
					utility += (board[col][row] == p_player_number ?  -14 : 14);
				}
				else if(row_count-1 == row && board[col][row] != p_empty_space_number &&
						((board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] &&
						board[col+3][row] == p_empty_space_number)
						||
						(board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+3][row] &&
						board[col+2][row] == p_empty_space_number)
						||
						(board[col][row] == board[col+3][row] &&
						board[col][row] == board[col+2][row] &&
						board[col+1][row] == p_empty_space_number))){
					utility += (board[col][row] == p_player_number ? -12 : 12);
				}
			}
		}
		
		//iterate over all left-up diagonals
		for(int col = 3; col < column_count; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						(board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] &&
						(board[col-3][row+3] == p_empty_space_number ||
						(row_count-4 > row && board[col-3][row+4] == board[col][row]))
						||
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-3][row+3] &&
						(board[col-2][row+2] == p_empty_space_number || board[col][row] == board[col-2][row+3])
						||
						board[col][row] == board[col-2][row+2] &&
						board[col][row] == board[col-3][row+3] &&
						(board[col-1][row+1] == p_empty_space_number || board[col][row] == board[col-1][row+2]))){
					utility += (board[col][row] == p_player_number ? -14 : 14);
				}
			}
		}
		
		//iterate over all right-up diagonals
		for(int col = 0; col < column_count-3; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						(board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+2][row+2] &&
						(board[col+3][row+3] == p_empty_space_number ||
						(row_count-4 > row && board[col+3][row+4] == board[col][row]))
						||
						board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+3][row+3] &&
						(board[col+2][row+2] == p_empty_space_number || board[col][row] == board[col+2][row+3])
						||
						board[col][row] == board[col+2][row+2] &&
						board[col][row] == board[col+3][row+3] &&
						(board[col+1][row+1] == p_empty_space_number || board[col][row] == board[col+1][row+2]))){
					utility += (board[col][row] == p_player_number ? -14 : 14);
				}
			}
		}

		return utility;
	}
	
	private final short evaluate_board_three(final BoardState target_board, final String move){
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_4 = 200;
		final int three_in_a_row = 3;
		short utility = 0;
		
		int diagonal_bonus_offset = 0; 	//highly experimental, turn to 0 in case of bad stuff
		
		//check up and left for 4 in a row
		for(int col = 3; col < column_count; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] &&
						board[col][row] == board[col-3][row+3] ){
					utility += (board[col][row] == p_computer_number ? connect_4 + diagonal_bonus_offset : -1 * connect_4 + diagonal_bonus_offset);
				}
			}
		}
		
		//check straight up for 4 in a row
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col][row+1] &&
						board[col][row] == board[col][row+2] &&
						board[col][row] == board[col][row+3] ){
					utility += (board[col][row] == p_computer_number ? connect_4 : -1 * connect_4 );
				}
			}
		}
		
		//check up and right for 4 in a row
		for(int col = 0; col < column_count-3; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+2][row+2] &&
						board[col][row] == board[col+3][row+3] ){
					utility += (board[col][row] == p_computer_number ? connect_4 + diagonal_bonus_offset : -1 * connect_4 + diagonal_bonus_offset);
				}
			}
		}
		
		//check right for 4 in a row
		
		for(int col = 0; col < column_count-3; col++){
			for(int row = 0; row < row_count; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] &&
						board[col][row] == board[col+3][row] ){
					utility += (board[col][row] == p_computer_number ? connect_4 : -1 * connect_4);
				}
			}
		}
		
		//check up and left for 3 in a row
		for(int col = 2; col < column_count; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] ){
					utility += (board[col][row] == p_computer_number ? three_in_a_row + diagonal_bonus_offset : -1 * three_in_a_row);
				}
			}
		}
		
		//check straight up for 3 in a row
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col][row+1] &&
						board[col][row] == board[col][row+2] ){
					utility += (board[col][row] == p_computer_number ? three_in_a_row : -1 * three_in_a_row);
				}
			}
		}
		
		//check up and right for 3 in a row
		for(int col = 0; col < column_count-2; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+2][row+2] ){
					utility += (board[col][row] == p_computer_number ? three_in_a_row + diagonal_bonus_offset : -1 * three_in_a_row);
				}
			}
		}
		
		//check right for 3 in a row
		for(int col = 0; col < column_count-3; col++){
			for(int row = 0; row < row_count; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] ){
					utility += (board[col][row] == p_computer_number ? three_in_a_row : -1 * three_in_a_row);
				}
			}
		}		
		return utility;
	}
	
	private final short evaluate_move_one(final String move){
		// This is only valid for 7 column boards
		// This was designed for Connect 4, not Pop Out
		// In fact, this is probably a horrible heuristic for Pop Out
		int move_col = Integer.parseInt(move.substring(2));
		switch(move_col){
		case 0:
			return 3;
		case 1:
			return 4;
		case 2:
			return 5;
		case 3:
			return 7;
		case 4:
			return 5;
		case 5:
			return 4;
		case 6:
			return 3;
		default:
			System.err.println("Tried to evaluate a move into an invalid column!");
			return 0;			
		}
	}
	
	private final short evaluate_move_two(final BoardState target_board, final String move){
		//This gives small points for drops which will allow for a pop in the future
		//or for pops which will not prevent a pop in the future.
		
		//Since target_board already has the move applied, this will check for an empty space in the second-lowest row, not lowest row
		
		int move_col = Integer.parseInt(move.substring(2));
		if(		p_empty_space_number == target_board.get_state()[move_col][1] &&
				'D' == move.charAt(0)){
			//The computer must have just put its chip in board[move_col][0]
			return 2;
		}
		else if('P' == move.charAt(0)){
			if(		p_computer_number == target_board.get_state()[move_col][1]){
				//This is a somewhat safe pop because it will allow for another pop in the future
				return 1;
			}
			else if(p_player_number == target_board.get_state()[move_col][1]){
				//This is not a safe pop because it allows the opposing player the ability to pop this column
				return -1;
			}
		}
		return 0;
	}
}
