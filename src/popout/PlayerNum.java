package popout;

public class PlayerNum {
	public static final short empty_space = 0;
	public static final short human = 1;
	public static final short computer = 2;
	
	public static short opposite(final short player){
		return (short) (player == human ? computer : human);
	}
}
