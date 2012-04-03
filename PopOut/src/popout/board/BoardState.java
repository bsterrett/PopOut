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
		if(0 == p_current_state[col][p_row_count-1]){
			//this column has an empty space and can be played
			for(int row = 0; row < p_row_count; row++){
				//search up from the bottom to find where the chip will go
				if(0 == p_current_state[col][row]){
					p_current_state[col][row] = player;
					break;
				}
			}
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
		if(0 == p_current_state[col][0]){
			//this column is empty and cannot be popped!
			System.err.println("Trying to pop an empty column!");
			return false;
		}
		else{
			for(int row = 0; row < (p_row_count-1); row++){
				//drops chip from space above into current space, going from bottom to top
				p_current_state[col][row] = p_current_state[col][row+1];
			}
			p_current_state[col][p_row_count-1] = 0;
			return true;
		}
	}

	public short[][] get_state() {
		return p_current_state;
	}
	
	public short compute_win(){
		for(int col = 0; col < p_column_count; col++){
			for(int row = 0; row < p_row_count; row++){
				if(0 != p_current_state[col][row]){
					//check up-left
					//check up
					//check up-right
					//check right
				}
			}
		}
		return 0; //CHANGE THIS!!!!!
	}
}
