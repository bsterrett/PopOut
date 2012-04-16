package popout.board;

public class BoardState {

	protected short[][] p_current_state;
	protected final int p_column_count;
	protected final int p_row_count;

	public BoardState(int board_height, int board_width) {
		p_column_count = board_width;
		p_row_count = board_height;
		p_current_state = new short[p_column_count][p_row_count];
		for (int col = 0; col < p_column_count; col++) {
			for (int row = 0; row < p_row_count; row++) {
				p_current_state[col][row] = 0;
			}
		}
	}

	public BoardState(short[][] board_state) {
		p_column_count = board_state.length;
		p_row_count = board_state[0].length;
		p_current_state = board_state;
	}

	public boolean valid_drop(final int col) {
		return (col >= 0 && col <= p_column_count - 1 && 0 == p_current_state[col][p_row_count - 1]);
	}

	public boolean drop(final int col, short player) {
		if (valid_drop(col)) {
			// this column has an empty space and can be played
			for (int row = 0; row < p_row_count; row++) {
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
		return (col >= 0 && col <= p_column_count - 1 && player == p_current_state[col][0]);
	}

	public boolean pop(final int col, final short player) {
		if (valid_pop(col, player)) {
			for (int row = 0; row < (p_row_count - 1); row++) {
				// drops chip from space above into current space, going from
				// bottom to top
				p_current_state[col][row] = p_current_state[col][row + 1];
			}
			p_current_state[col][p_row_count - 1] = 0;
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
		final short[][] return_state = new short[p_column_count][p_row_count];
		for (int col = 0; col < p_column_count; col++) {
			for (int row = 0; row < p_row_count; row++) {
				return_state[col][row] = p_current_state[col][row];
			}
		}
		return return_state;
	}

	public short compute_win() {
		short connect_4s_by_number[] = new short[10];
		for (int col = 0; col < p_column_count; col++) {
			for (int row = 0; row < p_row_count; row++) {
				final short compare_against = p_current_state[col][row];
				if (0 != compare_against) {
					if (col >= 3
							&& row <= p_row_count - 4
							&& p_current_state[col - 1][row + 1] == compare_against
							&& p_current_state[col - 2][row + 2] == compare_against
							&& p_current_state[col - 3][row + 3] == compare_against) {
						// check for win by going diagonally up and left
						connect_4s_by_number[compare_against] += 1;
					}
					if (row <= p_row_count - 4
							&& p_current_state[col][row + 1] == compare_against
							&& p_current_state[col][row + 2] == compare_against
							&& p_current_state[col][row + 3] == compare_against) {
						// check for win by going straight up
						connect_4s_by_number[compare_against] += 1;
					}
					if (col <= p_column_count - 4
							&& row <= p_row_count - 4
							&& p_current_state[col + 1][row + 1] == compare_against
							&& p_current_state[col + 2][row + 2] == compare_against
							&& p_current_state[col + 3][row + 3] == compare_against) {
						// check for win by going diagonally up and right
						connect_4s_by_number[compare_against] += 1;
					}
					if (col <= p_column_count - 4
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
			else if(0 != winner && connect_4s_by_number[i] == connect_4s_by_number[winner]){
				tied = i;
			}
		}
		return (short) (tied*10+winner);
		
	}

	public final String[] get_available_moves(final short player) {
		// returns a list of available moves that the next player can make
		int valid_move_count = 0;
		for (int i = 0; i < p_column_count; i++) {
			if (valid_pop(i, player))
				valid_move_count++;
			if (valid_drop(i))
				valid_move_count++;
		}
		String move_list[] = new String[valid_move_count];
		int move_write_count = 0;
		for (int i = 0; i < p_column_count; i++) {
			if (valid_pop(i, player))
				move_list[move_write_count++] = "P " + String.valueOf(i);
			if (valid_drop(i))
				move_list[move_write_count++] = "D " + String.valueOf(i);
		}
		return move_list;
	}

	public final String[] get_cheap_ordered_available_moves(final short player) {
		// This only works if there are 7 columns!
		if (p_column_count != 7) {
			System.err
					.println("Tried to get an ordered list of moves with a strange board size!");
			return get_available_moves(player);
		} else {
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
				if (valid_drop(best_order[i]))
					valid_move_count++;
				if (valid_pop(best_order[i], player))
					valid_move_count++;
			}
			String move_list[] = new String[valid_move_count];
			int move_write_count = 0;
			for (int i = 0; i < 7; i++) {
				if (valid_drop(best_order[i]))
					move_list[move_write_count++] = "D "
							+ String.valueOf(best_order[i]);
			}
			for (int i = 0; i < 7; i++) {
				if (valid_pop(best_order[i], player))
					move_list[move_write_count++] = "P "
							+ String.valueOf(best_order[i]);
			}
			return move_list;
		}
	}
	
	public final String[] get_expensive_ordered_available_moves(final short player){
		final int connect_4 = 100;
		final int pop_4 = 5;
		final int drop_4 = 5;
		
		int pop_interest[] = new int[p_column_count];
		for(int col = 0; col < p_column_count; col++){
			pop_interest[col] = 0;
		}
		
		int drop_interest[] = new int[p_column_count];
		for(int col = 0; col < p_column_count; col++){
			drop_interest[col] = 0;
		}
		
		for(int col = 0; col < p_column_count; col++){
			for(int row = 0; row < p_row_count; row++){
				if(p_current_state[col][row] != 0){
					//checking up and left
					if(	col >= 3 && row <= p_row_count - 4
							&& p_current_state[col][row] == p_current_state[col-1][row+1]
							&& p_current_state[col][row] == p_current_state[col-2][row+2] ){
						if(p_current_state[col][row] == p_current_state[col-3][row+3]){
							//currently a connect 4
						}
						else if( row <= p_row_count-5 && p_current_state[col][row] == p_current_state[col-3][row+4]
									&& p_current_state[col][row] == p_current_state[col-3][0]){
							//can pop for a connect 4
							pop_interest[col-3] += (p_current_state[col][row] == player ? pop_4 : -1 * pop_4 );
						}
						else if( p_current_state[col-3][row+2] != 0 && p_current_state[col-3][row+3] == 0){
							//can drop for a connect 4
							drop_interest[col-3] += (p_current_state[col][row] == player ? drop_4 : -1 * drop_4 );
						}
					}
									
					//checking straight up
					if( row <= p_row_count - 4
							&& p_current_state[col][row] == p_current_state[col][row+1]
							&& p_current_state[col][row] == p_current_state[col][row+2] ){
						if(p_current_state[col][row] == p_current_state[col][row+3]){
							//currently a connect 4
						}
						else if(p_current_state[col][row+3] == 0){
							//can drop for a connect 4
							drop_interest[col] += (p_current_state[col][row] == player ? drop_4 : -1 * drop_4 );
						}
					}
					
					//checking up and right
					if( col <= p_column_count - 4 && row <= p_row_count - 4
							&& p_current_state[col][row] == p_current_state[col+1][row+1]
							&& p_current_state[col][row] == p_current_state[col+2][row+2] ){
						if(p_current_state[col][row] == p_current_state[col+3][row+3]){
							//currently a connect 4
						}
						else if( row <= p_row_count-5 && p_current_state[col][row] == p_current_state[col+3][row+4]
									&& p_current_state[col][row] == p_current_state[col+3][0]){
							//can pop for a connect 4
							pop_interest[col+3] += (p_current_state[col][row] == player ? pop_4 : -1 * pop_4 );
						}
						else if( p_current_state[col+3][row+2] != 0 && p_current_state[col+3][row+3] == 0){
							//can drop for a connect 4
							drop_interest[col+3] += (p_current_state[col][row] == player ? drop_4 : -1 * drop_4 );
						}						
					}
					
					//checking straight right
					if( col <= p_column_count - 4
							&& p_current_state[col][row] == p_current_state[col+1][row]
							&& p_current_state[col][row] == p_current_state[col+2][row] ){
						if(p_current_state[col][row] == p_current_state[col+3][row]){
							//currently a connect 4
						}
						else if( row <= p_row_count-2 && p_current_state[col][row] == p_current_state[col+3][row+1]
									&& p_current_state[col][row] == p_current_state[col+3][0]){
							//can pop for a connect 4
							pop_interest[col+3] += (p_current_state[col][row] == player ? pop_4 : -1 * pop_4 );
						}
						else if( row >= 1 && p_current_state[col+3][row-1] != 0 && p_current_state[col+3][row] == 0){
							//can drop for a connect 4
							drop_interest[col+3] += (p_current_state[col][row] == player ? drop_4 : -1 * drop_4 );
						}
					}
				}				
			}
		}
		final int available_move_count = get_available_moves(player).length; 
		String move_list[] = new String[available_move_count];
		int move_list_counter = 0;
		int last_best_drop = Integer.MAX_VALUE;
		int last_best_pop = Integer.MAX_VALUE;
		while(move_list_counter < available_move_count){
			int next_best_drop = Integer.MIN_VALUE;
			for(int col = 0; col < p_column_count; col++){
				if(drop_interest[col] >= next_best_drop && drop_interest[col] <= last_best_drop){
					next_best_drop = col;
				}
			}
			int next_best_pop = Integer.MIN_VALUE;
			for(int col = 0; col < p_column_count; col++){
				if(pop_interest[col] >= next_best_pop && pop_interest[col] <= last_best_pop){
					next_best_pop = col;
				}
			}
		}
		
		return get_cheap_ordered_available_moves(player);
	}

}
