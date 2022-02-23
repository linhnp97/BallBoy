package ballboy.model.Memento;

import ballboy.model.GameEngine;

/**
 * interface for concrete classes that captures the sate of another class
 */
public interface Memento {
    GameEngine restore();
}
