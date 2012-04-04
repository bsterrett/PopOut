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
	
	public boolean valid_drop(final int col){
		return (0 == p_current_state[col][p_row_count-1]);
	}
	
	public boolean drop(final int col, final short player){
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
	
	public boolean valid_pop(final int col){
		return (0 != p_current_state[col][0]);
	}
	
	public boolean pop(final int col){
		if(valid_pop(col)){
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

	public short[][] get_state() {
		return p_current_state;
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
	
	
	
	public String[] get_available_moves(){
		//returns a list of available moves that the next player can make
		int valid_move_count = 0;
		for(int i = 0; i < p_column_count; i++){
			if(valid_pop(i)) valid_move_count++;
		}
		for(int i = 0; i < p_column_count; i++){
			if(valid_drop(i)) valid_move_count++;
		}
		String move_list[] = new String[valid_move_count];
		int move_write_count = 0;
		for(int i = 0; i < p_column_count; i++){
			if(valid_pop(i)) move_list[move_write_count++] = "P " + String.valueOf(i);
		}
		for(int i = 0; i < p_column_count; i++){
			if(valid_drop(i)) move_list[move_write_count++] = "D " + String.valueOf(i); 
		}
		return move_list;		
	}
}
