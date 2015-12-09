package mahmer;


import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarExplorerBrain;
import edu.warbot.communications.WarMessage;

public abstract class WarExplorerBrainController extends WarExplorerBrain {

  boolean _angleBaseEnemyDetected = false;

  public WarExplorerBrainController() {
    super();
  }
 

  private void checkBaseFounded() {
    if (_angleBaseEnemyDetected) {
      return;
    }

    for (WarMessage m : getMessages()) {
      if (m.getMessage().equals(Messsages.MESSAGE_BASE_ENEMY_DETECTED)) {
        _angleBaseEnemyDetected = true;
        return;
      }
    }

    for (WarAgentPercept base : getPerceptsEnemiesByType(WarAgentType.WarBase)) {
      broadcastMessageToAgentType(WarAgentType.WarBase, Messsages.MESSAGE_BASE_ENEMY_DETECTED, base.getAngle() + "", base.getDistance() + "");
      setDebugString("I Founded " + base.getAngle() + " " + base.getDistance());
      System.out.println("I Founded " + base.getAngle() + " " + base.getDistance());
    }
    
    
  }



  @Override
  public String action() {

    checkBaseFounded();

    if (isBlocked())
      setRandomHeading();
    return WarExplorer.ACTION_MOVE;
  }

}
