package popout.search.rlearner;

public class Sandbox {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int row_size = 4;
    int row = 7;
    
    int test = 0xDEADBEEF;
    System.out.println(Integer.toBinaryString(test));
    long mask = (long)Math.pow(2, ((row+1)*row_size)) - 1;
    
    System.out.println(Long.toBinaryString(mask));
    
    byte ex = (byte)((test & mask) >> (row*row_size));
    
    System.out.println(Integer.toBinaryString(ex));
    
    byte foo = (byte)-128;
    System.out.println(foo);
  }

}
