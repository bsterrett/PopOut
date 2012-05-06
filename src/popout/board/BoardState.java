package popout.board;

import popout.BoardSize;
import popout.PlayerNum;

public class BoardState {

	protected short[][] p_current_state;

	public BoardState() {
		p_current_state = new short[BoardSize.COLUMN_COUNT][BoardSize.ROW_COUNT];
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				p_current_state[col][row] = PlayerNum.EMPTY_SPACE;
			}
		}
	}

	public BoardState(short[][] board_state) {
		p_current_state = board_state;
	}
	
	public boolean make_move(Move input_move){
		if(Move.DROP == input_move.type){
			return drop(input_move.col, input_move.player);
		}
		else if(Move.POP == input_move.type){
			return pop(input_move.col, input_move.player);
		}
		else return false;
	}

	public boolean valid_drop(final int col) {
		return (col >= 0 && col <= BoardSize.COLUMN_COUNT - 1 && PlayerNum.EMPTY_SPACE == p_current_state[col][BoardSize.ROW_COUNT - 1]);
	}

	private boolean drop(final int col, short player) {
		if (valid_drop(col)) {
			// this column has an empty space and can be played
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				// search up from the bottom to find where the chip will go
				if (0 == p_current_state[col][row]) {
					p_current_state[col][row] = player;
					break;
				}
			}
			return true;
		} else {
			// this column is full and can only be popped!
			System.err.println("You cannot drop into that column!");
			return false;
		}
	}

	public boolean valid_pop(final int col, final short player) {
		return (col >= 0 && col <= BoardSize.COLUMN_COUNT - 1 && player == p_current_state[col][0]);
	}

	private boolean pop(final int col, final short player) {
		if (valid_pop(col, player)) {
			for (int row = 0; row < (BoardSize.ROW_COUNT - 1); row++) {
				// drops chip from space above into current space, going from
				// bottom to top
				p_current_state[col][row] = p_current_state[col][row + 1];
			}
			p_current_state[col][BoardSize.ROW_COUNT - 1] = PlayerNum.EMPTY_SPACE;
			return true;
		} else {
			// this column is empty and cannot be popped!
			System.err.println("You cannot pop from that column!");
			return false;
		}
	}

	public void set_state(short[][] board_state) {
		// ONLY FOR USE BY SEARCH FUNCTION
		p_current_state = board_state;
	}

	public short[][] get_state() {
		final short[][] return_state = new short[BoardSize.COLUMN_COUNT][BoardSize.ROW_COUNT];
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				return_state[col][row] = p_current_state[col][row];
			}
		}
		return return_state;
	}

	public short compute_win() {
		short connect_4s_by_number[] = new short[10];	//this isn't initialized to 0, should it be?
		for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
			for (int row = 0; row < BoardSize.ROW_COUNT; row++) {
				final short compare_against = p_current_state[col][row];
				if (PlayerNum.EMPTY_SPACE != compare_against) {
					if (col >= 3
							&& row <= BoardSize.ROW_COUNT - 4
							&& p_current_state[col - 1][row + 1] == compare_against
							&& p_current_state[col - 2][row + 2] == compare_against
							&& p_current_state[col - 3][row + 3] == compare_against) {
						// check for win by going diagonally up and left
						connect_4s_by_number[compare_against] += 1;
					}
					if (row <= BoardSize.ROW_COUNT - 4
							&& p_current_state[col][row + 1] == compare_against
							&& p_current_state[col][row + 2] == compare_against
							&& p_current_state[col][row + 3] == compare_against) {
						// check for win by going straight up
						connect_4s_by_number[compare_against] += 1;
					}
					if (col <= BoardSize.COLUMN_COUNT - 4
							&& row <= BoardSize.ROW_COUNT - 4
							&& p_current_state[col + 1][row + 1] == compare_against
							&& p_current_state[col + 2][row + 2] == compare_against
							&& p_current_state[col + 3][row + 3] == compare_against) {
						// check for win by going diagonally up and right
						connect_4s_by_number[compare_against] += 1;
					}
					if (col <= BoardSize.COLUMN_COUNT - 4
							&& p_current_state[col + 1][row] == compare_against
							&& p_current_state[col + 2][row] == compare_against
							&& p_current_state[col + 3][row] == compare_against) {
						// check for win by going straight right
						connect_4s_by_number[compare_against] += 1;
					}
				}
			}
		}
		
		short winner = 0;
		short tied = 0;
		for(short i = 0; i < 10; i++){
			if(connect_4s_by_number[i] > connect_4s_by_number[winner]){
				winner = i;
				tied = 0;
			}
			else if(PlayerNum.EMPTY_SPACE != winner && connect_4s_by_number[i] == connect_4s_by_number[winner]){
				tied = i;
			}
		}
		if(tied != 0){
			return winner;
		}
		else{
			return PlayerNum.TIE;
		}
		
	}
	
	public Move[] get_moves(final short player){
		int valid_move_count = 0;
		for (int i = 0; i < BoardSize.COLUMN_COUNT; i++) {
			if (valid_pop(i, player)) valid_move_count++;
			if (valid_drop(i)) valid_move_count++;
		}
		Move available_moves[] = new Move[valid_move_count];
		int move_write_count = 0;
		for(int i = 0; i < BoardSize.COLUMN_COUNT; i++){
			if(valid_drop(i)) available_moves[move_write_count++] = new Move(Move.DROP, i, player);
			if(valid_pop(i, player)) available_moves[move_write_count++] = new Move(Move.POP, i, player);			
		}
		valid_move_count = 0;
		return available_moves;
	}
}
