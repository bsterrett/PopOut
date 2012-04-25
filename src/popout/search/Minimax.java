package popout.search;

import popout.PlayerNum;
import popout.board.*;

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
	
	public void compute(){
	}

	public void get_computer_move() {
		short alpha = Short.MIN_VALUE;
		int best_move = -1;
		short current_board_short[][] = p_board.get_state();
		BoardState current_board = new BoardState(current_board_short);
		final Move valid_next_moves[] = get_unordered_moves(current_board,PlayerNum.COMPUTER);		
		for (int i = 0; i < valid_next_moves.length; i++) {
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move, PlayerNum.COMPUTER);
			final short next_board[][] = current_board.get_state();
			final short temp_score = minimax(next_board, p_depth, PlayerNum.HUMAN, next_move);
			next_move.utility = temp_score;
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
			p_board.make_move(valid_next_moves[best_move], PlayerNum.COMPUTER);
		}
		
		for (int i = 0; i < valid_next_moves.length; i++) {
			// for debugging
			System.out.print(valid_next_moves[i].type + "" + valid_next_moves[i].col + " : " + valid_next_moves[i].utility + "     ");
		}
		System.out.println("");
	}

	protected final short minimax(final short[][] test_board_short,
			final int depth, final short turn, final Move current_move) {
		BoardState current_board = new BoardState(test_board_short);
		
		if (depth <= 0) {
			return evaluate_board(current_board, current_move);
		}
		
		short alpha = (PlayerNum.COMPUTER == turn ? Short.MIN_VALUE : Short.MAX_VALUE);
		Move valid_next_moves[] = get_unordered_moves(current_board, turn);
		
		for(int i = 0; i < valid_next_moves.length; i++){
			Move next_move = valid_next_moves[i];
			final short temp_board[][] = current_board.get_state();
			current_board.make_move(next_move, turn);
			final short next_board[][] = current_board.get_state();
			final short temp_score = minimax(next_board, depth - 1, PlayerNum.opposite(turn), next_move);
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
