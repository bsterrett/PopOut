package popout.search;

import popout.board.BoardState;

public abstract class Search {

	protected BoardState p_board;
	protected final short p_player_number;
	protected final short p_computer_number;
	protected final short p_empty_space_number;
	protected final int p_column_count;
	protected final int p_row_count;
	
	public Search(BoardState board){
		p_board = board;
		p_empty_space_number = 0;
		p_player_number = 1;
		p_computer_number = 2;
		p_column_count = p_board.get_state().length;
		p_row_count = p_board.get_state()[0].length;
		
	}
	
	public void make_next_move(){
		//this should do something if called by a particular search algorithm
		System.err.println("Called generic Search.make_next_move(), need to specify search type!");
	}
	
	protected final short evaluate_board_one(final BoardState target_board){
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
	
	protected final short evaluate_board_two(final BoardState target_board){
		
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
	
	protected final short evaluate_board_three(final BoardState target_board, final String move){
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_4 = 100;
		final int three_in_a_row = 3;
		final int scary_loss_factor = 0;
		short utility = 0;
		
		//check up and left for 4 in a row
		for(int col = 3; col < column_count; col++){
			for(int row = 0; row < row_count-3; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] &&
						board[col][row] == board[col-3][row+3] ){
					utility += (board[col][row] == p_computer_number ? connect_4  : -1 * connect_4 );
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
					utility += (board[col][row] == p_computer_number ? connect_4 : -1 * connect_4 );
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
					utility += (board[col][row] == p_computer_number ? connect_4 : -1 * connect_4 );
				}
			}
		}
		
		//check up and left for 3 in a row
		for(int col = 2; col < column_count; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] ){
					utility += (board[col][row] == p_computer_number ? three_in_a_row : -1 * three_in_a_row);
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
					utility += (board[col][row] == p_computer_number ? three_in_a_row : -1 * three_in_a_row);
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
	
	protected final short evaluate_move_one(final String move){
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
	
	protected final short evaluate_move_two(final BoardState target_board, final String move){
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
	
	protected final short fake_utility(int debug_leaf){
		debug_leaf += 0;
		switch(debug_leaf){
		case 0:
			return 5;
		case 1:
			return 6;
		case 2:
			return 7;
		case 3:
			return 4;
		case 4:
			return 5;
		case 5:
			return 3;
		case 6:
			return 6;
		case 7:
			return 6;
		case 8:
			return 9;
		case 9:
			return 7;
		case 10:
			return 5;
		case 11:
			return 9;
		case 12:
			return 8;
		case 13:
			return 6;
		default:
			return -1;
		}
	}
}


