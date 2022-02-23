package ballboy.model.Observer;

import ballboy.model.Entity;
import ballboy.model.GameEngine;
import ballboy.model.entities.DynamicEntity;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Concrete class that update scores
 */
public class ScoreObserver implements Observer{
    private GameEngine model;
    private List<Text> totalScore;
    private List<Text> levelScore;

    public ScoreObserver(GameEngine model, List<Text> totalScore, List<Text> levelScore){
        this.totalScore = totalScore;
        this.model = model;
        this.levelScore = levelScore;
    }

    public void update(){
        totalScore.get(0).setText(String.valueOf(model.redScore()));
        totalScore.get(1).setText(String.valueOf(model.blueScore()));
        totalScore.get(2).setText(String.valueOf(model.yellowScore()));

        levelScore.get(0).setText(String.valueOf(model.getCurrentLevel().redScore()));
        levelScore.get(1).setText(String.valueOf(model.getCurrentLevel().blueScore()));
        levelScore.get(2).setText(String.valueOf(model.getCurrentLevel().yellowScore()));
    }

    public List<Text> getTotalScore(){
        return totalScore;
    }

    public List<Text> getLevelScore(){ return levelScore; }



}
