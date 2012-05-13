/*
 * ReinforceLearnerAction.java
 * 
 * Cha Li
 * 11 May 2012
 * 
 * This class represent a single action made by the reinforcement learner.
 * 
 */
package popout.search.rlearner;

public class ReinforceLearnerAction {
  private float state_id;   //the state this action was performed in
  private int action_id;  //the action
  private float reward;     //the immediate reward given for choosing this action
  
  public String toString(){
    return "State: " + state_id + "\n" +
            "Action: " + action_id + "\n" +
            "Reward: " + reward;
  }
  
  //constructors
  public ReinforceLearnerAction(){
    this.state_id = -1;
    this.action_id = -1;
    this.reward = 0;
  }
  
  public ReinforceLearnerAction(float sid, int aid, float reward){
    this.state_id = sid;
    this.action_id = aid;
    this.reward = reward;
  }
  
  //getters
  public float getStateID(){ return state_id; }
  public int getActionID(){ return action_id; }
  public float getReward(){ return reward; }
}
