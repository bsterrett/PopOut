package popout.board;

public class BoardState {	

	protected short[][] p_current_state;
	protected short p_moves_so_far;	
	protected final int p_column_count;
	protected final int p_row_count;
	
	public BoardState(int board_height, int board_width){
		p_column_count = board_width;
		p_row_count = board_height;
		p_current_state = new short[p_column_count][p_row_count];
		for(int col = 0; col < p_column_count; col++){
			for(int row = 0; row < p_row_count; row++){
				p_current_state[col][row] = 0;
			}
		}
	}
	
	public BoardState(short[][] board_state){
		p_column_count = board_state.length;
		p_row_count = board_state[0].length;
		p_current_state = board_state;
	}
	
	public boolean valid_drop(final int col){
		return (col >= 0 && col <= p_column_count-1 && 0 == p_current_state[col][p_row_count-1]);
	}
	
	public boolean drop(final int col, short player){
		if(valid_drop(col)){
			//this column has an empty space and can be played
			for(int row = 0; row < p_row_count; row++){
				//search up from the bottom to find where the chip will go
				if(0 == p_current_state[col][row]){
					p_current_state[col][row] = player;
					break;
				}
			}
			p_moves_so_far++;
			return true;
		}
		else{
			//this column is full and can only be popped!
			System.err.println("Trying to drop in a full column!");
			return false;
		}
	}
	
	public boolean valid_pop(final int col, final short player){
		return (col >=0 && col <= p_column_count-1 && player == p_current_state[col][0]);
	}
	
	public boolean pop(final int col, final short player){
		if(valid_pop(col,player)){
			for(int row = 0; row < (p_row_count-1); row++){
				//drops chip from space above into current space, going from bottom to top
				p_current_state[col][row] = p_current_state[col][row+1];
			}
			p_current_state[col][p_row_count-1] = 0;
			p_moves_so_far++;
			return true;
		}
		else{
			//this column is empty and cannot be popped!
			System.err.println("Trying to pop an empty column!");
			return false;
		}
	}
	
	public void set_state(short[][] board_state){
		// ONLY FOR USE BY SEARCH FUNCTION 
		p_current_state = board_state;
	}

	public short[][] get_state() {
		return p_current_state;
	}
	
	public short get_moves_so_far(){
		return p_moves_so_far;
	}
	
	public short compute_win(){
		//WARNING: THIS CANNOT RECOGNIZE TWO SIMULTANEOUS WINNERS!
		for(int col = 0; col < p_column_count; col++){
			for(int row = 0; row < p_row_count; row++){
				final short compare_against = p_current_state[col][row];
				if(0 != compare_against){
					if(		col >= 3 && row <= p_row_count-4 &&
							p_current_state[col-1][row+1] == compare_against && 
							p_current_state[col-2][row+2] == compare_against && 
							p_current_state[col-3][row+3] == compare_against){
						//check for win by going diagonally up and left
						return compare_against;
					}
					if(		row <= p_row_count-4 &&
							p_current_state[col][row+1] == compare_against &&
							p_current_state[col][row+2] == compare_against &&
							p_current_state[col][row+3] == compare_against){
						//check for win by going straight up
						return compare_against;
					}
					if(		col <= p_column_count-4 && row <= p_row_count-4 &&
							p_current_state[col+1][row+1] == compare_against &&
							p_current_state[col+2][row+2] == compare_against &&
							p_current_state[col+3][row+3] == compare_against){
						//check for win by going diagonally up and right
						return compare_against;
					}
					if(		col <= p_column_count-4 &&
							p_current_state[col+1][row] == compare_against &&
							p_current_state[col+2][row] == compare_against &&
							p_current_state[col+3][row] == compare_against){
						//check for win by going straight right
						return compare_against;
					}
				}
			}
		}
		return 0;
	}
	
	
	
	public String[] get_available_moves(short player){
		//returns a list of available moves that the next player can make
		int valid_move_count = 0;
		for(int i = 0; i < p_column_count; i++){
			if(valid_pop(i,player)) valid_move_count++;
			if(valid_drop(i)) valid_move_count++;
		}
		String move_list[] = new String[valid_move_count];
		int move_write_count = 0;
		for(int i = 0; i < p_column_count; i++){
			if(valid_pop(i,player)) move_list[move_write_count++] = "P " + String.valueOf(i);
			if(valid_drop(i)) move_list[move_write_count++] = "D " + String.valueOf(i); 
		}
		return move_list;		
	}
	
	public String[] get_ordered_available_moves(short player){
		//This only works if there are 7 columns!
		if(p_column_count != 7){
			System.err.println("Tried to get an ordered list of moves with a strange board size!");
			return get_available_moves(player);
		}
		else{
			int valid_move_count = 0;
			int best_order[] = new int[7];
			best_order[0] = 3;
			best_order[1] = 4;
			best_order[2] = 2;
			best_order[3] = 1;
			best_order[4] = 5;
			best_order[5] = 6;
			best_order[6] = 0;
			for(int i = 0; i < 7; i++){
				if(valid_drop(best_order[i])) 		valid_move_count++;
				if(valid_pop(best_order[i],player))	valid_move_count++;
			}
			String move_list[] = new String[valid_move_count];
			int move_write_count = 0;
			for(int i = 0; i < 7; i++){
				if(valid_drop(best_order[i]))		move_list[move_write_count++] = "D " + String.valueOf(best_order[i]);
			}
			for(int i = 0; i < 7; i++){
				if(valid_pop(best_order[i],player))	move_list[move_write_count++] = "P " + String.valueOf(best_order[i]);
			}
			return move_list;
		}
	}

}
