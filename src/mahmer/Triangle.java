package mahmer;

import edu.warbot.tools.geometry.CartesianCoordinates;
import edu.warbot.tools.geometry.PolarCoordinates;

public class Triangle {
    public static PolarCoordinates getAngleWithDistance(PolarCoordinates p1, PolarCoordinates p2) {
        CartesianCoordinates base_enemy = p1.toCartesian();
        CartesianCoordinates base_my = p2.toCartesian();
        double angle = CartesianCoordinates.getAngleBetween(base_enemy, base_my);
        double distance = CartesianCoordinates.distance(base_enemy.x, base_enemy.y, base_my.x, base_my.y);
        return new PolarCoordinates(distance, angle);
    }

    public static PolarCoordinates getAngleWithDistance(double phi1,
                                                        double distance1,
                                                        double phi2,
                                                        double distance2) {
        return getAngleWithDistance(new PolarCoordinates(distance1, phi1),
                new PolarCoordinates(distance2, phi2));
    }

}
