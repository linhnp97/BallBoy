package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * An aggressive strategy that makes the entity orbit around ballboy.
 * Position is calculated based on the position of ballboy.
 */
public class SquarecatBehaviorStrategy implements BehaviourStrategy {
    private List<Vector2D> listPos;
    private int currentPos = 0;
    private final Level level;
    private int xIncrement = 80;
    private int yIncrement = 80;

    public SquarecatBehaviorStrategy(Level level) {
        this.level = level;

    }

    @Override
    public void behave(
            DynamicEntity entity,
            double frameDurationMilli) {


        //Get Hero position
        Double xPos = level.getHeroX();
        Double yPos = level.getHeroY();


        entity.setPosition(new Vector2D(xPos + xIncrement, yPos + yIncrement));
        entity.setVelocity(entity.getVelocity().setX(0));
        entity.setVelocity(entity.getVelocity().setY(0));

        if(yIncrement == 80){
            xIncrement -=1;
        }

        if(xIncrement == -80){
            yIncrement -=1;
        }
        if(yIncrement == -80){
            xIncrement +=1;

        }
        if(xIncrement == 80){
            yIncrement +=1;
        }
    }

    public BehaviourStrategy copy(Level level){
        return new SquarecatBehaviorStrategy(level);
    }
}
