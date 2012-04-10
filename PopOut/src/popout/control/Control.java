package popout.control;

import java.util.Scanner;

import popout.board.*;
import popout.search.*;
import popout.ui.*;

public class Control {

	public static void main(String[] args) {
		final short empty_space_number = 0;
		final short player_number = 1;
		final short computer_number = 2;
		
		BoardState B = new BoardState(6, 7);
		CLDisplay C = new CLDisplay(6, 7, B);
		Search M = new Minimax(B, empty_space_number, player_number, computer_number);
		Scanner scan = new Scanner(System.in); 


		while(B.compute_win() == 0){
			System.out.println("Next move? (Format: 'X Y' where X is D or P and Y is 0-6)");
			boolean user_input_valid = false;
			while(!user_input_valid){
				String user_input = scan.nextLine();
				if(user_input.length() > 0 && 'D' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.drop(Integer.parseInt(user_input.substring(1).trim()), player_number);					
				}
				else if(user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.pop(Integer.parseInt(user_input.substring(1).trim()), player_number);					
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
		
/*		while(B.compute_win() != 4){
			System.out.println("Player move? (Format: 'X Y' where X is D or P and Y is 0-6)");
			boolean user_input_valid = false;
			while(!user_input_valid){
				String user_input = scan.nextLine();
				if(user_input.length() > 0 && 'D' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.drop(Integer.parseInt(user_input.substring(1).trim()), player_number);					
				}
				else if(user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.pop(Integer.parseInt(user_input.substring(1).trim()), player_number);					
				}
				else if(user_input.length() > 0 && 'E' == user_input.toUpperCase().charAt(0)){
					System.out.println("Current board utility for computer: " + M.evaluate_board_four(B, ""));
				}
				else{
					System.err.println("Invalid user input!");
				}
			}
			System.out.println(C.toString());
			System.out.println("Computer move? (Format: 'X Y' where X is D or P and Y is 0-6)");
			user_input_valid = false;
			while(!user_input_valid){
				String user_input = scan.nextLine();
				if(user_input.length() > 0 && 'D' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.drop(Integer.parseInt(user_input.substring(1).trim()), computer_number);					
				}
				else if(user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)){
					user_input_valid = B.pop(Integer.parseInt(user_input.substring(1).trim()), computer_number);					
				}
				else if(user_input.length() > 0 && 'E' == user_input.toUpperCase().charAt(0)){
					System.out.println("Current board utility for computer: " + M.evaluate_board_four(B, ""));
				}
				else{
					System.err.println("Invalid user input!");
				}
			}
			System.out.println(C.toString());			
		}*/
		
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
