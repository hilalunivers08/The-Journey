package thejourney.world;

import java.awt.Graphics2D;

public interface Renderable {

    double getSortY();

    void render(Graphics2D g2, double alpha);
}
