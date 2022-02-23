package ballboy.view;

import ballboy.ConfigurationParseException;
import ballboy.ConfigurationParser;
import ballboy.model.Entity;
import ballboy.model.GameEngine;
import ballboy.model.GameEngineImpl;
import ballboy.model.Level;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.behaviour.*;
import ballboy.model.entities.collision.*;
import ballboy.model.factories.*;
import ballboy.model.levels.LevelImpl;
import ballboy.model.levels.PhysicsEngine;
import ballboy.model.levels.PhysicsEngineImpl;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

class GameWindowTest {

    @BeforeAll
    public static void setupJavaFx() throws InterruptedException {
        Semaphore available = new Semaphore(0, true);
        Platform.startup(available::release);
        available.acquire();
    }


    @Test
    void Test(){

        ConfigurationParser configuration = new ConfigurationParser();
        JSONObject parsedConfiguration = null;
        try {
            parsedConfiguration = configuration.parseConfig("config.json");
        } catch (ConfigurationParseException e) {
            System.out.println(e);
            System.exit(-1);
        }

        final double frameDurationMilli = 17;
        PhysicsEngine engine = new PhysicsEngineImpl(frameDurationMilli);

        EntityFactoryRegistry entityFactoryRegistry = new EntityFactoryRegistry();
        entityFactoryRegistry.registerFactory("cloud", new CloudFactory());
        entityFactoryRegistry.registerFactory("enemy", new EnemyFactory());
        entityFactoryRegistry.registerFactory("background", new StaticEntityFactory(Entity.Layer.BACKGROUND));
        entityFactoryRegistry.registerFactory("static", new StaticEntityFactory(Entity.Layer.FOREGROUND));
        entityFactoryRegistry.registerFactory("finish", new FinishFactory());
        entityFactoryRegistry.registerFactory("hero", new BallboyFactory());
        entityFactoryRegistry.registerFactory("squarecat", new SquarecatFactory());

        Integer levelIndex = ((Number) parsedConfiguration.get("currentLevelIndex")).intValue();
        JSONArray levelConfigs = (JSONArray) parsedConfiguration.get("levels");

        ArrayList<Level> levels = new ArrayList<>();
        for(int i = 0 ; i < levelConfigs.size(); i++){
            levels.add(new LevelImpl((JSONObject) levelConfigs.get(i), engine, entityFactoryRegistry, frameDurationMilli));
        }

        GameEngine gameEngine = new GameEngineImpl(levels, levelIndex);

        GameWindow window = new GameWindow(gameEngine, 640, 400, frameDurationMilli);

        Level level = gameEngine.getCurrentLevel();

        assertEquals(2000, level.getLevelWidth(),0.1);

        assertFalse(level.hasFinished());


        assertEquals(0, level.blueScore(),0.1);
        assertEquals(0, level.redScore(),0.1);
        assertEquals(0, level.yellowScore(),0.1);

        assertEquals(0, gameEngine.blueScore());
        assertEquals(0, gameEngine.redScore());
        assertEquals(0, gameEngine.yellowScore());

        for(int i = 0; i < gameEngine.getCurrentLevel().getEntities().size(); i++){
            Entity e = gameEngine.getCurrentLevel().getEntities().get(i);
            if(gameEngine.getCurrentLevel().isHero(e)){
                ControllableDynamicEntity hero = (ControllableDynamicEntity) e;
                assertTrue(gameEngine.getCurrentLevel().isHero(e));
            }
            else{
                assertFalse(gameEngine.getCurrentLevel().isHero(e));
            }

            if(gameEngine.getCurrentLevel().isSquarecat(e)){
                assertTrue(gameEngine.getCurrentLevel().isSquarecat(e));
            }
            else{
                assertFalse(gameEngine.getCurrentLevel().isSquarecat(e));
            }

            if(gameEngine.getCurrentLevel().isFinish(e)){
                assertTrue(gameEngine.getCurrentLevel().isFinish(e));
            }
            else{
                assertFalse(gameEngine.getCurrentLevel().isFinish(e));
            }
        }

        assertEquals(17, gameEngine.getCurrentLevel().getEntities().size());
        for(int i = 0; i < 1000; i++){
            window.run();
        }

        gameEngine.moveLeft();
        gameEngine.moveLeft();
        gameEngine.moveLeft();


        for(int i = 0; i < 100; i++){
            window.run();
        }

        gameEngine.moveRight();
        gameEngine.saveGame();
        gameEngine.loadGame();
        for(int i = 0; i < 1000; i++){
            gameEngine.moveRight();
            gameEngine.boostHeight();
        }

        for(int i = 0; i < 1000; i++){
            gameEngine.moveLeft();
            gameEngine.boostHeight();
        }




        System.out.println(gameEngine.getCurrentLevel().getEntities().size());



    }

    @AfterAll
    public static void cleanupJavaFx() {
        Platform.exit();
    }

}