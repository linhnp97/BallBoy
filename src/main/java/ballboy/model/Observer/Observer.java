package ballboy.model.Observer;

import javafx.scene.text.Text;

import java.util.List;

/**
 * Interface for concrete classes that needs to update
 * according to another class
 */
public interface Observer {
    void update();
    List<Text> getTotalScore();
    List<Text> getLevelScore();
}
