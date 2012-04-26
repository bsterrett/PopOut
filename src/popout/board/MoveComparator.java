package popout.board;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move>{
	public int compare(Move move1, Move move2){
		return move1.utility - move2.utility;
	}
}