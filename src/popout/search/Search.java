package popout.search;

import popout.PlayerNum;
import popout.board.BoardState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public abstract class Search extends RecursiveAction {

	protected BoardState p_board;
	protected final short p_player_number;
	protected final short p_computer_number;
	protected final short p_empty_space_number;
	protected final int p_column_count;
	protected final int p_row_count;
	protected Random p_random;
	protected final short p_heuristic_num;
	private static final long serialVersionUID = 1337L;

	public Search(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number) {
		p_board = board;
		p_empty_space_number = empty_space_number;
		p_player_number = player_number;
		p_computer_number = computer_number;
		p_column_count = p_board.get_state().length;
		p_row_count = p_board.get_state()[0].length;
		p_random = new Random(System.nanoTime());
		p_heuristic_num = 5;
	}

	public void get_computer_move() {
		// this should do something if called by a particular search algorithm
		System.err
				.println("Called generic Search.make_next_move(), need to specify search type!");
	}
	
	public final String[] get_heuristic_ordered_moves(final BoardState input_board, final short player){
		BoardState board = new BoardState(input_board.get_state());
		final short temp_board[][] = board.get_state();
		
		ArrayList<String> moves = new ArrayList<String>(Arrays.asList(board.get_available_moves(player)));
		ArrayList<Integer> move_utilities = new ArrayList<Integer>();
		
		for(int i = 0; i < moves.size(); i++){
			String move = moves.get(i);
			int move_col = Integer.parseInt(move.substring(2));
			int temp_score = 0;			
			if ('D' == move.charAt(0)) {
				// this move is a drop				
				board.drop(move_col, player);
				temp_score = evaluate_board_four_lite(board);
			} else if ('P' == move.charAt(0)) {
				// this move is a pop
				board.pop(move_col, player);	
				temp_score = evaluate_board_four_lite(board);
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + move);
			}
			board.set_state(temp_board);
			move_utilities.add(temp_score);
		}		
		String ordered_moves[] = new String[moves.size()];
		int move_counter = 0;
		while(moves.size() > 0){
			int best_move_so_far = 0;
			for(int i = 0; i < moves.size(); i++){
				if(move_utilities.get(i) >= move_utilities.get(best_move_so_far)){
					best_move_so_far = i;
				}
			}
			ordered_moves[move_counter++] = moves.get(best_move_so_far);
			move_utilities.remove(best_move_so_far);
			moves.remove(best_move_so_far);
		}		
		return ordered_moves;
	}
	
	public final short evaluate_board(final BoardState current_board,
			final String move) {
		return evaluate_board(current_board, move, p_heuristic_num);
	}

	public final short evaluate_board(final BoardState current_board,
			final String move, final int heuristic) {
		switch (heuristic) {
		case 1:
			return evaluate_board_one(current_board);
		case 2:
			return evaluate_board_two(current_board);
		case 3:
			return evaluate_board_three(current_board, move);
		case 4:
			return evaluate_board_four(current_board, move);
		case 5:
			return evaluate_board_five(current_board, move);
		case 101:
			return evaluate_move_one(move);
		case 102:
			return evaluate_move_two(current_board, move);
		default:
			return evaluate_board_four(current_board, move);
		}
	}

	public final short evaluate_board_one(final BoardState target_board) {
		// Returns a poorly adjusted utility for the computer player
		// 20 for computer win, -20 for player win
		// 5, 10, 15 for 1, 2, 3 three-in-a-rows respectively

		// This sucks, dont use it

		short current_winner = target_board.compute_win();
		if (p_player_number == current_winner)
			return -20;
		if (p_computer_number == current_winner)
			return 20;
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		short positive_board_utility = 0;
		short negative_board_utility = 0;
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count; row++) {
				final short compare_against = board[col][row];
				if (p_player_number == compare_against) {
					// compute the utility of this position if owned by the
					// player
					if (col >= 2 && row <= row_count - 3
							&& board[col - 1][row + 1] == compare_against
							&& board[col - 2][row + 1] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (row <= row_count - 3
							&& board[col][row + 1] == compare_against
							&& board[col][row + 2] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (col <= column_count - 3 && row <= row_count - 3
							&& board[col + 1][row + 1] == compare_against
							&& board[col + 2][row + 2] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (col <= column_count - 3
							&& board[col + 1][row] == compare_against
							&& board[col + 2][row] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
				}
				if (p_computer_number == compare_against) {
					// compute the utility of this position if owned by the
					// computer
					if (col >= 2 && row <= row_count - 3
							&& board[col - 1][row + 1] == compare_against
							&& board[col - 2][row + 1] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (row <= row_count - 3
							&& board[col][row + 1] == compare_against
							&& board[col][row + 2] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (col <= column_count - 3 && row <= row_count - 3
							&& board[col + 1][row + 1] == compare_against
							&& board[col + 2][row + 2] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (col <= column_count - 3
							&& board[col + 1][row] == compare_against
							&& board[col + 2][row] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
				}
				// more than 3 three-in-a-rows is not a significant strategic
				// advantage
				if (positive_board_utility - negative_board_utility >= 30)
					return (short) (positive_board_utility + negative_board_utility);
			}
		}
		return (short) (positive_board_utility + negative_board_utility);
	}

	public final short evaluate_board_two(final BoardState target_board) {

		// Dont use this, its bad!

		short current_winner = target_board.compute_win();
		if (p_player_number == current_winner)
			return -19000;
		if (p_computer_number == current_winner)
			return 19000;
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		short utility = 0;

		// iterate over all columns, looking for 3 in a row with an empty space
		// on top
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row + 3] == p_empty_space_number) {
					utility += (board[col][row] == p_player_number ? -12 : 12);
				}
			}
		}

		// iterate over all rows, looking for 3 in a row with an empty space OR
		// a chip that could be popped to make a connect 4
		for (int row = 0; row < row_count; row++) {
			for (int col = 0; col < column_count - 3; col++) {
				if (row_count - 1 > row
						&& board[col][row] != p_empty_space_number
						&& ((board[col][row] == board[col + 1][row]
								&& board[col][row] == board[col + 2][row] && (board[col + 3][row] == p_empty_space_number || board[col][row] == board[col + 3][row + 1]))
								|| (board[col][row] == board[col + 1][row]
										&& board[col][row] == board[col + 3][row] && (board[col + 2][row] == p_empty_space_number || board[col][row] == board[col + 2][row + 1])) || (board[col][row] == board[col + 3][row]
								&& board[col][row] == board[col + 2][row] && (board[col + 1][row] == p_empty_space_number || board[col][row] == board[col + 1][row + 1])))) {
					// this could be broken up to look for a good pop and a good
					// drop separately
					// could increase the efficacy of the evaluation function
					utility += (board[col][row] == p_player_number ? -14 : 14);
				} else if (row_count - 1 == row
						&& board[col][row] != p_empty_space_number
						&& ((board[col][row] == board[col + 1][row]
								&& board[col][row] == board[col + 2][row] && board[col + 3][row] == p_empty_space_number)
								|| (board[col][row] == board[col + 1][row]
										&& board[col][row] == board[col + 3][row] && board[col + 2][row] == p_empty_space_number) || (board[col][row] == board[col + 3][row]
								&& board[col][row] == board[col + 2][row] && board[col + 1][row] == p_empty_space_number))) {
					utility += (board[col][row] == p_player_number ? -12 : 12);
				}
			}
		}

		// iterate over all left-up diagonals
		for (int col = 3; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& (board[col][row] == board[col - 1][row + 1]
								&& board[col][row] == board[col - 2][row + 2]
								&& (board[col - 3][row + 3] == p_empty_space_number || (row_count - 4 > row && board[col - 3][row + 4] == board[col][row]))
								|| board[col][row] == board[col - 1][row + 1]
								&& board[col][row] == board[col - 3][row + 3]
								&& (board[col - 2][row + 2] == p_empty_space_number || board[col][row] == board[col - 2][row + 3]) || board[col][row] == board[col - 2][row + 2]
								&& board[col][row] == board[col - 3][row + 3]
								&& (board[col - 1][row + 1] == p_empty_space_number || board[col][row] == board[col - 1][row + 2]))) {
					utility += (board[col][row] == p_player_number ? -14 : 14);
				}
			}
		}

		// iterate over all right-up diagonals
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& (board[col][row] == board[col + 1][row + 1]
								&& board[col][row] == board[col + 2][row + 2]
								&& (board[col + 3][row + 3] == p_empty_space_number || (row_count - 4 > row && board[col + 3][row + 4] == board[col][row]))
								|| board[col][row] == board[col + 1][row + 1]
								&& board[col][row] == board[col + 3][row + 3]
								&& (board[col + 2][row + 2] == p_empty_space_number || board[col][row] == board[col + 2][row + 3]) || board[col][row] == board[col + 2][row + 2]
								&& board[col][row] == board[col + 3][row + 3]
								&& (board[col + 1][row + 1] == p_empty_space_number || board[col][row] == board[col + 1][row + 2]))) {
					utility += (board[col][row] == p_player_number ? -14 : 14);
				}
			}
		}

		return utility;
	}

	public final short evaluate_board_three(final BoardState target_board,
			final String move) {
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_4 = 100;
		final int three_in_a_row = 3;
		short utility = 0;

		// check up and left for 4 in a row
		for (int col = 3; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row

		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == p_computer_number ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < column_count - 2; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == p_computer_number ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}
		return utility;
	}

	public final short evaluate_board_four(final BoardState target_board,
			final String move) {
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);

		// using move utility instead of 0 to start with
		short utility = evaluate_move_two(target_board, move);

		// check up and left for 5 in a row
		for (int col = 4; col < column_count; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]
						&& board[col][row] == board[col - 4][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight up for 5 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]
						&& board[col][row] == board[col][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and right for 5 in a row
		for (int col = 0; col < column_count - 4; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]
						&& board[col][row] == board[col + 4][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight right for 5 in a row
		for (int col = 0; col < column_count - 4; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]
						&& board[col][row] == board[col + 4][row]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and left for 4 in a row
		for (int col = 3; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < column_count - 2; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < column_count - 2; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}
		return utility;
	}
	
	public final short evaluate_board_four_lite(final BoardState target_board) {
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_4 = 100;

		// using move utility instead of 0 to start with
		short utility = 0;

		// check up and left for 4 in a row
		for (int col = 3; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}
		return utility;
	}
	
	public final short evaluate_board_five(final BoardState target_board,
			final String move) {
		final short board[][] = target_board.get_state();
		final int column_count = board.length;
		final int row_count = board[0].length;
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);
		final int empty_space = 2;

		// using move utility instead of 0 to start with
		short utility = evaluate_move_two(target_board, move);
		
		for(int col = 0; col < column_count; col++){
			for(int row = 0; row < row_count; row++){
				if(board[col][row] == p_empty_space_number){
					utility += empty_space;
				}
			}
		}

		// check up and left for 5 in a row
		for (int col = 4; col < column_count; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]
						&& board[col][row] == board[col - 4][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight up for 5 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]
						&& board[col][row] == board[col][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and right for 5 in a row
		for (int col = 0; col < column_count - 4; col++) {
			for (int row = 0; row < row_count - 4; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]
						&& board[col][row] == board[col + 4][row + 4]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight right for 5 in a row
		for (int col = 0; col < column_count - 4; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]
						&& board[col][row] == board[col + 4][row]) {
					utility += (board[col][row] == p_computer_number ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and left for 4 in a row
		for (int col = 3; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count - 3; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < column_count - 3; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == p_computer_number ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < column_count; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < column_count - 2; col++) {
			for (int row = 0; row < row_count - 2; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < column_count - 2; col++) {
			for (int row = 0; row < row_count; row++) {
				if (board[col][row] != p_empty_space_number
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == p_computer_number ? connect_3
							: -1 * connect_3);
				}
			}
		}
		return utility;
	}

	public final short evaluate_move_one(final String move) {
		// This is only valid for 7 column boards
		// This was designed for Connect 4, not Pop Out
		// In fact, this is probably a horrible heuristic for Pop Out
		int move_col = Integer.parseInt(move.substring(2));
		switch (move_col) {
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
			System.err
					.println("Tried to evaluate a move into an invalid column!");
			return 0;
		}
	}

	public final short evaluate_move_two(final BoardState target_board,
			final String move) {
		// This gives small points for drops which will allow for a pop in the
		// future
		// or for pops which will not prevent a pop in the future.

		// Since target_board already has the move applied, this will check for
		// an empty space in the second-lowest row, not lowest row
		int move_col = Integer.parseInt(move.substring(2));
		if (p_empty_space_number == target_board.get_state()[move_col][1]
				&& 'D' == move.charAt(0)) {
			// The computer must have just put its chip in board[move_col][0]
			return 2;
		} else if ('P' == move.charAt(0)) {
			if (p_computer_number == target_board.get_state()[move_col][1]) {
				// This is a somewhat safe pop because it will allow for another
				// pop in the future
				return 1;
			} else if (p_player_number == target_board.get_state()[move_col][1]) {
				// This is not a safe pop because it allows the opposing player
				// the ability to pop this column
				return -1;
			}
		}
		return 0;
	}
	

}
