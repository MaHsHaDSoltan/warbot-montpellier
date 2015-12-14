package mahmer;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarRocketLauncherBrain;
import edu.warbot.communications.WarMessage;

public abstract class WarRocketLauncherBrainController extends WarRocketLauncherBrain {

  boolean _defend;
  boolean _attack;

  public WarRocketLauncherBrainController() {
    super();
    _defend = false;
    _attack = false;
  }

  private void checkBaseAttack() {
    setDebugString("");
    for (WarMessage m : getMessages()) {
      if (m.getMessage().equals(Messsages.MESSAGE_UNDER_ATTACK)) {
        setDebugString("i am in my way");
        setHeading(m.getAngle());
        _defend = true;
        return;
      }
    }

    if (_defend)
      setDebugString("i am in my way");

  }

  private void checkBaseOrder() {
      if(_attack) {
          return;
      }
    for (WarMessage m : getMessages()) {
      if (m.getMessage().equals(Messsages.MESSAGE_BASE_ORDER_GO_TO)) {
        double angle = Double.parseDouble(m.getContent()[0]);
        setDebugString("i will go to "+ angle);
        System.out.println(toString() +"Direction "+angle);
        setHeading(angle);
      } else if (m.getMessage().equals(Messsages.MESSAGE_BASE_ORDER_ATTACK_AT)){
          double angle = Double.parseDouble(m.getContent()[0]);
          setDebugString("to the enemy base at "+ angle);
          System.out.println(getID()  +"Direction "+angle);
          setHeading(angle);
          _attack = true;
      }
    }
    
    // send my position at base
    broadcastMessageToAgentType(WarAgentType.WarBase, Messsages.MESSAGE_BASE_LOCATION);
  }
  
  


  private String tryAttack() {
    for (WarAgentPercept wp : getPerceptsEnemies()) {
      if (wp.getType().equals(WarAgentType.WarBase)) {
          _attack= false;
        setHeading(wp.getAngle());
        if (isReloaded())
          return ACTION_FIRE;
        else if (isReloading())
          return ACTION_IDLE;
        else
          return ACTION_RELOAD;
      }

      if (wp.getType().equals(WarAgentType.WarRocketLauncher)) {
        setHeading(wp.getAngle());
        if (isReloaded())
          return ACTION_FIRE;
        else if (isReloading())
          return ACTION_IDLE;
        else
          return ACTION_RELOAD;
      }


    }

    return null;
  }

  @Override
  public String action() {
    checkBaseAttack();
    checkBaseOrder();


    String attack = tryAttack();
    if (attack != null) {
      return attack;
    }

    if (isBlocked())
      setRandomHeading();

    return ACTION_MOVE;
  }

}
