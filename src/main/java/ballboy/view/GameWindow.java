package ballboy.view;

import ballboy.model.Entity;
import ballboy.model.GameEngine;
import ballboy.model.Observer.ScoreObserver;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to visualise the all components to the screen
 */
public class GameWindow {
    private static final double VIEWPORT_MARGIN_X = 100;
    private static final double VIEWPORT_MARGIN_Y = 50;
    private final int width;
    private final int height;
    private final double frameDurationMilli;
    private final Scene scene;
    private final Pane pane;
    private GameEngine model;
    private final List<EntityView> entityViews;
    private final BackgroundDrawer backgroundDrawer;
    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;

    public GameWindow(
            GameEngine model,
            int width,
            int height,
            double frameDurationMilli) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.frameDurationMilli = frameDurationMilli;
        pane = new Pane();
        scene = new Scene(pane, width, height);

        entityViews = new ArrayList<>();

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        backgroundDrawer = new BlockedBackground();
        backgroundDrawer.draw(model, pane);

        Text score = new Text();
        score.setText("Total Enemy Killed:");
        score.setX(50);
        score.setY(50);
        score.setStyle("-fx-font-family: 'serif'");

        Text totalRed = new Text();
        totalRed.setText("0");
        totalRed.setX(200);
        totalRed.setY(50);
        totalRed.setY(50);
        totalRed.setFill(Paint.valueOf("RED"));
        totalRed.setStyle("-fx-font-family: 'serif'");

        Text totalBlue = new Text();
        totalBlue.setText("0");
        totalBlue.setX(250);
        totalBlue.setY(50);
        totalBlue.setFill(Paint.valueOf("BLUE"));
        totalBlue.setStyle("-fx-font-family: 'serif'");

        Text totalYellow = new Text();
        totalYellow.setText("0");
        totalYellow.setX(300);
        totalYellow.setY(50);
        totalYellow.setStyle("-fx-font-family: 'serif'");
        totalYellow.setFill(Paint.valueOf("YELLOW"));

        ArrayList<Text> texts = new ArrayList<>();
        texts.add(totalRed);
        texts.add(totalBlue);
        texts.add(totalYellow);

        pane.getChildren().add(totalRed);
        pane.getChildren().add(totalBlue);
        pane.getChildren().add(totalYellow);
        pane.getChildren().add(score);

        Text levelScore = new Text();
        levelScore.setText("Enemy Killed This Level:");
        levelScore.setX(50);
        levelScore.setY(100);
        levelScore.setStyle("-fx-font-family: 'serif'");


        Text red = new Text();
        red.setText("0");
        red.setX(200);
        red.setY(100);
        red.setFill(Paint.valueOf("RED"));
        red.setStyle("-fx-font-family: 'serif'");

        Text blue = new Text();
        blue.setText("0");
        blue.setX(250);
        blue.setY(100);
        blue.setFill(Paint.valueOf("BLUE"));
        blue.setStyle("-fx-font-family: 'serif'");

        Text yellow = new Text();
        yellow.setText("0");
        yellow.setX(300);
        yellow.setY(100);
        yellow.setStyle("-fx-font-family: 'serif'");
        yellow.setFill(Paint.valueOf("YELLOW"));

        ArrayList<Text> levelScoreTracker = new ArrayList<>();
        levelScoreTracker.add(red);
        levelScoreTracker.add(blue);
        levelScoreTracker.add(yellow);

        pane.getChildren().add(levelScore);
        pane.getChildren().add(red);
        pane.getChildren().add(blue);
        pane.getChildren().add(yellow);

        model.registerObserver(new ScoreObserver(model, texts, levelScoreTracker));
    }

    public Scene getScene() {
        return scene;
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameDurationMilli),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void draw() {
        if(model.gameEnded()){
            Text winner = new Text();
            winner.setText("WINNER");
            winner.setX(250);
            winner.setY(300);
            winner.setFill(Paint.valueOf("RED"));
            winner.setStyle("-fx-font-family: 'serif'");
            pane.getChildren().add(winner);
        }

        model.tick();

        for(int i = 0 ; i < model.getCurrentLevel().getToDelete().size(); i++){
            for(EntityView view: entityViews){
                if(view.matchesEntity(model.getCurrentLevel().getToDelete().get(i))){
                    pane.getChildren().remove(view.getNode());
                }
            }
        }

        model.getCurrentLevel().getToDelete().clear();


        List<Entity> entities = model.getCurrentLevel().getEntities();
        for (EntityView entityView : entityViews) {
            entityView.markForDelete();
        }

        double heroXPos = model.getCurrentLevel().getHeroX();
        double viewportLeftBar = xViewportOffset + VIEWPORT_MARGIN_X;
        double viewportRightBar = viewportLeftBar + (width - 2 * VIEWPORT_MARGIN_X);

        if (heroXPos < viewportLeftBar) {
            xViewportOffset -= heroXPos - viewportLeftBar;
        } else if (heroXPos + model.getCurrentLevel().getHeroWidth() > viewportRightBar) {
            xViewportOffset += heroXPos + model.getCurrentLevel().getHeroWidth() - viewportRightBar;
        }

        heroXPos -= xViewportOffset;

        if (heroXPos < VIEWPORT_MARGIN_X) {
            if (xViewportOffset >= 0) { // Don't go further left than the start of the level
                xViewportOffset -= VIEWPORT_MARGIN_X - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        }

        double levelRight = model.getCurrentLevel().getLevelWidth();
        double screenRight = xViewportOffset + width - model.getCurrentLevel().getHeroWidth();
        if (screenRight > levelRight) {
            xViewportOffset = levelRight - width + model.getCurrentLevel().getHeroWidth();
        }


        double levelTop = 0.0;
        double levelBottom = model.getCurrentLevel().getLevelHeight();
        double heroYPos = model.getCurrentLevel().getHeroY();
        double heroHeight = model.getCurrentLevel().getHeroHeight();
        double viewportTop = yViewportOffset + VIEWPORT_MARGIN_Y;
        double viewportBottom = yViewportOffset + height - 2 * VIEWPORT_MARGIN_Y;

        if (heroYPos + heroHeight > viewportBottom) {
            // if below, shift down
            yViewportOffset += heroYPos + heroHeight - viewportBottom;
        } else if (heroYPos < viewportTop) {
            // if above, shift up
            yViewportOffset -= viewportTop - heroYPos;
        }

        double screenBottom = yViewportOffset + height;
        double screenTop = yViewportOffset;
        // shift back in the instance when we're near the boundary
        if (screenBottom > levelBottom) {
            yViewportOffset -= screenBottom - levelBottom;
        } else if (screenTop < 0.0) {
            yViewportOffset -= screenTop;
        }



        backgroundDrawer.update(xViewportOffset, yViewportOffset);

        for (Entity entity : entities) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }

        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

}
