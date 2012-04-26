package popout.board;

import java.util.Comparator;

public class MoveReverseComparator implements Comparator<Move> {
	public int compare(Move move1, Move move2){
		return move2.utility - move1.utility;
	}
}
