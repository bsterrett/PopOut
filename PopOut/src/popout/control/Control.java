package popout.control;

import popout.board.*;
import popout.search.*;
import popout.ui.*;

public class Control {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BoardState B = new BoardState(6, 7);
		CLDisplay C = new CLDisplay(6, 7, B);
		//Search M = new Minimax(B);
		B.drop(0, (short) 1);
		B.drop(0, (short) 1);
		B.drop(0, (short) 1);
		B.drop(0, (short) 2);
		B.drop(1, (short) 1);
		B.drop(1, (short) 1);
		B.drop(1, (short) 2);
		System.out.println(C.toString());
		while(B.compute_win() == 0){
			B.drop(3, (short) 2);
			System.out.println(C.toString());
			System.out.println("Current winner: " + B.compute_win());
		}
	}

}
