package popout.search;

import popout.board.BoardState;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public abstract class Search extends RecursiveAction {

	protected BoardState p_board;
	protected final short p_player_number;
	protected final short p_computer_number;
	protected final short p_empty_space_number;
	protected final int p_column_count;
	protected final int p_row_count;
	protected Random p_random;
	protected final Heuristic p_heuristic_func;
	protected final short p_heuristic_num;
	private static final long serialVersionUID = 1337L;

	public Search(BoardState board, final short empty_space_number,
			final short player_number, final short computer_number) {
		p_board = board;
		p_empty_space_number = empty_space_number;
		p_player_number = player_number;
		p_computer_number = computer_number;
		p_column_count = p_board.get_state().length;
		p_row_count = p_board.get_state()[0].length;
		p_random = new Random(System.nanoTime());
		p_heuristic_num = 4;
		p_heuristic_func = new Heuristic(empty_space_number, player_number,
				computer_number, p_heuristic_num);
	}

	public void get_computer_move() {
		// this should do something if called by a particular search algorithm
		System.err
				.println("Called generic Search.make_next_move(), need to specify search type!");
	}

}
