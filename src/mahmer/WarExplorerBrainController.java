package mahmer;


import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarExplorerBrain;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CartesianCoordinates;
import edu.warbot.tools.geometry.PolarCoordinates;

public abstract class WarExplorerBrainController extends WarExplorerBrain {

  boolean _angleBaseEnemyDetected = false;
  WarAgentPercept base = null;

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
      } else if(base != null && m.getMessage().equals(Messsages.MESSAGE_BASE_LOCATION)){
          PolarCoordinates result = Triangle.getAngleWithDistance(base.getAngle(), base.getDistance(), m.getAngle(), m.getDistance());
          double angle = (result.getAngle() + 180) % 360;
          double distance = result.getDistance();
          broadcastMessageToAgentType(WarAgentType.WarBase, Messsages.MESSAGE_BASE_ENEMY_DETECTED, angle + "", distance + "");
          _angleBaseEnemyDetected = true;
          System.out.println("Base Enemy distance:"+ base.getDistance()+" angle:"+base.getAngle());
          System.out.println("Base Allie distance:"+ m.getDistance()+" angle:"+m.getAngle());
          System.out.println("Result distance:"+ result.getDistance()+" angle:"+result.getAngle());
          
      }
    }

    for (WarAgentPercept base : getPerceptsEnemiesByType(WarAgentType.WarBase)) {
      broadcastMessageToAgentType(WarAgentType.WarBase, Messsages.MESSAGE_BASE_LOCATION);
      this.base = base;
      setDebugString("I Founded " + base.getAngle() + " " + base.getDistance());
      System.out.println("I Founded distance:" + base.getDistance() + " angle:" + base.getAngle());
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
