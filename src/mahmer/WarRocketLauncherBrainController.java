package mahmer;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarRocketLauncherBrain;
import edu.warbot.communications.WarMessage;

public abstract class WarRocketLauncherBrainController extends WarRocketLauncherBrain {

  boolean _underattack;

  public WarRocketLauncherBrainController() {
    super();
    _underattack = false;
  }

  private void checkBaseAttack() {
    setDebugString("");
    for (WarMessage m : getMessages()) {
      if (m.getMessage().equals(Messsages.MESSAGE_UNDER_ATTACK)) {
        setDebugString("i am in my way");
        setHeading(m.getAngle());
        _underattack = true;
        return;
      }
    }

    if (_underattack)
      setDebugString("i am in my way");

  }

  private void checkBaseFound() {
    if (_underattack)
      return;
    for (WarMessage m : getMessages()) {
      if (m.getMessage().equals(Messsages.MESSAGE_BASE_ENEMY_DETECTED)) {
        setDebugString("to the enemy base");
        double angle_our_enmey = Double.parseDouble(m.getContent()[0]);
        double distance_our_enmey = Double.parseDouble(m.getContent()[1]);
        Triangle tr = new Triangle(distance_our_enmey, m.getDistance(), m.getAngle() + angle_our_enmey);
        //setHeading(tr.getCa());
      }
    }
  }


  private String tryAttack() {
    for (WarAgentPercept wp : getPerceptsEnemies()) {
      if (wp.getType().equals(WarAgentType.WarBase)) {
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
    checkBaseFound();


    String attack = tryAttack();
    if (attack != null) {
      return attack;
    }

    if (isBlocked())
      setRandomHeading();

    return ACTION_MOVE;
  }

}
