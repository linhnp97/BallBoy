package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.behaviour.AggressiveEnemyBehaviourStrategy;
import ballboy.model.entities.behaviour.PassiveEntityBehaviourStrategy;
import ballboy.model.entities.behaviour.SquarecatBehaviorStrategy;
import ballboy.model.entities.collision.BallboyCollisionStrategy;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.collision.PassiveCollisionStrategy;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.KinematicState;
import ballboy.model.entities.utilities.KinematicStateImpl;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

/*
 * Concrete entity factory for Squarecat.
 */
public class SquarecatFactory  implements EntityFactory {

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double startX = ((Number) config.get("posX")).doubleValue();
            double startY = ((Number) config.get("posY")).doubleValue();

            String imageName = (String) config.getOrDefault("image", "squarecat.png");
            String size = (String) config.get("size");

            double height;
            if (size.equals("small")) {
                height = 10.0/3;
            } else if (size.equals("medium")) {
                height = 25.0/3;
            } else if (size.equals("large")) {
                height = 50.0/3;
            } else {
                throw new ConfigurationParseException(String.format("Invalid squarecat size %s", size));
            }

            Image image = new Image(imageName);
            // preserve image ratio
            double width = height * image.getWidth() / image.getHeight();

            Vector2D startingPosition = new Vector2D(startX, startY);

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(startingPosition)
                    .build();

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    startingPosition,
                    height,
                    width
            );

            return new DynamicEntityImpl(
                    kinematicState,
                    volume,
                    Entity.Layer.FOREGROUND,
                    new Image(imageName),
                    new PassiveCollisionStrategy(),
                    new SquarecatBehaviorStrategy( level)
            );

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid squarecat entity configuration"));
        }
    }
}
