package ballboy.view;

import ballboy.model.GameEngine;
import ballboy.model.Memento.CareTaker;
import ballboy.model.Memento.GameEngineMemento;
import ballboy.model.Memento.Memento;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

class KeyboardInputHandler {
    private GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    KeyboardInputHandler(GameEngine model) {
        this.model = model;
    }

    void handlePressed(KeyEvent keyEvent){
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.UP)) {
            if (model.boostHeight()) {
//                MediaPlayer jumpPlayer = sounds.get("jump");
//                jumpPlayer.stop();
//                jumpPlayer.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        } else if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }


        else if(keyEvent.getCode().equals(KeyCode.S)){
            model.saveGame();
        }

        else if(keyEvent.getCode().equals(KeyCode.Q)){

            model.loadGame();
        }

        else {
            return;
        }



        if (left) {
            model.moveLeft();
        } else {
            model.moveRight();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
        } else if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = false;
        } else {
            return;
        }

        if (!(right || left)) {
            model.dropHeight();
        } else if (right) {
            model.moveRight();
        } else {
            model.moveLeft();
        }
    }
}
