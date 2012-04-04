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
			boolean user_input_valid = false;
			while(!user_input_valid){
				String user_input = scan.nextLine();
				if(user_input.length() > 0 && 'D' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.drop(Integer.parseInt(user_input.substring(1).trim()), (short) 1);					
				}
				else if(user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.pop(Integer.parseInt(user_input.substring(1).trim()));					
				}
				else{
					System.err.println("Invalid user input!");
				}
			}
			if(B.compute_win() != 0){
				System.out.println(C.toString());
				break;
			}
			M.make_next_move();
			System.out.println(C.toString());			
		}
		
		switch(B.compute_win()){
		case 0:
			System.out.println("No winner. Maybe a draw. Who knows. This shouldn't happen.");
			break;
		case 1:
			System.out.println("You win! The computer must be really REALLY dumb.");
			break;
		case 2:
			System.out.println("The computer wins! Societal takeover is imminent!");
			break;
		default:
			System.err.println("Not sure who won.");				
		}
	}

}
