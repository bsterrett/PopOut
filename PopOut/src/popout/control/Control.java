package popout.control;

import java.util.Scanner;

import popout.board.*;
import popout.search.*;
import popout.ui.*;

public class Control {

	public static void main(String[] args) {
		BoardState B = new BoardState(6, 7);
		CLDisplay C = new CLDisplay(6, 7, B);
		Search M = new Minimax(B);
		Scanner scan = new Scanner(System.in); 

		while(B.compute_win() == 0){
			System.out.println("Next move? (Format: 'X Y' where X is D or P and Y is 0-6)");
			String user_input = scan.nextLine();
			if(user_input.length() > 0 &&'D' == user_input.charAt(0)){
				B.drop(Integer.parseInt(user_input.substring(2)), (short) 1);
			}
			else if(user_input.length() > 0 && 'P' == user_input.charAt(0)){
				B.pop(Integer.parseInt(user_input.substring(2)));
			}
			else{
				System.err.println("Invalid user input!");
			}
			//M.make_next_move();
			System.out.println(C.toString());			
		}
		System.out.println("The winner: " + B.compute_win());
	}

}
