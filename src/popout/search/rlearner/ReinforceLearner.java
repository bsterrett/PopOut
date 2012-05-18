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
import popout.PlayerNum;
import popout.board.BoardState;
import popout.board.Move;


public class ReinforceLearner {
  private final int STATES_PERFILE = 200; //number of game states to store per file
  private final int TOTAL_ACTIONS = 14;   //max 14 possible actions: 7 drops, 7 pop
  private final float MIN_REWARD = 20f;
  
  private final String PI_FILE = "rl_pi-tbl";
  private final String Q_FILE = "rl_q-tbl";
 
  //strategy bias
  private float DEF_BIAS = .45f;
    
  private final int WIN_REWARD = 900;
  private final int LOSE_REWARD = -100;
  
  private BoardState board; //the game board
  private short c_player;   //the symbol(piece) used by the computer
  private float alpha;      //learning rate  
  private float gamma;      //discount rate
  
  private Random generator = new Random(System.currentTimeMillis());
    
  //List to keep track of actions made by learner
  private LinkedList<ReinforceLearnerAction> actions;
  
  //TODO: implement
  //Tree to keep track of which files states are in. <fid, String>
  //private TreeMap<Float, String> filesys;
  
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
    //filesys = new TreeMap<Float, String>();
    
    active_pi_tbl = ReinforceLearnerUtils.loadTable(PI_FILE);
    active_q_tbl = ReinforceLearnerUtils.loadTable(Q_FILE);
    
