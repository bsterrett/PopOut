package popout.search.rlearner;

import java.util.ArrayList;

import popout.BoardSize;
import popout.PlayerNum;

public class ReinforceLearnerUtils {
  
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
        if( board[i][j] != PlayerNum.EMPTY_SPACE ){
          filled |= (1 << (i*j)); //shift by some function of i and j
          if( board[i][j] == player )
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
}
