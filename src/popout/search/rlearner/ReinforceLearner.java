/*
 * ReinforceLearner.java
 * 
 * Cha Li
 * 11 May 2012
 * 
 * Reinforcement learning algorithm that plays Popout and destroys any
 * algorithm Ben uses.
 * 
 */


package popout.search.rlearner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import popout.board.BoardState;


public class ReinforceLearner {
  private final int STATES_PERFILE = 200; //number of game states to store per file
  private final String PI_FILE = "rl_pi-tbl";
  private final String Q_FILE = "rl_q-tbl";
  
  private final int WIN_REWARD = 1000;
  private final int LOSE_REWARD = 200;
  
  private BoardState board; //the game board
  private short c_player;   //the symbol(piece) used by the computer
  private float alpha;      //learning rate  
  private float gamma;      //discount rate
  private int states;       //number of game states
  
  //List to keep track of actions made by learner
  private LinkedList<ReinforceLearnerAction> actions;
  
  //Tree to keep track of which files states are in. <fid, String>
  private TreeMap<Integer, String> filesys;
  
  //Q and PI tables used by RL currently loaded in memory 
  //maps State ID's -> Reward for State Actions / Probability of State Actions
  private TreeMap<Float, ArrayList<Float>> active_pi_tbl;
  private TreeMap<Float, ArrayList<Integer>> active_q_tbl;
  
  public ReinforceLearner(BoardState board, int states, short player){
    this.alpha = .6f;
    this.gamma = .9f;
    this.board = board;
  }
  
  public ReinforceLearner(BoardState board, float alpha, float gamma, int states, short player){
    this.alpha = alpha;
    this.gamma = gamma;
    this.board = board;
  }
  
  
  public void init(){
    actions = new LinkedList<ReinforceLearnerAction>();
    filesys = new TreeMap<Integer, String>();
    
    
  }
  
  //make a move
  public void step(){
     /*
     * read board configuration
     * convert configuration into state id
     * use state id to load file which contains related probabilities
     * chose an action for the given state id
     * add action to list
     * make the move.
     */
    ArrayList<Long> state = ReinforceLearnerUtils.boardToState(board.get_state(), c_player);
    
    float sid = ReinforceLearnerUtils.stateToStateId(state);
    
    
    
    /* states:
     *  each position can be:
     *    empty 
     *    contain your piece
     *    contain opponent piece
     *    
     *  state id calculation is a function of what is in each board position.
     *  state id should range from 0 to N-1
     */ 
    
    /*
     * actions: 
     *  pieces can be dropped in any col as long as its not full (7 possible drops)
     *  pieces can be popped in player piece is at the bottom (7 possible pops)
     *  
     *  maximum 14 actions per state, illegal actions have p = 0;
     */
    
  }
  
  public void end(boolean learner_wins){
    int end_prize = ( learner_wins ) ? WIN_REWARD : LOSE_REWARD;
    ReinforceLearnerAction past_action;
    int sid, aid;
    
    //alter the table values for each action chosen in this game
    while(!actions.isEmpty()){
      past_action = actions.removeLast();
      sid = past_action.getStateID();
      aid = past_action.getActionID();
      
      
    }
  }
}
