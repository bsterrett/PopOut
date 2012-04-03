package popout.ui;

import popout.board.BoardState;

public class CLDisplay {
	
	protected short[][] p_display_state;
	protected BoardState p_board;
	protected final int p_column_count;
	protected final int p_row_count;

	public CLDisplay(int board_height, int board_width, BoardState board){
		p_board = board;
		p_column_count = board_width;
		p_row_count = board_height;
	}
	
	public void print_board(){
		p_display_state = p_board.get_state();
		if(p_column_count != p_display_state.length && p_row_count != p_display_state[0].length){
			System.err.println("Tried to print board state which does not have the correct dimensions!");
		}
		else{
			for(int row = (p_row_count-1); row >= 0; row--){
				for(int col = 0; col < p_column_count; col++){
					switch(p_display_state[col][row]) {
					case 0:
						System.out.print("- ");
						break;
					case 1:
						System.out.print("X ");
						break;
					case 2:
						System.out.print("O ");
						break;
					default:
						System.out.print("Z ");
						break;
					}
				}
				System.out.print("\n");
			}
		}
	}
	

}
