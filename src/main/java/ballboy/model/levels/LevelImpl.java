package ballboy.model.levels;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.Observer.Observer;
import ballboy.model.Observer.Subject;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.EntityFactory;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Level logic, with abstract factor methods.
 */
public class LevelImpl implements Level {
    private int red;
    private int blue;
    private int yellow;
    private List<Entity> entities = new ArrayList<>();
    private List<DynamicEntity> toDelete = new ArrayList<>();
    private final PhysicsEngine engine;
    private final EntityFactory entityFactory;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private Entity finish;
    private Entity squarecat;
    private boolean finished = false;
    private double levelHeight;
    private double levelWidth;
    private double levelGravity;
    private double floorHeight;
    private Color floorColor;

    private final double frameDurationMilli;

    /**
     * A callback queue for post-update jobs. This is specifically useful for scheduling jobs mid-update
     * that require the level to be in a valid state.
     */
    private final Queue<Runnable> afterUpdateJobQueue = new ArrayDeque<>();

    public LevelImpl(
            JSONObject levelConfiguration,
            PhysicsEngine engine,
            EntityFactory entityFactory,
            double frameDurationMilli) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        initLevel(levelConfiguration);
    }

    /**
     * Deep copy constructors for this object
     */
    public LevelImpl(LevelImpl levelImpl) {
        this.red = levelImpl.red;
        this.blue = levelImpl.blue;
        this.yellow = levelImpl.yellow;
        this.engine = levelImpl.engine;
        this.entityFactory = levelImpl.entityFactory;
        this.levelHeight = levelImpl.levelHeight;
        this.levelWidth = levelImpl.levelWidth;
        this.levelGravity = levelImpl.levelGravity;
        this.floorHeight = levelImpl.floorHeight;
        this.frameDurationMilli = levelImpl.frameDurationMilli;
        this.toDelete = levelImpl.toDelete;

        for(int i = 0; i < levelImpl.getEntities().size(); i++){
            Entity e = levelImpl.getEntities().get(i);
            if(!levelImpl.isFinish(e) && !levelImpl.isHero(e) && !levelImpl.isSquarecat(e)){
                Entity entity  = e.clone(this);
                this.entities.add(entity);
            }
        }


        this.hero = (ControllableDynamicEntity<DynamicEntity>) levelImpl.hero.clone(this);
        this.squarecat =  levelImpl.squarecat.clone(this);
        this.finish = levelImpl.finish.clone(this);

        entities.add(this.hero);
        entities.add(this.squarecat);
        entities.add(this.finish);



    }

    /**
     * Instantiates a level from the level configuration.
     *
     * @param levelConfiguration The configuration for the level.
     */
    private void initLevel(JSONObject levelConfiguration) {
        this.levelWidth = ((Number) levelConfiguration.get("levelWidth")).doubleValue();
        this.levelHeight = ((Number) levelConfiguration.get("levelHeight")).doubleValue();
        this.levelGravity = ((Number) levelConfiguration.get("levelGravity")).doubleValue();

        JSONObject floorJson = (JSONObject) levelConfiguration.get("floor");
        this.floorHeight = ((Number) floorJson.get("height")).doubleValue();

        String floorColorWeb = (String) floorJson.get("color");
        this.floorColor = Color.web(floorColorWeb);

        JSONArray generalEntities = (JSONArray) levelConfiguration.get("genericEntities");
        for (Object o : generalEntities) {
            this.entities.add(entityFactory.createEntity(this, (JSONObject) o));
        }

        JSONObject heroConfig = (JSONObject) levelConfiguration.get("hero");
        double maxVelX = ((Number) levelConfiguration.get("maxHeroVelocityX")).doubleValue();

        Object hero = entityFactory.createEntity(this, heroConfig);
        if (!(hero instanceof DynamicEntity)) {
            throw new ConfigurationParseException("hero must be a dynamic entity");
        }
        DynamicEntity dynamicHero = (DynamicEntity) hero;
        Vector2D heroStartingPosition = dynamicHero.getPosition();
        this.hero = new ControllableDynamicEntity<>(dynamicHero, heroStartingPosition, maxVelX, floorHeight,
                levelGravity);
        this.entities.add(this.hero);

        JSONObject finishConfig = (JSONObject) levelConfiguration.get("finish");
        this.finish = entityFactory.createEntity(this, finishConfig);
        this.entities.add(finish);

        JSONObject squarecatConfig = (JSONObject) levelConfiguration.get("squarecat");
        this.squarecat = entityFactory.createEntity(this, squarecatConfig);
        this.entities.add(squarecat);

    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    private List<DynamicEntity> getDynamicEntities() {
        return entities.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return entities.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public double getLevelHeight() {
        return this.levelHeight;
    }

    @Override
    public double getLevelWidth() {
        return this.levelWidth;
    }

    @Override
    public double getHeroHeight() {
        return hero.getHeight();
    }

    @Override
    public double getHeroWidth() {
        return hero.getWidth();
    }

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public Color getFloorColor() {
        return floorColor;
    }

    @Override
    public double getGravity() {
        return levelGravity;
    }

    @Override
    public void update() {
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        dynamicEntities.stream().forEach(e -> {
            e.update(frameDurationMilli, levelGravity);
        });

        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB)) {
                    dynamicEntityA.collideWith(dynamicEntityB);
                    dynamicEntityB.collideWith(dynamicEntityA);
                    if (!isHero(dynamicEntityA) && !isHero(dynamicEntityB)) {
                        engine.resolveCollision(dynamicEntityA, dynamicEntityB);
                    }
                }
            }

            for (StaticEntity staticEntity : getStaticEntities()) {
                if (dynamicEntityA.collidesWith(staticEntity)) {
                    dynamicEntityA.collideWith(staticEntity);
                    engine.resolveCollision(dynamicEntityA, staticEntity, this);
                }
            }
        }

        dynamicEntities.stream().forEach(e -> engine.enforceWorldLimits(e, this));

        afterUpdateJobQueue.forEach(j -> j.run());
        afterUpdateJobQueue.clear();

        for(int i = 0; i < toDelete.size(); i++){
            if(toDelete.get(i).getColour().equals("RED")){
                red +=1;
            }
            if(toDelete.get(i).getColour().equals("BLUE")){
                blue +=1;
            }
            if(toDelete.get(i).getColour().equals("YELLOW")){
                yellow+=1;
            }
        }

    }

    @Override
    public double getHeroX() {
        return hero.getPosition().getX();
    }

    @Override
    public double getHeroY() {
        return hero.getPosition().getY();
    }

    @Override
    public boolean boostHeight() {
        return hero.boostHeight();
    }

    @Override
    public boolean dropHeight() {
        return hero.dropHeight();
    }

    @Override
    public boolean moveLeft() {
        return hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return hero.moveRight();
    }

    @Override
    public boolean isHero(Entity entity) {
        return entity == hero;
    }

    @Override
    public boolean isSquarecat(Entity entity) {
        return entity == squarecat;
    }

    @Override
    public List<DynamicEntity> getToDelete() {
        return toDelete;
    }

    @Override
    public boolean isFinish(Entity entity) {
        return this.finish == entity;
    }

    @Override
    public void resetHero() {
        afterUpdateJobQueue.add(() -> this.hero.reset());
    }

    @Override
    public void finish() {
        finished = true;
    }

    @Override
    public int redScore() {
        return red;
    }

    @Override
    public int blueScore() {
        return blue;
    }

    @Override
    public int yellowScore() {
        return yellow;
    }

    @Override
    public boolean hasFinished() {
        return finished;
    }

}
