package popout;

public class PlayerNum {
	public static final short EMPTY_SPACE = 0;
	public static final short HUMAN = 1;
	public static final short COMPUTER = 2;
	
	public static short opposite(final short player){
		if(player == HUMAN){
			return COMPUTER;
		}
		if(player == COMPUTER){
			return HUMAN;
		}
		return EMPTY_SPACE;
	}
}
