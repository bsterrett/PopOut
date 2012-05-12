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
import java.util.Random;
import java.util.TreeMap;

import popout.BoardSize;
import popout.board.BoardState;


public class ReinforceLearner {
  private final int STATES_PERFILE = 200; //number of game states to store per file
  
  private final int TOTAL_ACTIONS = 14; //max 14 possible actions: 7 drops, 7 pop
  
  private final String PI_FILE = "rl_pi-tbl";
  private final String Q_FILE = "rl_q-tbl";
  
  private final int WIN_REWARD = 1000;
  private final int LOSE_REWARD = 200;
  
  private BoardState board; //the game board
  private short c_player;   //the symbol(piece) used by the computer
  private float alpha;      //learning rate  
  private float gamma;      //discount rate
  private int states;       //number of game states
  
  private Random generator = new Random(System.currentTimeMillis());
    
  //List to keep track of actions made by learner
  private LinkedList<ReinforceLearnerAction> actions;
  
  //Tree to keep track of which files states are in. <fid, String>
  private TreeMap<Float, String> filesys;
  
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
    filesys = new TreeMap<Float, String>();
    
    
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
    
    //never encountered this state before, initialize it (start learning it)
    if( !filesys.containsKey(sid) ){
      System.out.println("I've never seen state" + sid + ", initializing it now...");
      active_pi_tbl.put(sid, initPiDataForState(state));
      active_q_tbl.put(sid, initQDataForState(state));
    }
    //i've seen this state before, choose an action from it
    else{
      float choice = generator.nextFloat();
      ArrayList<Float> possible_actions = active_pi_tbl.get(sid);
      float sum = 0f; int option = 0;
      for(option = 0; option < TOTAL_ACTIONS; ++option){
        sum += possible_actions.get(option);
        if(sum >= choice){
          System.out.println("State: " + sid + " -> action: " + option); 
          break;
        }
      }
      
      ArrayList<Integer> rewards = active_q_tbl.get(sid);
      ReinforceLearnerAction chosen = new ReinforceLearnerAction(sid, option, rewards.get(option));
      actions.add(chosen);
    }
    
    
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
    int aid; float sid;
    
    //alter the table values for each action chosen in this game
    while(!actions.isEmpty()){
      past_action = actions.removeLast();
      sid = past_action.getStateID();
      aid = past_action.getActionID();
      
      
    }
  }
  
  
  private ArrayList<Integer> initQDataForState(ArrayList<Long> state){
    return new ArrayList<Integer>(TOTAL_ACTIONS);
  }
  
  //the probabilities are list as follows:
  //first 7 (0-6) elements represent prob of a drop in cols 0-6 respectively
  //last 7 (7-13) elements represent prob of a pop in cols 0-6 respectively
  //
  //probabilities are initially uniformly distributed 
  private ArrayList<Float> initPiDataForState(ArrayList<Long> state){
    //determine which actions are illegal given the state
    long all_filled = state.get(0);
    long player_filled = state.get(1);
    
    byte top_row = ReinforceLearnerUtils.shiftToRow(all_filled, BoardSize.TOP_ROW);
    byte bottom_row = ReinforceLearnerUtils.shiftToRow(player_filled, BoardSize.BOTTOM_ROW);
    
    if( top_row == ReinforceLearnerUtils.ROW_ERROR ||
        top_row == ReinforceLearnerUtils.ROW_ERROR){
      System.out.println("ROW ERROR IN PI DATA");
      return null;
    }
      
    //count number of legal actions
    byte legal = 0;
    for(int i = 0; i < 7; ++i){
      //drops allowed if column isn't full
      if( ((top_row >> i) & 1) == 0 ) ++legal;
      //pops allowed if bottom position of column filled by player
      if( ((bottom_row >> i) & 1) == 1 ) ++legal;
    }
    
    //no legal actions in state
    if (0 == legal){
      System.out.println("NOT GOOD");
      return null;
    }
    
    //set initial probs
    ArrayList<Float> probs = new ArrayList<Float>(TOTAL_ACTIONS);
    float uniform = 100.0f / legal;
    for(int i = 0; i < 7; ++i){
      if( ((top_row >> i) & 1) == 0 ) probs.set(i, uniform);
      if( ((bottom_row >> i) & 1) == 1 ) probs.set(i+BoardSize.COLUMN_COUNT, uniform);
    }    
    
    return probs;
  }
}

