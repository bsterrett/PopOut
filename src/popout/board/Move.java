package popout.board;

public class Move {
	public final int col;
	public final int type;
	public final short player;
	public short utility;
	public static final int DROP = 0;
	public static final int POP = 1;
	
	public Move(final int type, final int col, final short player){
		this.type = type;
		this.col = col;
		this.player = player;
	}
}

