package ballboy.model.Memento;

import ballboy.model.GameEngine;
/**
 * Concrete classes that keeps the state (GameEngine)
 */
public class GameEngineMemento implements Memento {
    GameEngine engine;
    public GameEngineMemento(GameEngine engine) {
        this.engine = engine.copy();
    }
    public GameEngine restore(){
        return engine.copy();}
}
