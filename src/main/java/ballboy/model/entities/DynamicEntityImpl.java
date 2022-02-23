package ballboy.model.entities;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.Observer.Observer;
import ballboy.model.Observer.Subject;
import ballboy.model.entities.behaviour.AggressiveEnemyBehaviourStrategy;
import ballboy.model.entities.behaviour.BehaviourStrategy;
import ballboy.model.entities.behaviour.ScaredEnemyBehaviourStrategy;
import ballboy.model.entities.behaviour.SquarecatBehaviorStrategy;
import ballboy.model.entities.collision.CollisionStrategy;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.KinematicState;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

public class DynamicEntityImpl extends DynamicEntity implements Cloneable {

    private final CollisionStrategy collisionStrategy;
    private final BehaviourStrategy behaviourStrategy;
    private final AxisAlignedBoundingBox volume;
    private final Layer layer;
    private final Image image;
    private String color;
    private final KinematicState kinematicState;

    /**
     * Constructors for colour coded enemy.
     */
    public DynamicEntityImpl (
            KinematicState kinematicState,
            AxisAlignedBoundingBox volume,
            Layer layer,
            Image image,
            CollisionStrategy collisionStrategy,
            BehaviourStrategy behaviourStrategy,
            String color
    ) {
        this.kinematicState = kinematicState;
        this.volume = volume;
        this.layer = layer;
        this.image = image;
        this.collisionStrategy = collisionStrategy;
        this.behaviourStrategy = behaviourStrategy;
        this.color = color;
    }

    public DynamicEntityImpl(
            KinematicState kinematicState,
            AxisAlignedBoundingBox volume,
            Layer layer,
            Image image,
            CollisionStrategy collisionStrategy,
            BehaviourStrategy behaviourStrategy
    ) {
        this.kinematicState = kinematicState;
        this.volume = volume;
        this.layer = layer;
        this.image = image;
        this.collisionStrategy = collisionStrategy;
        this.behaviourStrategy = behaviourStrategy;
        this.color = "";
    }

    /**
     * Copy constructors for this class.
     */
    public DynamicEntityImpl( DynamicEntityImpl d, Level level
    ) {
        this.kinematicState = d.kinematicState.copy();
        this.volume = d.volume;
        this.layer = d.layer;
        this.image = d.image;
        this.collisionStrategy = d.collisionStrategy.copy(level);
        this.behaviourStrategy = d.behaviourStrategy.copy(level);
        this.color = d.color;
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public Vector2D getPosition() {
        return kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D pos) {
        this.kinematicState.setPosition(pos);
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public Vector2D getVelocity() {
        return this.kinematicState.getVelocity();
    }

    @Override
    public void setVelocity(Vector2D vel) {
        this.kinematicState.setVelocity(vel);
    }

    @Override
    public double getHorizontalAcceleration() {
        return this.kinematicState.getHorizontalAcceleration();
    }

    @Override
    public void setHorizontalAcceleration(double horizontalAcceleration) {
        this.kinematicState.setHorizontalAcceleration(horizontalAcceleration);
    }

    @Override
    public double getHeight() {
        return volume.getHeight();
    }

    @Override
    public double getWidth() {
        return volume.getWidth();
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public String getColour() {
        return color;
    }

    @Override
    public boolean collidesWith(Entity entity) {
        return volume.collidesWith(entity.getVolume());
    }

    @Override
    public AxisAlignedBoundingBox getVolume() {
        return this.volume;
    }

    @Override
    public void collideWith(Entity entity) {
        collisionStrategy.collideWith(this, entity);
    }

    @Override
    public void update(
            double milliSeconds,
            double levelGravity) {
        kinematicState.update(milliSeconds, levelGravity);
        behaviourStrategy.behave(this, milliSeconds);
        this.volume.setTopLeft(this.kinematicState.getPosition());
    }
    @Override
    public Entity clone(Level level) {
       return new DynamicEntityImpl(this, level);
    }

}
