package popout.search;

import popout.PlayerNum;
import popout.BoardSize;
import popout.board.BoardState;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Minimax extends Search {
	
	private static final long serialVersionUID = 31415L;

	protected final short p_depth;
	
	protected short p_thread_alpha;
	protected int p_thread_depth;
	protected short p_thread_turn;
	protected String p_thread_move;

	public Minimax(BoardState board) {
		super(board);
		p_depth = 5;

	}
	
	public Minimax(final short[][] target_board, final short target_depth,
			final int current_depth, final short turn, final String move){
		super(new BoardState(target_board));
		p_depth = target_depth;
		p_thread_depth = current_depth;
		p_thread_turn = turn;
		p_thread_move = move;
	}
	
	private final short get_alpha(){
		return p_thread_alpha;
	}
	
	public void compute(){
	}

	public void get_computer_move() {
		short alpha = Short.MIN_VALUE;
		int best_move = -1;
		short current_board_short[][] = p_board.get_state();
		BoardState current_board = new BoardState(current_board_short);
		final String valid_next_moves[] = current_board.get_available_moves(PlayerNum.COMPUTER);
		final short move_utilities[] = new short[valid_next_moves.length];
		int utilities_iter = 0;
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_score = 0;
			short temp_board[][] = current_board.get_state();
			short next_board[][] = null;
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop				
				current_board.drop(move_col, PlayerNum.COMPUTER);
				next_board = current_board.get_state();
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop
				current_board.pop(move_col, PlayerNum.COMPUTER);
				next_board = current_board.get_state();		
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
			}			
			temp_score = minimax(next_board, p_depth, PlayerNum.HUMAN, valid_next_moves[i]);
			move_utilities[utilities_iter++] = temp_score;
			current_board.set_state(temp_board);
			
			if(temp_score > alpha){
				alpha = temp_score;
				best_move = i;
			}
		}
		
		if (-1 == best_move) {
			// couldn't find a best move or something
			System.err.println("Something bad happened during minimax search!");
		} else {
			// the search found one or more best moves, committing to a random one
			if ('D' == valid_next_moves[best_move].charAt(0)) p_board.drop(Integer.parseInt(valid_next_moves[best_move].substring(2)), PlayerNum.COMPUTER);
			if ('P' == valid_next_moves[best_move].charAt(0)) p_board.pop(Integer.parseInt(valid_next_moves[best_move].substring(2)), PlayerNum.COMPUTER);
		}
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			System.out.print(valid_next_moves[i] + " : " + move_utilities[i] + "     ");
		}
		System.out.println("");
	}

	protected final short minimax(final short[][] test_board_short,
			final int depth, final short turn, final String move) {
		BoardState current_board = new BoardState(test_board_short);
		
		if (depth <= 0) {
			return evaluate_board(current_board, move);
		}
		
		short alpha = (PlayerNum.COMPUTER == turn ? Short.MIN_VALUE : Short.MAX_VALUE);
		String valid_next_moves[] = current_board.get_available_moves(turn);
		
		for(int i = 0; i < valid_next_moves.length; i++){
			short temp_score = 0;
			final int move_col = Integer.parseInt(valid_next_moves[i].substring(2));
			short temp_board[][] = current_board.get_state();
			short next_board[][] = null;
			if ('D' == valid_next_moves[i].charAt(0)) {
				// this move is a drop
				current_board.drop(move_col, turn);
				next_board = current_board.get_state();
			} else if ('P' == valid_next_moves[i].charAt(0)) {
				// this move is a pop
				current_board.pop(move_col, turn);
				next_board = current_board.get_state();
			} else {
				// this move is not recognized
				System.err.println("Unrecognized available move: " + valid_next_moves[i]);
				return 0;
			}
			
			temp_score = minimax(next_board, depth - 1, PlayerNum.opposite(turn), valid_next_moves[i]);
			current_board.set_state(temp_board);
			
			if(PlayerNum.COMPUTER == turn){
				alpha = (short) Math.max(alpha, temp_score);
			}
			else{
				alpha = (short) Math.min(alpha, temp_score);
			}
		}
		return alpha;
	}

}
