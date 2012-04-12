package popout.control;

import java.util.Scanner;

import popout.board.*;
import popout.search.*;
import popout.ui.*;

public class Control {
	private static BoardState p_board;
	private static CLDisplay p_display;
	private static Search p_search;
	private static short p_empty_space_number;
	private static short p_player_number;
	private static short p_computer_number;
	
	private static void init(){
		p_empty_space_number = 0;
		p_player_number = 1;
		p_computer_number = 2;		
		p_board = new BoardState(6, 7);
		p_display = new CLDisplay(6, 7, p_board);
		p_search = new Minimax(p_board, p_empty_space_number, p_player_number, p_computer_number);
		//p_search = new AlphaBeta(p_board, p_empty_space_number, p_player_number, p_computer_number);
	}
	
	private static void get_player_move(){
		Scanner scan = new Scanner(System.in);
		System.out.println("Next move? (Format: 'D X' for drops or 'P X' for pops where X is 0-6)");
		boolean user_input_valid = false;
		while(!user_input_valid){
			String user_input = scan.nextLine();
			if(user_input.length() > 0 && 'D' == user_input.toUpperCase().charAt(0)){
				user_input_valid = p_board.drop(Integer.parseInt(user_input.substring(1).trim()), p_player_number);					
			}
			else if(user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)){
				user_input_valid = p_board.pop(Integer.parseInt(user_input.substring(1).trim()), p_player_number);					
			}
			else{
				System.err.println("Invalid user input!");
			}
		}
	}
	
	private static void print_winner(){
		switch(p_board.compute_win()){
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

	public static void main(String[] args) {
		init();
		System.out.println(p_display.toString());
		while(p_board.compute_win() == 0){
			get_player_move();
			if(p_board.compute_win() != 0){
				System.out.println(p_display.toString());
				break;
			}
			p_search.get_computer_move();
			System.out.println(p_display.toString());			
		}
		print_winner();
	}

}
