package ballboy.view;

import ballboy.model.GameEngine;
import javafx.scene.layout.Pane;


/**
 * Interface for objects that draw background for the game
 */
public interface BackgroundDrawer {
    void draw(
            GameEngine model,
            Pane pane);

    void update(
            double xViewportOffset,
            double yViewportOffset);
}
