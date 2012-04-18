package popout.search;

import popout.board.BoardState;

public class NegaScout extends Search {

	private static final long serialVersionUID = 12345678910L;

	public NegaScout(BoardState board) {
		super(board);
		//do the normal get_computer_move stuff here
	}
	
	public NegaScout(final short[][] test_board_short, final int depth, final short turn, final String move, final short start_alpha, final short start_beta){
		super(new BoardState(test_board_short));
		//do recursive negascout search here
	}

}
