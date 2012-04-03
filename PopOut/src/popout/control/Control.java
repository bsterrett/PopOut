package popout.control;

import popout.board.BoardState;
import popout.ui.CLDisplay;

public class Control {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BoardState B = new BoardState(6, 7);
		CLDisplay C = new CLDisplay(6, 7, B);
		B.drop(0, (short)1);
		B.drop(2, (short)1);
		B.drop(4, (short)2);
		B.drop(6, (short)2);
		B.drop(6, (short)2);
		B.drop(6, (short)2);
		B.drop(6, (short)2);
		B.drop(6, (short)2);
		B.drop(6, (short)2);
		C.print_board();
		B.drop(6, (short)2);
		B.pop(6);
		C.print_board();
		B.pop(5);
	}

}
