package popout.search.rlearner;

import java.util.ArrayList;
import java.util.TreeMap;

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
        
    TreeMap<Integer, ArrayList<Integer>> foo = new TreeMap<Integer, ArrayList<Integer>>();
    ArrayList<Integer> foo_list = new ArrayList<Integer>();
    for(int i = 0; i < 10; ++i)
      foo_list.add(i);
    
    foo.put(1, foo_list);
    System.out.println(foo_list.toString());
    System.out.println(foo.get(1).toString());
    
    foo_list = foo.get(1);
    foo_list.set(3, 100);
    
    System.out.println(foo.get(1).toString());
  }

}
