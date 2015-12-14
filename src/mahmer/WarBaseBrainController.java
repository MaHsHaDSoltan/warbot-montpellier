package mahmer;

import java.util.List;

import edu.warbot.agents.agents.WarBase;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CartesianCoordinates;
import edu.warbot.tools.geometry.PolarCoordinates;

public abstract class WarBaseBrainController extends WarBaseBrain {

  private boolean _alreadyCreated;
  int _lastHealth = -1;

  boolean _baseDetected;
  double _baseEnemyAngle;
  double _baseEnemyDistance;

  public WarBaseBrainController() {
    super();
    _alreadyCreated = false;
    _baseDetected = false;
  }

  private void checkFindBase() {

    if (_baseDetected) {
      return;
    }

    for (WarMessage m : getMessages()) {
      if (!m.getMessage().equals(Messsages.MESSAGE_BASE_ENEMY_DETECTED))
        continue;
      
      _baseEnemyAngle = Double.parseDouble(m.getContent()[0]);
      _baseEnemyDistance = Double.parseDouble(m.getContent()[1]);
      _baseDetected = true;
      setDebugString("Base Enemy Recived " + _baseEnemyDistance + " " + _baseEnemyAngle);
      System.out.println("Base Enemy at distance :"+_baseEnemyDistance + " angle:" + _baseEnemyAngle);
    }
  }
  
  
  


  private String checkCreated() {
    if (!_alreadyCreated) {
      setNextAgentToCreate(WarAgentType.WarEngineer);
      _alreadyCreated = true;
      return WarBase.ACTION_CREATE;
    }

    if (getMaxHealth() == getHealth()) {
      _alreadyCreated = true;
    }

    return null;

  }

  
  private void checkAskLocation(){
      for(WarMessage m: getMessages()){
          if(!m.getMessage().equals(Messsages.MESSAGE_BASE_LOCATION)) continue;
              
          switch (m.getSenderType()) {
            case WarRocketLauncher: {
                if (_baseDetected) {
                    
                    
                    PolarCoordinates result = Triangle.getAngleWithDistance(_baseEnemyAngle, _baseEnemyDistance, m.getAngle(), m.getDistance());
                    broadcastMessageToAgentType(WarAgentType.WarRocketLauncher,Messsages.MESSAGE_BASE_ORDER_ATTACK_AT, result.getAngle()+"");
                    System.err.println("Base Send to "+ m.getSenderID() +" "+result.getAngle());
                    return;
                  }
            }

            default:
                reply(m, Messsages.MESSAGE_BASE_LOCATION);
                break;
        }
              
      }
  }
  
  private void setLastHealth() {
    if (_lastHealth == -1)
      _lastHealth = getHealth();
  }

  private void checkUnderAttack() {
    if (getHealth() < _lastHealth && _lastHealth != -1) {
      System.out.println("HEALTH 2 " + _lastHealth + " " + getHealth());
      setDebugString("under attack");
      broadcastMessageToAll(Messsages.MESSAGE_UNDER_ATTACK, "");
      _lastHealth = getHealth();
    } else {
      setDebugString("i am ok");
    }
  }

  @Override
  public String action() {
    String create = checkCreated();
    if (create != null) {
      return create;
    }
    setLastHealth();
    checkUnderAttack();
    checkFindBase();
    checkAskLocation();
    
    if (getNbElementsInBag() >= 0 && getHealth() <= 0.8 * getMaxHealth())
      return WarBase.ACTION_EAT;

    List<WarMessage> messages = getMessages();

    for (WarMessage message : messages) {
      if (message.getMessage().equals("Where is the base"))
        reply(message, "I'm here");
    }

    for (WarAgentPercept percept : getPerceptsResources()) {
      if (percept.getType().equals(WarAgentType.WarFood))
        broadcastMessageToAgentType(WarAgentType.WarExplorer, "I detected food", String.valueOf(percept.getAngle()),
            String.valueOf(percept.getDistance()));
    }

    return WarBase.ACTION_IDLE;
  }

}
