package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;

/**
 * Collision strategy injected into all concrete dynamic entities.
 * It should contain the non-physical entity-specific behaviour for collisions.
 */
public interface CollisionStrategy {

    void collideWith(
            Entity currentEntity,
            Entity hitEntity);


    /**
     * Return a deep copy of this Strategy
     */
    CollisionStrategy copy(Level level);

}
