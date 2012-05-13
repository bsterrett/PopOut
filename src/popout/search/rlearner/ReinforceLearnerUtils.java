package popout.search.rlearner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

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
          filled |= (1l << ((i * NUM_COLS) + j));
          if( board[j][i] == player )
            p_filled |= (1l << ((i * NUM_COLS) + j));
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
    if( 2 != state.size() ){
      System.err.println("BROKEN STATE");
      System.out.println(state.toString());
      return -1;
    }
    else{
      float sid;
      if( state.get(0) == 0 )
        sid = (float)Math.pow(PRIME_2, Math.log10(state.get(1)));
      else if( state.get(1) == 0 )
        sid = (float)Math.pow(PRIME_1, Math.log10(state.get(0)));
      else
        sid = (float)(Math.pow(PRIME_1, Math.log10(state.get(0))) * 
              Math.pow(PRIME_2, Math.log10(state.get(1))));
      return sid;
    }
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
  
  public static void saveTable(TreeMap<Float, ArrayList<Float>> table, String filename){
    try{
      FileOutputStream fout = new FileOutputStream(filename); 
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      ObjectOutputStream o = new ObjectOutputStream(b);
      o.writeObject(table);
      o.close();
      fout.write(b.toByteArray());
      fout.close();
    }catch(FileNotFoundException e1){ e1.printStackTrace(); }
    catch(IOException e2){ e2.printStackTrace(); }
  }

  public static TreeMap<Float, ArrayList<Float>> loadTable(String filename){
    ObjectInputStream o;
    FileInputStream fin;
    TreeMap<Float, ArrayList<Float>> data = null;
    try{
      fin = new FileInputStream(filename); 
      o = new ObjectInputStream(fin);
      data = (TreeMap<Float, ArrayList<Float>>)o.readObject(); 
    }catch(FileNotFoundException e1){ return null; }
    catch(IOException e2){ e2.printStackTrace(); }
    catch(ClassNotFoundException e3){ e3.printStackTrace(); }
    
    return data;
  }
  
  public static void viewTable(String filename){
    TreeMap<Float, ArrayList<Float>> data = loadTable(filename);
    System.out.println(data);
  }
}
