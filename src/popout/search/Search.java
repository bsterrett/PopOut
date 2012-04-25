package popout.search;

import popout.PlayerNum;
import popout.BoardSize;
import popout.board.BoardState;
import popout.board.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public class Search extends RecursiveAction {

	protected BoardState p_board;
	protected Random p_random;
	protected final short p_heuristic_num;
	protected final short p_depth;
	private static final long serialVersionUID = 1337L;

	public Search(BoardState board) {
		p_board = board;
		p_random = new Random(System.nanoTime());
		p_heuristic_num = 5;
		p_depth = 8;
	}
	
	public void compute(){
		
	}

	public void get_computer_move() {

	}
	public Move[] get_unordered_moves(BoardState input_board, final short player){
		int length = input_board.get_moves(player).length;
		Move move_list[] = new Move[length];
		for(int i = 0; i < length; i++){
			move_list[i] = input_board.get_moves(player)[i];
		}
		return move_list;
	}
	
	public Move[] get_cheap_ordered_moves(BoardState input_board, final short player){
		int valid_move_count = 0;
		int best_order[] = new int[7];
		best_order[0] = 3;
		best_order[1] = 4;
		best_order[2] = 2;
		best_order[3] = 1;
		best_order[4] = 5;
		best_order[5] = 6;
		best_order[6] = 0;
		for (int i = 0; i < 7; i++) {
			if (input_board.valid_drop(best_order[i])) valid_move_count++;
			if (input_board.valid_pop(best_order[i], player)) valid_move_count++;
		}
		Move move_list[] = new Move[valid_move_count];
		int move_write_count = 0;
		for (int i = 0; i < 7; i++) {
			if (input_board.valid_drop(best_order[i])) move_list[move_write_count++] = new Move(Move.DROP, best_order[i]);
		}
		for (int i = 0; i < 7; i++) {
			if (input_board.valid_pop(best_order[i], player)) move_list[move_write_count++] = new Move(Move.POP, best_order[i]);
		}
		return move_list;
	}
	
	public Move[] get_heuristic_ordered_moves(BoardState input_board, final short player){
		BoardState board = new BoardState(input_board.get_state());
		
		
		ArrayList<Move> moves = new ArrayList<Move>(Arrays.asList(get_cheap_ordered_moves(input_board, player)));
		ArrayList<Integer> move_utilities = new ArrayList<Integer>();
		
		for(int i = 0; i < moves.size(); i++){
			final short temp_board[][] = board.get_state();
			Move move = moves.get(i);
			board.make_move(move, player);
			move.utility = evaluate_board_four_lite(board, move);
			int temp_score = 0;
			board.set_state(temp_board);
			move_utilities.add(temp_score);
		}
		Move move_list[] = new Move[moves.size()];
		int move_counter = 0;
		while(moves.size() > 0){
			int best_move_so_far = 0;
			for(int i = 1; i < moves.size(); i++){
				if(PlayerNum.COMPUTER == player && move_utilities.get(i) > move_utilities.get(best_move_so_far)){
					best_move_so_far = i;
				}
				else if(PlayerNum.HUMAN == player && move_utilities.get(i) < move_utilities.get(best_move_so_far)){
					best_move_so_far = i;
				}
			}
			move_list[move_counter++] = moves.get(best_move_so_far);
			move_utilities.remove(best_move_so_far);
			moves.remove(best_move_so_far);
		}		
		return move_list;
	}
	
	public final short evaluate_board(final BoardState current_board, final Move move) {
		return evaluate_board(current_board, move, p_heuristic_num);
	}

	public final short evaluate_board(final BoardState current_board, final Move move, final int heuristic) {
		switch (heuristic) {
		case 1:
			return evaluate_board_one(current_board);
		case 2:
			return evaluate_board_two(current_board);
		case 3:
			return evaluate_board_three(current_board);
		case 4:
			return evaluate_board_four(current_board, move);
		case 5:
			return evaluate_board_five(current_board, move);
		case 101:
			return evaluate_move_one(move);
		case 102:
			return evaluate_move_two(current_board, move);
		default:
			return evaluate_board_five(current_board, move);
		}
	}

	public final short evaluate_board_one(final BoardState target_board) {
		// Returns a poorly adjusted utility for the computer player
		// 20 for computer win, -20 for player win
		// 5, 10, 15 for 1, 2, 3 three-in-a-rows respectively

		// This sucks, dont use it

		short current_winner = target_board.compute_win();
		if (PlayerNum.HUMAN == current_winner)
			return -20;
		if (PlayerNum.COMPUTER == current_winner)
			return 20;
		final short board[][] = target_board.get_state();
		short positive_board_utility = 0;
		short negative_board_utility = 0;
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				final short compare_against = board[col][row];
				if (PlayerNum.HUMAN == compare_against) {
					// compute the utility of this position if owned by the
					// player
					if (col >= 2 && row <= BoardSize.ROW_COUNT - 3
							&& board[col - 1][row + 1] == compare_against
							&& board[col - 2][row + 1] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (row <= BoardSize.ROW_COUNT - 3
							&& board[col][row + 1] == compare_against
							&& board[col][row + 2] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (col <= BoardSize.COLUMN_COUNT - 3 && row <= BoardSize.ROW_COUNT - 3
							&& board[col + 1][row + 1] == compare_against
							&& board[col + 2][row + 2] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
					if (col <= BoardSize.COLUMN_COUNT - 3
							&& board[col + 1][row] == compare_against
							&& board[col + 2][row] == compare_against) {
						negative_board_utility += negative_board_utility < -10 ? 0
								: -5;
					}
				}
				if (PlayerNum.COMPUTER == compare_against) {
					// compute the utility of this position if owned by the
					// computer
					if (col >= 2 && row <= BoardSize.ROW_COUNT - 3
							&& board[col - 1][row + 1] == compare_against
							&& board[col - 2][row + 1] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (row <= BoardSize.ROW_COUNT - 3
							&& board[col][row + 1] == compare_against
							&& board[col][row + 2] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (col <= BoardSize.COLUMN_COUNT - 3 && row <= BoardSize.ROW_COUNT - 3
							&& board[col + 1][row + 1] == compare_against
							&& board[col + 2][row + 2] == compare_against) {
						positive_board_utility += positive_board_utility > 10 ? 0
								: 5;
					}
					if (col <= BoardSize.COLUMN_COUNT - 3
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

		// Don't use this, its bad!

		short current_winner = target_board.compute_win();
		if (PlayerNum.HUMAN == current_winner)	return -19000;
		if (PlayerNum.COMPUTER == current_winner) return 19000;
		final short board[][] = target_board.get_state();
		short utility = 0;

		// iterate over all columns, looking for 3 in a row with an empty space
		// on top
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row + 3] == PlayerNum.EMPTY_SPACE) {
					utility += (board[col][row] == PlayerNum.HUMAN ? -12 : 12);
				}
			}
		}

		// iterate over all rows, looking for 3 in a row with an empty space OR
		// a chip that could be popped to make a connect 4
		for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
			for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
				if (BoardSize.ROW_COUNT - 1 > row
						&& board[col][row] != PlayerNum.EMPTY_SPACE
						&& ((board[col][row] == board[col + 1][row]
								&& board[col][row] == board[col + 2][row] && (board[col + 3][row] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col + 3][row + 1]))
								|| (board[col][row] == board[col + 1][row]
										&& board[col][row] == board[col + 3][row] && (board[col + 2][row] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col + 2][row + 1])) || (board[col][row] == board[col + 3][row]
								&& board[col][row] == board[col + 2][row] && (board[col + 1][row] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col + 1][row + 1])))) {
					// this could be broken up to look for a good pop and a good
					// drop separately
					// could increase the efficacy of the evaluation function
					utility += (board[col][row] == PlayerNum.HUMAN ? -14 : 14);
				} else if (BoardSize.ROW_COUNT - 1 == row
						&& board[col][row] != PlayerNum.EMPTY_SPACE
						&& ((board[col][row] == board[col + 1][row]
								&& board[col][row] == board[col + 2][row] && board[col + 3][row] == PlayerNum.EMPTY_SPACE)
								|| (board[col][row] == board[col + 1][row]
										&& board[col][row] == board[col + 3][row] && board[col + 2][row] == PlayerNum.EMPTY_SPACE) || (board[col][row] == board[col + 3][row]
								&& board[col][row] == board[col + 2][row] && board[col + 1][row] == PlayerNum.EMPTY_SPACE))) {
					utility += (board[col][row] == PlayerNum.HUMAN ? -12 : 12);
				}
			}
		}

		// iterate over all left-up diagonals
		for (int col = 3; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& (board[col][row] == board[col - 1][row + 1]
								&& board[col][row] == board[col - 2][row + 2]
								&& (board[col - 3][row + 3] == PlayerNum.EMPTY_SPACE || (BoardSize.ROW_COUNT - 4 > row && board[col - 3][row + 4] == board[col][row]))
								|| board[col][row] == board[col - 1][row + 1]
								&& board[col][row] == board[col - 3][row + 3]
								&& (board[col - 2][row + 2] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col - 2][row + 3]) || board[col][row] == board[col - 2][row + 2]
								&& board[col][row] == board[col - 3][row + 3]
								&& (board[col - 1][row + 1] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col - 1][row + 2]))) {
					utility += (board[col][row] == PlayerNum.HUMAN ? -14 : 14);
				}
			}
		}

		// iterate over all right-up diagonals
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& (board[col][row] == board[col + 1][row + 1]
								&& board[col][row] == board[col + 2][row + 2]
								&& (board[col + 3][row + 3] == PlayerNum.EMPTY_SPACE || (BoardSize.ROW_COUNT - 4 > row && board[col + 3][row + 4] == board[col][row]))
								|| board[col][row] == board[col + 1][row + 1]
								&& board[col][row] == board[col + 3][row + 3]
								&& (board[col + 2][row + 2] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col + 2][row + 3]) || board[col][row] == board[col + 2][row + 2]
								&& board[col][row] == board[col + 3][row + 3]
								&& (board[col + 1][row + 1] == PlayerNum.EMPTY_SPACE || board[col][row] == board[col + 1][row + 2]))) {
					utility += (board[col][row] == PlayerNum.HUMAN ? -14 : 14);
				}
			}
		}

		return utility;
	}

	public final short evaluate_board_three(final BoardState target_board) {
		final short board[][] = target_board.get_state();
		final int connect_4 = 100;
		final int three_in_a_row = 3;
		short utility = 0;

		// check up and left for 4 in a row
		for (int col = 3; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row

		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 2; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? three_in_a_row
							: -1 * three_in_a_row);
				}
			}
		}
		return utility;
	}

	public final short evaluate_board_four(final BoardState target_board, final Move move) {
		final short board[][] = target_board.get_state();
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);

		// using move utility instead of 0 to start with
		short utility = evaluate_move_two(target_board, move);

		// check up and left for 5 in a row
		for (int col = 4; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]
						&& board[col][row] == board[col - 4][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight up for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]
						&& board[col][row] == board[col][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and right for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 4; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]
						&& board[col][row] == board[col + 4][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight right for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 4; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]
						&& board[col][row] == board[col + 4][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and left for 4 in a row
		for (int col = 3; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 2; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 2; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}
		return utility;
	}
	
	public final short evaluate_board_four_lite(final BoardState target_board, final Move move) {
		final short board[][] = target_board.get_state();
		final int connect_4 = 100;

		// using move utility instead of 0 to start with
		short utility = (short) (evaluate_move_one(move) + evaluate_move_two(target_board, move));

		// check up and left for 4 in a row
		for (int col = 3; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}
		return utility;
	}
	
	public final short evaluate_board_five(final BoardState target_board,final Move move) {
		final short board[][] = target_board.get_state();
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);
		final int empty_space = 2;

		// using move utility instead of 0 to start with
		short utility = evaluate_move_two(target_board, move);
		
		for(int col = 0; col < BoardSize.COLUMN_COUNT; col++){
			for(int row = 0; row < BoardSize.ROW_COUNT; row++){
				if(PlayerNum.EMPTY_SPACE == board[col][row]){
					utility += empty_space;
				}
			}
		}

		// check up and left for 5 in a row
		for (int col = 4; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]
						&& board[col][row] == board[col - 4][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight up for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]
						&& board[col][row] == board[col][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and right for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 4; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 4; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]
						&& board[col][row] == board[col + 4][row + 4]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check straight right for 5 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 4; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]
						&& board[col][row] == board[col + 4][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_5
							: -1 * connect_5);
				}
			}
		}

		// check up and left for 4 in a row
		for (int col = 3; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]
						&& board[col][row] == board[col - 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check straight up for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row] == board[col][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]
						&& board[col][row] == board[col + 3][row + 3]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check right for 4 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col][row] == board[col + 3][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_4
							: -1 * connect_4);
				}
			}
		}

		// check up and left for 3 in a row
		for (int col = 2; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col - 1][row + 1]
						&& board[col][row] == board[col - 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check straight up for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check up and right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 2; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 2; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row + 1]
						&& board[col][row] == board[col + 2][row + 2]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}

		// check right for 3 in a row
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 2; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]) {
					utility += (board[col][row] == PlayerNum.COMPUTER ? connect_3
							: -1 * connect_3);
				}
			}
		}
		return utility;
	}

	public final short evaluate_move_one(final Move move) {
		// This is only valid for 7 column boards
		// This was designed for Connect 4, not Pop Out
		// In fact, this is probably a horrible heuristic for Pop Out
		switch (move.col) {
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

	public final short evaluate_move_two(final BoardState target_board, final Move move) {
		// This gives small points for drops which will allow for a pop in the
		// future
		// or for pops which will not prevent a pop in the future.

		// Since target_board already has the move applied, this will check for
		// an empty space in the second-lowest row, not lowest row
		if (PlayerNum.EMPTY_SPACE == target_board.get_state()[move.col][1]
				&& Move.DROP == move.type) {
			// The computer must have just put its chip in board[move_col][0]
			return 2;
		} else if (Move.POP == move.type) {
			if (PlayerNum.COMPUTER == target_board.get_state()[move.col][1]) {
				// This is a somewhat safe pop because it will allow for another
				// pop in the future
				return 1;
			} else if (PlayerNum.HUMAN == target_board.get_state()[move.col][1]) {
				// This is not a safe pop because it allows the opposing player
				// the ability to pop this column
				return -1;
			}
		}
		return 0;
	}
	

}
