package ballboy.model;

import ballboy.model.Memento.Memento;
import ballboy.model.Observer.Observer;

import java.util.List;

/**
 * The base interface for interacting with the Ballboy model
 */
public interface GameEngine {
    /**
     * Return the currently loaded level
     *
     * @return The current level
     */
    Level getCurrentLevel();

    /**
     * Start the level
     */
    void startLevel();

    /**
     * Increases the bounce height of the current hero.
     *
     * @return boolean True if the bounce height of the hero was successfully boosted.
     */
    boolean boostHeight();

    /**
     * Reduces the bounce height of the current hero.
     *
     * @return boolean True if the bounce height of the hero was successfully dropped.
     */
    boolean dropHeight();

    /**
     * Applies a left movement to the current hero.
     *
     * @return True if the hero was successfully moved left.
     */
    boolean moveLeft();

    /**
     * Applies a right movement to the current hero.
     *
     * @return True if the hero was successfully moved right.
     */
    boolean moveRight();

    /**
     * Instruct the model to progress forward in time by one increment.
     */
    void tick();

    /**
     * Add observer to a stored field for GameEngine.
     */
    void registerObserver(Observer o) ;

    /**
     * send notification for observers to update
     */
    void notifyObservers();

    /**
     * return score for killing red enemies
     */
    int redScore();

    /**
     * return score for killing blue enemies
     */
    int blueScore();

    /**
     * return score for killing yellow enemies
     */
    int yellowScore();

    /**
     * Deep copy method for gameEngine
     */
    GameEngine copy();

    /**
     * return whether user has finished all levels
     */
    boolean gameEnded();

    /**
     * Create Memento for save state
     */
    Memento createMemento();

    /**
     * Extract saved state from Memento
     */
    void setMemento(Memento m);

    /**
     * Save state of the GameEngine
     */
    void saveGame();

    /**
     * Load game back to saved State
     */
    void loadGame();



}
