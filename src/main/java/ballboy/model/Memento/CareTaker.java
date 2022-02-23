package ballboy.model.Memento;

import ballboy.model.GameEngine;
import ballboy.view.GameWindow;
/**
 * Class that stores saved state (GameEngine) and reload it to the game
 */
public class CareTaker {
    private Memento memento;

    public void save(Memento state) {
        memento = state;

    }
    public Memento load() {
        return memento;
    }
}
