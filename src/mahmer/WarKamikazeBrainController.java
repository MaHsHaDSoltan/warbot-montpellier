package mahmer;

import java.util.List;

import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarKamikazeBrain;

public abstract class WarKamikazeBrainController extends WarKamikazeBrain {

    public WarKamikazeBrainController() {
        super();
    }

    @Override
    public String action() {
        List <WarAgentPercept> percepts = getPercepts();

        for (WarAgentPercept p : percepts) {
            switch (p.getType()) {
                case WarBase:
                    if (isEnemy(p)) {
                        broadcastMessageToAll("Ennemi Base Found", String.valueOf(p.getAngle()), String.valueOf(p.getDistance()));
                    }
                    break;
                default:
                    break;
            }
        }

        if (isBlocked())
            setRandomHeading();
        return WarExplorer.ACTION_MOVE;
    }
}
