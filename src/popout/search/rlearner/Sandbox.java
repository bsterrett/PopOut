package popout.search.rlearner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    
    ArrayList<Float> probs = new ArrayList<Float>(14);
    System.out.println(probs.toString());
    System.out.println(foo.toString());
    
    try{
      FileOutputStream fout = new FileOutputStream("sandbox"); 
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      ObjectOutputStream o = new ObjectOutputStream(b);
      o.writeObject(foo);
      o.close();
      fout.write(b.toByteArray());
      fout.close();
    }catch(Exception e){e.printStackTrace(); }
    
    try{
      FileInputStream fin = new FileInputStream("sandbox"); 
      ObjectInputStream o = new ObjectInputStream(fin);
      TreeMap<Integer, ArrayList<Integer>> lol = (TreeMap<Integer, ArrayList<Integer>>)o.readObject();
      System.out.println(lol.toString());
    }catch(Exception e){e.printStackTrace(); }
    
  }

}
