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
    System.out.println("----- PI-TABLE -----");
    ReinforceLearnerUtils.viewTable("rl_pi-tbl");
    System.out.println("\n\n");
    //System.out.println("----- Q-TABLE -----");
    //ReinforceLearnerUtils.viewTable("rl_q-tbl");
  }
}
