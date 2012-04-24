package popout.control;

import popout.PlayerNum;
import java.util.Scanner;

import popout.board.*;
import popout.search.*;
import popout.ui.*;

public class Control {
	private static BoardState p_board;
	private static CLDisplay p_display;
	private static Search p_search;

	private static void init() {
		p_board = new BoardState();
		p_display = new CLDisplay(p_board);
		p_search = new Minimax(p_board);
		//p_search = new AlphaBeta(p_board);
	}

	private static void get_player_move() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Next move? (Format: 'D X' for drops or 'P X' for pops where X is 0-6)");
		boolean user_input_valid = false;
		while (!user_input_valid) {
			String user_input = scan.nextLine();
			if (user_input.length() > 0	&& 'D' == user_input.toUpperCase().charAt(0)) {
				user_input_valid = p_board.drop(Integer.parseInt(user_input.substring(1).trim()), PlayerNum.HUMAN);
			} else if (user_input.length() > 0 && 'P' == user_input.toUpperCase().charAt(0)) {
				user_input_valid = p_board.pop(Integer.parseInt(user_input.substring(1).trim()), PlayerNum.HUMAN);
			} else {
				System.err.println("Invalid user input!");
			}
		}
	}

	private static void print_winner() {
		switch (p_board.compute_win()) {
		case PlayerNum.EMPTY_SPACE:
			System.out.println("No winner. Maybe a draw. Who knows. This shouldn't happen.");
			return;
		case PlayerNum.HUMAN:
			System.out.println("You win! The computer must be really REALLY dumb.");
			return;
		case PlayerNum.COMPUTER:
			System.out.println("The computer wins! Societal takeover is imminent!");
			return;
		case (short) (10*PlayerNum.COMPUTER + PlayerNum.HUMAN):
			System.out.println("Whoa, you tied! Try again!");
			return;
		default:
			System.err.println("Not sure who won.");
		}
	}

	public static void main(String[] args) {
		init();
		System.out.println(p_display.toString());
		while (p_board.compute_win() == PlayerNum.EMPTY_SPACE) {
			get_player_move();
			if (p_board.compute_win() != PlayerNum.EMPTY_SPACE) {
				System.out.println(p_display.toString());
				break;
			}
			p_search.get_computer_move();
			System.out.println(p_display.toString());
		}
		print_winner();
	}

}
