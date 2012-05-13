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
import popout.board.Move;


public class ReinforceLearner {
  private final int STATES_PERFILE = 200; //number of game states to store per file
  private final int TOTAL_ACTIONS = 14;   //max 14 possible actions: 7 drops, 7 pop
  private final float MAX_PROB = .8f;     //the maximum likelihood allowed for an action
  
  private final String PI_FILE = "rl_pi-tbl";
  private final String Q_FILE = "rl_q-tbl";
  

  
  private final int WIN_REWARD = 700;
  private final int LOSE_REWARD = -300;
  
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
  private TreeMap<Float, ArrayList<Float>> active_pi_tbl, active_q_tbl;
  
  public ReinforceLearner(BoardState board, short player){
    this.alpha = .6f;
    this.gamma = .9f;
    this.board = board;
    this.c_player = player;
  }
  
  public ReinforceLearner(BoardState board, float alpha, float gamma, short player){
    this.alpha = alpha;
    this.gamma = gamma;
    this.board = board;
    this.c_player = player;
  }
  
  
  public void init(){
    actions = new LinkedList<ReinforceLearnerAction>();
    filesys = new TreeMap<Float, String>();
    
    
  }
  
  //make a move
  public void step(){
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
      
      ArrayList<Float> rewards = active_q_tbl.get(sid);
      ReinforceLearnerAction chosen = new ReinforceLearnerAction(sid, option, rewards.get(option));
      actions.add(chosen);
      
      //drop
      if( option < 7 ){
        System.out.println("RL PLAYER: Dropping my piece in column " + option);
        Move move = new Move(Move.DROP, option, c_player);
        board.make_move(move);
      }
      //pop
      else{
        System.out.println("RL PLAYER: Popping my piece in column " + (option - 7));
        Move move = new Move(Move.POP, option - 7, c_player);
        board.make_move(move);
      }
      
    }
  }
  
  public void end(boolean learner_wins){
    float new_reward = ( learner_wins ) ? WIN_REWARD : LOSE_REWARD;
    float new_prob;
    ReinforceLearnerAction past_action;
    int aid; float sid;
    
    //alter the table values for each action chosen in this game
    //work backwards through the list
    ArrayList<Float> cur_rewards;
    ArrayList<Float> cur_probs;
    while(!actions.isEmpty()){
      past_action = actions.removeLast();
      sid = past_action.getStateID();
      aid = past_action.getActionID();
      cur_rewards = active_q_tbl.get(sid);
      
      //update the q values for this state
      new_reward = (1 - alpha) * cur_rewards.get(aid) + alpha * (gamma * new_reward);
      cur_rewards.set(aid, new_reward);
      
      //update the pi values for this state
      cur_probs = active_pi_tbl.get(sid);
      float sum = 0;
      for( float prob : cur_probs)
        sum += prob;
      
      for(int i = 0; i < TOTAL_ACTIONS; ++i){
        new_prob = cur_probs.get(i) / sum;
        cur_probs.set(i, new_prob);
      }
    }
  }
  
  
  private ArrayList<Float> initQDataForState(ArrayList<Long> state){
    //determine which actions are illegal given the state
    long all_filled = state.get(0);
    long player_filled = state.get(1);
    
    byte top_row = ReinforceLearnerUtils.shiftToRow(all_filled, BoardSize.TOP_ROW);
    byte bottom_row = ReinforceLearnerUtils.shiftToRow(player_filled, BoardSize.BOTTOM_ROW);
    
    if( top_row == ReinforceLearnerUtils.ROW_ERROR ||
        bottom_row == ReinforceLearnerUtils.ROW_ERROR){
      System.out.println("ROW ERROR IN PI DATA");
      return null;
    }
    
    //set initial rewards uniformly
    ArrayList<Float> rewards = new ArrayList<Float>(TOTAL_ACTIONS);
    for(int i = 0; i < 7; ++i){
      if( ((top_row >> i) & 1) == 0 ) rewards.set(i, (.8f * WIN_REWARD));
      if( ((bottom_row >> i) & 1) == 1 ) rewards.set(i+BoardSize.COLUMN_COUNT, (.8f * WIN_REWARD));
    }       
    
    return rewards;
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
        bottom_row == ReinforceLearnerUtils.ROW_ERROR){
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