    if(active_pi_tbl == null || active_q_tbl == null){
      active_pi_tbl = new TreeMap<Float, ArrayList<Float>>();
      active_q_tbl = new TreeMap<Float, ArrayList<Float>>();
      System.out.println("Tables Created.");
    }
    else
      System.out.println("Tables Loaded.");
    
  }
  
  //make a move
  public void step(){
    ArrayList<Long> state = ReinforceLearnerUtils.boardToState(board.get_state(), c_player);
    
    float sid = ReinforceLearnerUtils.stateToStateId(state);
    
    if( Float.isNaN(sid) ){
      System.err.println("step: UNKNOWN STATE");
      System.err.println(state.toString());
      System.err.println(Long.toBinaryString(state.get(0)) + " -- " + Long.toBinaryString(state.get(1)));
      System.exit(0);
    }
    
    //never encountered this state before, initialize it (start learning it)
    if( !active_pi_tbl.containsKey(sid) ){
      System.out.println("I've never seen state " + sid + ", initializing it now...");
      active_pi_tbl.put(sid, initPiDataForState(state));
      active_q_tbl.put(sid, initQDataForState(state));
    }
    //i've seen this state before, choose an action from it
    ArrayList<Float> possible_actions = active_pi_tbl.get(sid);
    ArrayList<Integer> legal_actions = new ArrayList<Integer>();
    float instant_reward; int option = 0, max_option = 0; float max_reward = 0;
    for(option = 0; option < TOTAL_ACTIONS; ++option){
      if( possible_actions.get(option) > 0 ){
        legal_actions.add(option);
        int action = (option < 7) ? Move.DROP : Move.POP;
        
        //pick the move
        instant_reward = calculateActionWorth(c_player, action, option % 7);
        if( possible_actions.get(option) * instant_reward > max_reward ){
          max_reward = possible_actions.get(option) * instant_reward;
          max_option = option;
        }
      }
    }
    
    //TODO: replace hard coded value with some decay function based on turns
    if( generator.nextFloat() <= .90 )  
      option = max_option;
    else{
      legal_actions.remove(new Integer(max_option));
      option = legal_actions.get(generator.nextInt(legal_actions.size()));
      System.out.println("RL PLAYER: Exploring suboptimal move...");
    }
    System.out.println("State: " + sid + " -> action: " + option); 
   
    //drop
    if( option < 7 ){
      System.out.println("RL PLAYER: Dropping my piece in column " + option);
      instant_reward = calculateActionWorth(c_player, Move.DROP, option);
      Move move = new Move(Move.DROP, option, c_player);
      board.make_move(move);
    }
    //pop
    else{
      System.out.println("RL PLAYER: Popping my piece in column " + (option - 7));
      instant_reward = calculateActionWorth(c_player, Move.POP, option - 7);      
      Move move = new Move(Move.POP, option - 7, c_player);
      board.make_move(move);
    }
    ReinforceLearnerAction chosen = new ReinforceLearnerAction(sid, option, instant_reward);
    actions.add(chosen);
  }
  
  public void end(boolean learner_wins){
    float new_reward = ( learner_wins ) ? WIN_REWARD : LOSE_REWARD;
    
    System.out.println("RL Player: " + (learner_wins ? "I Won!" : "I Lost =["));
    
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
      new_reward = (1 - alpha) * cur_rewards.get(aid) + alpha * (past_action.getReward() + gamma * new_reward);
      if(new_reward < MIN_REWARD)
        new_reward = MIN_REWARD;
      
      cur_rewards.set(aid, new_reward);
      //active_q_tbl.put(sid, cur_rewards);
      
      //update the pi values for this state
      float sum = 0;
      for( float reward : cur_rewards)
        sum += reward;

      cur_probs = active_pi_tbl.get(sid);
      for(int i = 0; i < TOTAL_ACTIONS; ++i){
        new_prob = cur_rewards.get(i) / sum;
        cur_probs.set(i, new_prob);
      }
      //active_pi_tbl.put(sid, cur_probs);
    }
  }
  
  public void finish(){
    ReinforceLearnerUtils.saveTable(active_pi_tbl, PI_FILE);
    ReinforceLearnerUtils.saveTable(active_q_tbl, Q_FILE);
  }
  
  private ArrayList<Float> initQDataForState(ArrayList<Long> state){
    //determine which actions are illegal given the state
    long all_filled = state.get(0);
    long player_filled = state.get(1);
    
    byte top_row = ReinforceLearnerUtils.shiftToRow(all_filled, BoardSize.TOP_ROW);
    byte bottom_row = ReinforceLearnerUtils.shiftToRow(player_filled, BoardSize.BOTTOM_ROW);
    
    if( top_row == ReinforceLearnerUtils.ROW_ERROR ||
        bottom_row == ReinforceLearnerUtils.ROW_ERROR){
      System.err.println("initQ: ROW ERROR IN TABLE DATA");
      System.err.println(state.toString());
      return null;
    }
    
    //set initial rewards uniformly
    ArrayList<Float> rewards = new ArrayList<Float>(TOTAL_ACTIONS);
    for(int i = 0; i < TOTAL_ACTIONS; ++i)
      rewards.add(0f);
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
      System.err.println("initPi: ROW ERROR IN TABLE DATA");
      System.err.println(state.toString());
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
      System.err.println("initPi: NO LEGAL ACTIONS IN STATE");
      System.err.println(state.toString());
      return null;
    }
    
    //set initial probs
    ArrayList<Float> probs = new ArrayList<Float>(TOTAL_ACTIONS);
    for(int i = 0; i < TOTAL_ACTIONS; ++i)
      probs.add(0f);
    
    float uniform = 1.0f / legal;
    for(int i = 0; i < 7; ++i){
      if( ((top_row >> i) & 1) == 0 ) probs.set(i, uniform);
      if( ((bottom_row >> i) & 1) == 1 ) probs.set(i+BoardSize.COLUMN_COUNT, uniform);
    }    
    
    return probs;
  }

  private float calculateActionWorth(short player, int action, int column){
    float worth = ReinforceLearnerUtils.REWARD_ERROR;
 
    if(action == Move.DROP){
      worth = calculateDropWorth(player, column);
    }
    else if(action == Move.POP){
      worth = calculatePopWorth(player, column);
    }
    return worth;
  }
  
  private float calculateDropWorth(short player, int column){
    int row = board.getFirstEmptyInColumn(column);
    return calculatePositionWorth(player, column, row);
  }
  
  private float calculatePositionWorth(short player, int column, int row){
    float base = 20;
    float v_score = 0, l_score = 0, r_score = 0, bias = 5;
    float ld_score = 0, rd_score = 0, lu_score = 0, ru_score = 0;
    
    v_score = calculateDirectionalWorth(player, column, row-1, column, row-3);

    //check left and right
    l_score = calculateDirectionalWorth(player, column-1, row, column-3, row);
    r_score = calculateDirectionalWorth(player, column+1, row, column+4, row);

    //check left and right down diagonals
    ld_score = calculateDirectionalWorth(player, column-1, row-1, column-3, row-3);
    rd_score = calculateDirectionalWorth(player, column+1, row-1, column+4, row-3);
    
    //check left and right up diagonals
    lu_score = calculateDirectionalWorth(player, column-1, row+1, column-3, row+4);
    ru_score = calculateDirectionalWorth(player, column+1, row+1, column+4, row+4);  
    
    //drops closer to the center are better
    base -= bias * Math.abs(column - Math.floor(BoardSize.COLUMN_COUNT / 2));
    
    return base + v_score + r_score + l_score + ld_score + rd_score + lu_score + ru_score;
  }
  
  //the worth of a pop is the sum of the drop worths of all pieces except the
  //dropped piece.
  private float calculatePopWorth(short player, int column){
    short[][] state = board.get_state();
    float worth = 0;
    for(int r = 1; r < BoardSize.ROW_COUNT; ++r){
      if( state[column][r] != PlayerNum.EMPTY_SPACE ){
        short piece = state[column][r];
        float delta = this.calculatePositionWorth(piece, column, r-1);
        worth += ( piece == player ) ? delta : -delta;
      }
      else{ break; }
    }
    return worth;
  }
  
  private float calculateDirectionalWorth(short player, int c_i, int r_i, int c_f, int r_f){
    short[][] state = board.get_state();
    
    //keep track of the first piece in the direction being checked
    short first = PlayerNum.EMPTY_SPACE;
    short enemy = PlayerNum.opposite(player); 
    
    //define the hard limits, remember signs
    int c_hard = 0, r_hard = 0, c_mod = 1, r_mod = 1, c_delta = -1, r_delta = -1;
    
    if((c_f - c_i) > 0){
      c_hard = -(BoardSize.COLUMN_COUNT - 1);
      c_i = -c_i; c_f = -c_f;
      c_mod = -1;
    }
    else if((c_f - c_i) == 0)
      c_delta = 0;
    
    if((r_f - r_i) > 0){
      r_hard = -(BoardSize.ROW_COUNT - 1);
      r_i = -r_i; r_f = -r_f;
      r_mod = -1;
    }
    else if((r_f - r_i) == 0)
      r_delta = 0;
    
    float worth = 10,  weight = 0;
    
    while(c_i >= Math.max(c_f, c_hard) && r_i >= Math.max(r_f, r_hard)){
      if( first == PlayerNum.EMPTY_SPACE){ 
        first = state[c_i * c_mod][r_i * r_mod];
        weight = (first == enemy) ? DEF_BIAS : 1 - DEF_BIAS;
      }           
      if( first != PlayerNum.EMPTY_SPACE && state[c_i * c_mod][r_i * r_mod] == first ){
        worth += (weight * worth); c_i+=c_delta; r_i+=r_delta;
      }
      else{ break; }       
    }
    return worth;
  }
}

