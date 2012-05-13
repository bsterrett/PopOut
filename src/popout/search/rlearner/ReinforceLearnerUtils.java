package popout.search.rlearner;

import java.util.ArrayList;

import popout.BoardSize;
import popout.PlayerNum;

public class ReinforceLearnerUtils {
  public static byte FILLED = 0;
  public static byte P_FILLED = 1;
  
  //errors
  public static byte ROW_ERROR = (byte)-128;
  
  private static final int NUM_COLS = BoardSize.COLUMN_COUNT;
  private static final int NUM_ROWS = BoardSize.ROW_COUNT;
  
  private static final int PRIME_1 = 3;
  private static final int PRIME_2 = 5;
  
  
  
  
  //convert a 2-d array representing a popout board to a state value 
  //represented as a value-pair. The first value is a value 
  //representing the positions of the board which are filled, the second 
  //value represents the positions filled with the given player's pieces.
  //
  //it takes 42 bits to represent a 7x6 board
  public static ArrayList<Long> boardToState(short[][] board, short player){
    
    ArrayList<Long> state = new ArrayList<Long>();
    long filled = 0, p_filled = 0;
    
    for(int i = 0; i < NUM_ROWS; ++i){
      for(int j = 0; j < NUM_COLS; ++j){
        if( board[j][i] != PlayerNum.EMPTY_SPACE ){
          filled |= (1 << (i*j)); //shift by some function of i and j
          if( board[j][i] == player )
            filled |= (1 << (i*j)); //shift by some function of i and j
        }
      }
    }
    state.add(filled); state.add(p_filled);
    return state; 
  }
  
  //using the state and player state values (together representing a 
  //single state), calculate the state ID used to represent 
  //the given state.
  //
  //state ID is guaranteed to be unique by using prime factorization. log used
  //to reduce memory usage
  public static float stateToStateId(ArrayList<Long> state){
    if( 2 != state.size() )
      return -1;
    else
      return (float)(Math.pow(PRIME_1, Math.log10(state.get(0))) * 
          Math.pow(PRIME_2, Math.log10(state.get(1))));
  }
  
  //row is 0-index, where row 0 is the bottom most row of the popout board.
  //this method returns a byte representing the 7 slots in the given row.
  //the returned byte, where the bottom 7 bits are used, represent if a 
  //position is filled. no distinction is made between what piece fills it.
  //on error, the left most bit is set to 1
  public static byte shiftToRow(Long board, byte row){
    
    if( row > NUM_ROWS-1 )
      return ROW_ERROR;
    
    long row_mask = (long)Math.pow(2, (row+1)*NUM_COLS) - 1; 
    
    return (byte)((board & row_mask) >> row*NUM_COLS);
  }
}