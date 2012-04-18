package popout.ui;

import popout.PlayerNum;
import popout.BoardSize;
import popout.board.BoardState;

public class CLDisplay {

	protected short[][] p_display_state;
	protected BoardState p_board;

	public CLDisplay(BoardState board) {
		p_board = board;
	}

	public String toString() {
		String return_string = "\n0 1 2 3 4 5 6\n-------------\n";
		p_display_state = p_board.get_state();
		if (BoardSize.column_count != p_display_state.length
				&& BoardSize.row_count != p_display_state[0].length) {
			System.err
					.println("Tried to print board state which does not have the correct dimensions!");
		} else {
			for (int row = (BoardSize.row_count - 1); row >= 0; row--) {
				for (int col = 0; col < BoardSize.column_count; col++) {
					switch (p_display_state[col][row]) {
					case PlayerNum.empty_space:
						return_string += "- ";
						break;
					case PlayerNum.human:
						return_string += "X ";
						break;
					case PlayerNum.computer:
						return_string += "O ";
						break;
					default:
						return_string += "! ";
						break;
					}
				}
				return_string += "\n";
			}
			return_string += "-------------\n0 1 2 3 4 5 6\n";
		}
		return return_string;
	}

}
