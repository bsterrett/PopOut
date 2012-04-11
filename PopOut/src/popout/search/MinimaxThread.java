package popout.search;

import popout.board.BoardState;
import popout.search.Search;

public class MinimaxThread extends Thread {
	
	private final short[][] p_test_board_short;
	private final int p_depth;
	private final short p_turn;
	private final String p_move;
	private final int p_column_count;
	private final int p_row_count;
	private final short p_empty_space_number;
	private final short p_player_number;
	private final short p_computer_number;
	private short p_alpha;
	
	
	public MinimaxThread(	final short empty_space_number, final short player_number, final short computer_number,
							final short[][] test_board_short, final int depth, final short turn, final String move){
		p_test_board_short = test_board_short;
		p_column_count = test_board_short.length;
		p_row_count = test_board_short[0].length;
		p_empty_space_number = empty_space_number;
		p_player_number = player_number;
		p_computer_number = computer_number;
		p_depth = depth;
		p_turn = turn;
		p_move = move;
		p_alpha = (short) (turn == computer_number ? -20000 : 20000 );
	}

	public void run() {
		p_alpha = minimax(p_test_board_short, p_depth, p_turn, p_move);
	}
	
	public final short get_alpha(){
		return p_alpha;
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
/*			switch(p_heuristic){
			case 1:
				return Search.evaluate_board_one(current_board);
			case 2:
				return Search.evaluate_board_two(current_board);
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
			}*/
			System.err.println("Need to make that static evaluation function or something!");
		}		

		short alpha = 0;
		if(		p_player_number == turn)	alpha = 20000;
		else if(p_computer_number == turn)	alpha = -20000;
		else{
			System.err.println("Minimax is unsure of whose turn it is!");
			return 0;
		}


		final String valid_next_moves[] = current_board.get_available_moves(turn);
		//final String valid_next_moves[] = current_board.fake_next_moves(debug_node++, turn);
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
			alpha += 0;
		}
		return alpha;
	}	

}
