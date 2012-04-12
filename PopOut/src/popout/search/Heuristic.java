package popout.search;

import popout.board.BoardState;

public final class Heuristic extends Thread{
	protected final short p_empty_space_number;
	protected final short p_player_number;
	protected final short p_computer_number;
	protected final int p_heuristic_number;
	protected final BoardState p_target_board;
	protected final String p_move;
	protected short p_alpha;
	
	Heuristic(final short empty_space_number, final short player_number, final short computer_number, final int heuristic_number){
		p_empty_space_number = empty_space_number;
		p_player_number = player_number;
		p_computer_number = computer_number;
		p_heuristic_number = heuristic_number;
		p_target_board = null;
		p_move = null;
		p_alpha = 0;
	}
	
	Heuristic(final short empty_space_number, final short player_number, final short computer_number, final int heuristic_number,
				final short[][] target_board, final String move){
		p_empty_space_number = empty_space_number;
		p_player_number = player_number;
		p_computer_number = computer_number;
		p_heuristic_number = heuristic_number;
		p_target_board = new BoardState(target_board);
		p_move = move;
		p_alpha = 0;
	}
	
	public void run(){
		if(null != p_target_board && null != p_move) p_alpha = evaluate_board(p_target_board, p_move);
		else{
			System.err.println("Tried to evaluate a board which was null!");
		}
	}
	
	public final short get_alpha(){
		return p_alpha;
	}
	
	public final short evaluate_board(final BoardState current_board, final String move){
		return evaluate_board(current_board, move, p_heuristic_number);
	}
	
	public final short evaluate_board(final BoardState current_board, final String move, final int heuristic){
		switch(heuristic){
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
	
	public final short evaluate_board_one(final BoardState target_board){
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
	
	public final short evaluate_board_two(final BoardState target_board){
		
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
	
	public final short evaluate_board_three(final BoardState target_board, final String move){
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_4 = 100;
		final int three_in_a_row = 3;
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
	
	public final short evaluate_board_four(final BoardState target_board, final String move){
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);
		
		//using move utility instead of 0 to start with
		short utility = evaluate_move_two(target_board, move);
		
		//check up and left for 5 in a row
		for(int col = 4; col < column_count; col++){
			for(int row = 0; row < row_count-4; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col-1][row+1] &&
						board[col][row] == board[col-2][row+2] &&
						board[col][row] == board[col-3][row+3] &&
						board[col][row] == board[col-4][row+4] ){
					utility += (board[col][row] == p_computer_number ? connect_5 : -1 * connect_5 );
				}
			}
		}
		
		//check straight up for 5 in a row
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count-4; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col][row+1] &&
						board[col][row] == board[col][row+2] &&
						board[col][row] == board[col][row+3] &&
						board[col][row] == board[col][row+4] ){
					utility += (board[col][row] == p_computer_number ? connect_5 : -1 * connect_5 );
				}
			}
		}
		
		//check up and right for 5 in a row
		for(int col = 0; col < column_count-4; col++){
			for(int row = 0; row < row_count-4; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+2][row+2] &&
						board[col][row] == board[col+3][row+3] &&
						board[col][row] == board[col+4][row+4] ){
					utility += (board[col][row] == p_computer_number ? connect_5 : -1 * connect_5 );
				}
			}
		}
		
		//check straight right for 5 in a row
		for(int col = 0; col < column_count-4; col++){
			for(int row = 0; row < row_count; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] &&
						board[col][row] == board[col+3][row] &&
						board[col][row] == board[col+4][row] ){
					utility += (board[col][row] == p_computer_number ? connect_5 : -1 * connect_5 );
				}
			}
		}
		
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
					utility += (board[col][row] == p_computer_number ? connect_3 : -1 * connect_3);
				}
			}
		}
		
		//check straight up for 3 in a row
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col][row+1] &&
						board[col][row] == board[col][row+2] ){
					utility += (board[col][row] == p_computer_number ? connect_3 : -1 * connect_3);
				}
			}
		}
		
		//check up and right for 3 in a row
		for(int col = 0; col < column_count-2; col++){
			for(int row = 0; row < row_count-2; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row+1] &&
						board[col][row] == board[col+2][row+2] ){
					utility += (board[col][row] == p_computer_number ? connect_3 : -1 * connect_3);
				}
			}
		}
		
		//check right for 3 in a row
		for(int col = 0; col < column_count-2; col++){
			for(int row = 0; row < row_count; row++){
				if(		board[col][row] != p_empty_space_number &&
						board[col][row] == board[col+1][row] &&
						board[col][row] == board[col+2][row] ){
					utility += (board[col][row] == p_computer_number ? connect_3 : -1 * connect_3);
				}
			}
		}
		return utility;
	}
	
	public final short evaluate_move_one(final String move){
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
	
	public final short evaluate_move_two(final BoardState target_board, final String move){
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