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
		if (BoardSize.COLUMN_COUNT != p_display_state.length
				&& BoardSize.ROW_COUNT != p_display_state[0].length) {
			System.err
					.println("Tried to print board state which does not have the correct dimensions!");
		} else {
			for (int row = (BoardSize.ROW_COUNT - 1); row >= 0; row--) {
				for (int col = 0; col < BoardSize.COLUMN_COUNT; col++) {
					switch (p_display_state[col][row]) {
					case PlayerNum.EMPTY_SPACE:
						return_string += "- ";
						break;
					case PlayerNum.HUMAN:
						return_string += "X ";
						break;
					case PlayerNum.COMPUTER:
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
