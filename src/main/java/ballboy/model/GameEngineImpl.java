package ballboy.model;
import ballboy.model.Memento.CareTaker;
import ballboy.model.Memento.GameEngineMemento;
import ballboy.model.Memento.Memento;
import ballboy.model.Observer.ScoreObserver;
import ballboy.model.Observer.Observer;
import ballboy.model.Observer.Subject;
import ballboy.model.Prototype.Prototype;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.levels.LevelImpl;
import ballboy.view.GameWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GameEngine interface.
 * This provides a common interface for the entire game.
 */
public class GameEngineImpl implements GameEngine, Subject, Prototype {
    private Observer observer;
    private Level level;
    private final ArrayList<Level> levels;
    private final int totalLevel;
    private int currentLevelIndex;
    private int red = 0 ;
    private int blue = 0;
    private int yellow = 0;
    private boolean gameEnded = false;
    private CareTaker careTaker;

    public GameEngineImpl(ArrayList<Level> levels, int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
        this.levels = levels;
        this.level = levels.get(currentLevelIndex);
        this.totalLevel = levels.size();
        careTaker = new CareTaker();
    }

    /**
     * Copy constructor for GameEngine
     */
    public GameEngineImpl(GameEngineImpl gameEngineImpl) {
        this.red = gameEngineImpl.red;
        this.blue = gameEngineImpl.blue;
        this.yellow = gameEngineImpl.yellow;
        this.currentLevelIndex = gameEngineImpl.currentLevelIndex;
        this.levels = new ArrayList<>();
        for(int i = 0; i < gameEngineImpl.levels.size(); i++){
            Level l =new LevelImpl((LevelImpl) gameEngineImpl.levels.get(i));
            levels.add(l);
        }
        this.currentLevelIndex = gameEngineImpl.currentLevelIndex;
        this.totalLevel = levels.size();
        this.level = this.levels.get(currentLevelIndex);
        this.observer = new ScoreObserver(this,gameEngineImpl.observer.getTotalScore(), gameEngineImpl.observer.getLevelScore());
        this.gameEnded = gameEngineImpl.gameEnded;
    }

    public Level getCurrentLevel() {
        return level;
    }

    public void startLevel() {
        // TODO: Handle when multiple levels has been implemented
        if(level.hasFinished()){
            if(currentLevelIndex + 1 == totalLevel){
                level.getEntities().clear();
                this.gameEnded = true;
            }
            else{
                currentLevelIndex +=1;
                level = levels.get(currentLevelIndex);
            }
        }
    }

    public boolean boostHeight() {
        return level.boostHeight();
    }

    public boolean dropHeight() {
        return level.dropHeight();
    }

    public boolean moveLeft() {
        return level.moveLeft();
    }

    public boolean moveRight() {
        return level.moveRight();
    }

    public void tick() {
        level.update();
        notifyObservers();
        startLevel();

        List<DynamicEntity> toDelete = level.getToDelete();
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
    public void registerObserver(Observer o) {
        observer = o;
    }

    @Override
    public void notifyObservers() {
      observer.update();
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
    public GameEngine copy() {
        return new GameEngineImpl(this);
    }

    @Override
    public boolean gameEnded() {
        return gameEnded;
    }

    @Override
    public Memento createMemento() {
        return new GameEngineMemento(this);
    }

    @Override
    public void setMemento(Memento m) {
        GameEngine toLoad = m.restore();
        this.level =toLoad.getCurrentLevel();
        this.gameEnded =toLoad.gameEnded();
        this.yellow = toLoad.yellowScore();
        this.blue = toLoad.blueScore();
        this.red = toLoad.redScore();
    }

    @Override
    public void saveGame() {
        this.careTaker.save(this.createMemento());

    }

    @Override
    public void loadGame() {
        this.setMemento(careTaker.load());
    }


}