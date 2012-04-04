package popout.search;

import popout.board.BoardState;

public class Minimax extends Search {
	
	protected final short p_heuristic;
	protected final short p_depth;
	
	public Minimax(BoardState board){
		super(board);
		p_heuristic = 0;
		p_depth = 5;
	}
	
	public Minimax(BoardState board, short depth, short heuristic){
		super(board);
		p_heuristic = heuristic;
		p_depth = depth;
	}
	
	public void make_next_move(){
		short depth = p_depth;
		short alpha = -200;
		int best_move = -1;
		BoardState current_board = new BoardState(p_board.get_state());
		final String valid_next_moves[] = current_board.get_available_moves();
		for(int i = 0; i < valid_next_moves.length; i++){
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				current_board.drop(move_col, p_computer_player);
				short next_board[][] = current_board.get_state();
				short temp_score = minimax(next_board, depth-1, p_player_number);
				if(temp_score >= alpha){				
					alpha = temp_score;
					best_move = i;
				}
						
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				current_board.pop(move_col);
				short next_board[][] = current_board.get_state();
				short temp_score = minimax(next_board, depth-1, p_player_number);
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
	}
	
	private short minimax(final short[][] test_board_short, final int depth, final short turn){
		BoardState current_board = new BoardState(test_board_short);		
		if(depth <= 0){
			return evaluate_board(current_board);
		}
		
		
		short alpha = 0;
		if(p_player_number == turn){
			alpha = 200;
		}
		else if(p_computer_number == turn){
			alpha = -200;
		}
		else{
			System.err.println("Minimax is unsure of whose turn it is!");
			return 0;
		}
		
		
		final String valid_next_moves[] = current_board.get_available_moves();
		for(int i = 0; i < valid_next_moves.length; i++){
			if('D' == valid_next_moves[i].charAt(0)){
				//this move is a drop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				current_board.drop(move_col, turn);
				short next_board[][] = current_board.get_state();
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number) );
				alpha = (short) (turn == p_player_number ? Math.min(alpha, temp_score) : Math.max(alpha, temp_score));
						
			}
			else if('P' == valid_next_moves[i].charAt(0)){
				//this move is a pop
				int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
				current_board.pop(move_col);
				short next_board[][] = current_board.get_state();
				short temp_score = minimax(next_board, depth-1, (turn == p_player_number ? p_computer_number : p_player_number) );
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
	
	
	private short evaluate_board(final BoardState target_board){
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
				if(positive_board_utility - negative_board_utility > 30) return (short) (positive_board_utility - negative_board_utility);
			}
		}
		return (short) (positive_board_utility - negative_board_utility);
	}
	
}
