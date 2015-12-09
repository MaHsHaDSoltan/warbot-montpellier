package mahmer;

import java.util.List;

import edu.warbot.agents.agents.WarBase;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CartesianCoordinates;

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
      broadcastMessageToAll(Messsages.MESSAGE_BASE_ENEMY_DETECTED, _baseEnemyAngle + "", _baseEnemyDistance + "");
      setDebugString("Base Enemy Found " + _baseEnemyAngle + " " + _baseEnemyDistance);
      return;
    }

    for (WarMessage m : getMessages()) {
      if (!m.getMessage().equals(Messsages.MESSAGE_BASE_ENEMY_DETECTED))
        continue;
      double angle_exp_enemy = Double.parseDouble(m.getContent()[0]);
      double distance_exp_enemy = Double.parseDouble(m.getContent()[1]);

      System.out.println("Base Enemy Recived ");
      System.out.println(angle_exp_enemy + " " + distance_exp_enemy);
      System.out.println(m.getAngle() + " " + m.getDistance());

      Triangle tr = new Triangle(distance_exp_enemy, m.getDistance(), angle_exp_enemy + m.getAngle());
      _baseEnemyDistance = tr.getC();
      _baseEnemyAngle = tr.getBc();
      _baseDetected = true;

      setDebugString("Base Enemy Recived " + _baseEnemyDistance + " " + _baseEnemyAngle);
      System.out.println(_baseEnemyDistance + " " + _baseEnemyAngle);
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
