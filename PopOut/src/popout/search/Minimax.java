package popout.search;

import popout.board.BoardState;
//import popout.ui.CLDisplay;

public class Minimax extends Search {
	
	protected final short p_heuristic;
	protected final short p_depth;
	
	public Minimax(BoardState board){
		super(board);
		p_heuristic = 2;
		p_depth = 4;
	}
	
	public Minimax(BoardState board, short depth, short heuristic){
		super(board);
		p_heuristic = heuristic;
		p_depth = depth;
	}
	
	public void make_next_move(){
		// Call this to make the computer move
		short depth = p_depth;
		short alpha = -30000;
		int best_move = -1;		
		short current_board_short[][] = new short[p_column_count][p_row_count];
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				current_board_short[col_iter][row_iter] = p_board.get_state()[col_iter][row_iter];
			}
		}
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board.get_available_moves(p_computer_number);		
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
				//CLDisplay C = new CLDisplay(current_board.get_state()); //for debugging, get rid of this
				//System.out.println(C.toString());  //for debugging, get rid of this
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical drop
				short temp_score = minimax(next_board, depth-1, p_player_number, valid_next_moves[i]);
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
				//CLDisplay C = new CLDisplay(current_board.get_state());  //for debugging, get rid of this
				//System.out.println(C.toString());  //for debugging, get rid of this
				short next_board[][] = current_board.get_state();
				current_board.set_state(temp_board);
				// recursively call minimax() for this hypothetical pop
				short temp_score = minimax(next_board, depth-1, p_player_number, valid_next_moves[i]);
				if(temp_score >= alpha){				
					alpha = temp_score;
					best_move = i;
				}
			}
			else{
				//this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
			}
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
	}
	
	private short minimax(final short[][] test_board_short, final int depth, final short turn, final String move){
		// Recursive function which will create a complete game tree up to a certain depth, then search the tree for good moves
		short test_board_temp[][] = new short[p_column_count][p_row_count];		//paranoid sanitation of references, can probably remove for performance boost
		for(int col_iter = 0; col_iter < p_column_count; col_iter++){
			for(int row_iter = 0; row_iter < p_row_count; row_iter++){
				test_board_temp[col_iter][row_iter] = test_board_short[col_iter][row_iter];
			}
		}		
		BoardState current_board = new BoardState(test_board_temp);	
		
		
		if(p_computer_number == current_board.compute_win()){
			return 19001;
		}
		if(p_player_number == current_board.compute_win()){
			return -19001;
		}
		
		if(depth <= 0){
			switch(p_heuristic){
			case 1:
				return evaluate_move_one(move);
			case 2:
				return evaluate_board_two(current_board);
			default:
				return evaluate_board_one(current_board);
			}
			
		}
		
		
		short alpha = 0;
		if(p_player_number == turn){
			alpha = 30000;
		}
		else if(p_computer_number == turn){
			alpha = -30000;
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
				short temp_board[][] = new short[7][6];
				for(int k = 0; k < 7; k++){
					for(int j = 0; j < 6; j++){
						temp_board[k][j] = current_board.get_state()[k][j];
					}
				}
				
				short next_board[][] = current_board.get_state();
				current_board.drop(move_col, turn);
				//CLDisplay C = new CLDisplay(next_board);
				current_board.set_state(temp_board);
				//recursive call
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i]);
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
				if(alpha > 15){
					//System.out.println(C.toString());
					alpha += 0;		//for debugging, please remove
				}
				
						
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				short temp_board[][] = new short[7][6];
				for(int k = 0; k < 7; k++){
					for(int j = 0; j < 6; j++){
						temp_board[k][j] = current_board.get_state()[k][j];
					}
				}
				current_board.pop(move_col,p_computer_number);
				short next_board[][] = current_board.get_state();
				//CLDisplay C = new CLDisplay(next_board);
				current_board.set_state(temp_board);
				//recursive call
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number), valid_next_moves[i] );
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
				if(alpha > 15){
					//System.out.println(C.toString());
					alpha += 0;		//for debugging, please remove
				}
			}
			else{
				//this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
				return 0;
			}
		}
		return (short) (alpha+0);
	}
	
	
	private short evaluate_board_one(final BoardState target_board){
		//Returns a poorly adjusted utility for the computer player
		// 20 for computer win, -20 for player win
		// 5, 10, 15   for    1, 2, 3   three-in-a-rows   respectively
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
	
	private short evaluate_board_two(final BoardState target_board){
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
	
	private short evaluate_move_one(final String move){
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
}
