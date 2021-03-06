package popout.search;

import popout.PlayerNum;
import popout.BoardSize;
import popout.board.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Search extends Thread {

	protected BoardState p_board;
	protected final short p_heuristic_num;
	protected final short p_depth;
	//private static final long serialVersionUID = 1337L;
	protected Move p_stashed_move;
	protected boolean p_interrupted = false;

	public Search(BoardState board) {
		p_board = board;
		p_heuristic_num = 5;
		p_depth = 9;
		p_stashed_move = new Move(-1, -1, (short) -1);
	}
	
	public Search(BoardState board, short depth){
		p_board = board;
		p_heuristic_num = 5;
		p_depth = depth;
		p_stashed_move = new Move(-1, -1, (short) -1);
	}
		
	public void interrupt(){
		p_interrupted = true;
	}
	
	public Move get_stashed_move(){
		return p_stashed_move;
	}

	public Move get_computer_move() {
		return null;
	}
	
	public void make_computer_move(){
		p_board.make_move(get_computer_move());
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
			if (input_board.valid_drop(best_order[i])) move_list[move_write_count++] = new Move(Move.DROP, best_order[i], player);
		}
		for (int i = 0; i < 7; i++) {
			if (input_board.valid_pop(best_order[i], player)) move_list[move_write_count++] = new Move(Move.POP, best_order[i], player);
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
			board.make_move(move);
			move.utility = evaluate_board_four_lite(board, move);
			int temp_score = 0;
			board.set_state(temp_board);
			move_utilities.add(temp_score);
		}
		if(PlayerNum.COMPUTER == player){
			Collections.sort(moves, new MoveReverseComparator());
		}
		else if(PlayerNum.HUMAN == player){
			Collections.sort(moves, new MoveComparator());
		}
		Move move_list[] = new Move[moves.size()];
		int move_counter = 0;
		for(int i = 0; i < moves.size(); i++){
			move_list[move_counter++] = moves.get(i);
		}
		return move_list;
	}
	
	public final short evaluate_board(final BoardState current_board, final Move move) {
		return evaluate_board(current_board, move, p_heuristic_num);
	}

	public final short evaluate_board(final BoardState current_board, final Move move, final int heuristic) {
		switch (heuristic) {
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

	private final short evaluate_board_four(final BoardState target_board, final Move move) {
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
	
	private final short evaluate_board_four_lite(final BoardState target_board, final Move move) {
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
	
	private final short evaluate_board_five(final BoardState target_board, final Move move) {
		final short board[][] = target_board.get_state();
		final int connect_3 = 3;
		final int connect_4 = 100;
		final int connect_5 = (short) (-1 * connect_4 + 10);
		//final int empty_space = 2;
		final int scary_loss = 0; //(move.player == PlayerNum.COMPUTER ? -10 : 0 );
		

		// using move utility instead of 0 to start with
		short utility = 0;// evaluate_move_two(target_board, move);
		
/*		for(int col = 0; col < BoardSize.COLUMN_COUNT; col++){
			for(int row = 0; row < BoardSize.ROW_COUNT; row++){
				if(PlayerNum.EMPTY_SPACE == board[col][row]){
					utility += empty_space;
				}
			}
		}*/

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
							: -1 * connect_4 + scary_loss);
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
							: -1 * connect_4 + scary_loss);
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
							: -1 * connect_4 + scary_loss);
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
							: -1 * connect_4 + scary_loss);
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

	private short evaluate_board_six(final BoardState target_board, final Move move){
		final short board[][] = target_board.get_state();
		int computer_connect_4_forks = 0;
		int player_connect_4_forks = 0;
		
		
		short utility = 0;
		
		//testing straight up for any drop forks
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT - 3; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE
						&& board[col][row] == board[col][row + 1]
						&& board[col][row] == board[col][row + 2]
						&& board[col][row + 3] == PlayerNum.EMPTY_SPACE) {
					if(board[col][row] == PlayerNum.COMPUTER) computer_connect_4_forks += 1;
					else player_connect_4_forks += 1;
				}
			}
		}
		
		//testing straight right for any drop forks
		for (int col = 0; col < BoardSize.COLUMN_COUNT - 3; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE &&
						(board[col][row] == board[col + 1][row]
						&& board[col][row] == board[col + 2][row]
						&& board[col + 3][row] == PlayerNum.EMPTY_SPACE
						&& (row == 0 || board[col + 3][row - 1] != PlayerNum.EMPTY_SPACE))
						||
						(board[col][row] == board[col + 1][row]
						&& board[col + 2][row] == PlayerNum.EMPTY_SPACE
						&& (row == 0 || board[col + 3][row - 1] != PlayerNum.EMPTY_SPACE)
						&& board[col][row] == board[col + 3][row])) {
					if(board[col][row] == PlayerNum.COMPUTER) computer_connect_4_forks += 1;
					else player_connect_4_forks += 1;
				}
			}
		}
		
		//testing straight left for any drop forks
		for (int col = BoardSize.COLUMN_COUNT-1; col > 2; col--) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				if (board[col][row] != PlayerNum.EMPTY_SPACE &&
						(board[col][row] == board[col - 1][row]
						&& board[col][row] == board[col - 2][row]
						&& board[col - 3][row] == PlayerNum.EMPTY_SPACE
						&& (row == 0 || board[col - 3][row - 1] != PlayerNum.EMPTY_SPACE))
						||
						(board[col][row] == board[col - 1][row]
						&& board[col - 2][row] == PlayerNum.EMPTY_SPACE
						&& (row == 0 || board[col - 3][row - 1] != PlayerNum.EMPTY_SPACE)
						&& board[col][row] == board[col - 3][row])) {
					if(board[col][row] == PlayerNum.COMPUTER) computer_connect_4_forks += 1;
					else player_connect_4_forks += 1;
				}
			}
		}
		
		
		return utility;
	}
	
	private final short evaluate_move_one(final Move move) {
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

	private final short evaluate_move_two(final BoardState target_board, final Move move) {
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
