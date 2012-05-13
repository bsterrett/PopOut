package popout.control;

import popout.PlayerNum;
import popout.board.*;
import popout.search.*;
import popout.search.rlearner.ReinforceLearner;
import popout.ui.*;

public class Control2 {
  private static BoardState p_board;
  private static CLDisplay p_display;
  private static Search p_search;
  private static ThreadedIDS p_ids;
  private static ReinforceLearner rlearner;
  
  //keep track of stats
  private static int wins = 0, losses = 0, ties = 0;
  
  private static void init() {
    p_board = new BoardState();
    p_display = new CLDisplay(p_board);
    p_ids = new ThreadedIDS(p_board, ThreadedIDS.NegaScout);
    rlearner = new ReinforceLearner(p_board, .9f, .9f, PlayerNum.HUMAN);
    rlearner.init();
  }

  private static void print_winner() {
    switch (p_board.compute_win()) {
    case PlayerNum.EMPTY_SPACE:
      System.out.println("No winner. Maybe a draw. Who knows. This shouldn't happen.");
      return;
    case PlayerNum.HUMAN:
      wins++;
      System.out.println("RLearner Wins!");
      rlearner.end(true);
      return;
    case PlayerNum.COMPUTER:
      losses++;
      System.out.println("The computer wins! Societal takeover is imminent!");
      rlearner.end(false);
      return;
    case PlayerNum.TIE:
      ties++;
      System.out.println("Whoa, you tied! Try again!");
      rlearner.end(false);
      return;
    default:
      System.err.println("Not sure who won.");
    }
  }

  public static void main(String[] args) {
    init();
    System.out.println(p_display.toString());
    while (p_board.compute_win() == PlayerNum.EMPTY_SPACE) {
      rlearner.step();
      if (p_board.compute_win() != PlayerNum.EMPTY_SPACE) {
        System.out.println(p_display.toString());
        break;
      }
      p_ids.start_search();
      System.out.println(p_display.toString());
    }
    print_winner();
    rlearner.finish();
    System.out.println("Wins: " + wins + ", Losses: " + losses + ", Ties: " + ties);
  }
}
