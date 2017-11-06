/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package Forms;

import asymmetricserver.Server;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for our 'Unlock' application, see 'Unlock.fxml'.
 * This class has all the logic to open the theater's doors using JavaFX
 * transitions.
 */
public final class UnlockController {

    @FXML
    private Server server;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML
    private Node root;
    @FXML // fx:id="pad"
    private Keypad pad; // Value injected by FXMLLoader
    @FXML // fx:id="error"
    private Rectangle error; // Value injected by FXMLLoader
    @FXML // fx:id="lock"
    private Button lock; // Value injected by FXMLLoader
    @FXML // fx:id="okleft"
    private Rectangle okleft; // Value injected by FXMLLoader
    @FXML // fx:id="okright"
    private Rectangle okright; // Value injected by FXMLLoader
    @FXML // fx:id="unlockbottom"
    private Rectangle unlockbottom; // Value injected by FXMLLoader
    @FXML // fx:id="unlocktop"
    private Rectangle unlocktop; // Value injected by FXMLLoader
    private boolean open = false;

    private FadeTransition fadeOut(final Duration duration, final Node node) {
        final FadeTransition fadeOut = new FadeTransition(duration, node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                node.setVisible(false);
            }
        });
        return fadeOut;
    }

    // Handler for Button[fx:id="lock"] onAction
    @FXML
    void unlockPressed(ActionEvent event) {
        // handle the event here
        lock.setDisable(true);
        pad.setDisable(true);
        root.requestFocus();

        final FadeTransition fadeLockButton = fadeOut(Duration.valueOf("1s"), lock);
        final HeightTransition openLockTop = new HeightTransition(Duration.valueOf("2s"), unlocktop);
        openLockTop.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                unlocktop.setVisible(false);
                unlocktop.setHeight(openLockTop.height);
            }
        });

        final HeightTransition openLockBottom = new HeightTransition(Duration.valueOf("2s"), unlockbottom);
        openLockBottom.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                unlockbottom.setVisible(false);
                unlockbottom.setHeight(openLockBottom.height);
            }
        });
        final ParallelTransition openLock = new ParallelTransition(openLockTop, openLockBottom);
        final SequentialTransition unlock = new SequentialTransition(fadeLockButton, openLock);
        unlock.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                pad.setDisable(false);
                //ok.getParent().getChildrenUnmodifiable().iterator().next().requestFocus();
            }
        });
        unlock.play();
    }

    private void resetVisibility() {
        pad.setOpacity(1.0);
        lock.setOpacity(1.0);
        pad.setVisible(true);
        pad.setDisable(true);
        lock.setVisible(true);
        lock.setDisable(false);
        okright.setVisible(true);
        okleft.setVisible(true);
        unlocktop.setVisible(true);
        unlockbottom.setVisible(true);
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert error != null : "fx:id=\"error\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert lock != null : "fx:id=\"lock\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert okleft != null : "fx:id=\"okleft\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert okright != null : "fx:id=\"okright\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert pad != null : "fx:id=\"pad\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert server != null : "fx:id=\"server\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert unlockbottom != null : "fx:id=\"unlockbottom\" was not injected: check your FXML file 'Unlock.fxml'.";
        assert unlocktop != null : "fx:id=\"unlocktop\" was not injected: check your FXML file 'Unlock.fxml'.";

        // set pin validation for the keypad
        pad.setValidateCallback(new ValidateCallback());

        // reset visibility and opacity of nodes - usefull if you left your
        // FXML in a 'bad' state
//        resetVisibility();
    }

    private void grantAccess() {
//        root.requestFocus();
        pad.setDisable(true);
        FadeTransition fadeOutPad = fadeOut(Duration.valueOf("1s"), pad);

        final WidthTransition openOkLeft =
                new WidthTransition(Duration.valueOf("2s"), okleft);
        openOkLeft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                okleft.setVisible(false);
                okleft.setWidth(openOkLeft.width);
            }
        });

        final WidthTransition openOkRight =
                new WidthTransition(openOkLeft.getDuration(), okright);
        openOkRight.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                okright.setVisible(false);
                okright.setWidth(openOkRight.width);
            }
        });

        final ParallelTransition openOk =
                new ParallelTransition(openOkLeft, openOkRight);

        final SequentialTransition okTrans =
                new SequentialTransition(fadeOutPad, openOk);
        okTrans.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                open = true;
                root.requestFocus();
            }
        });
        okTrans.play();
    }

    private void rejectAccess() {
        FadeTransition errorTrans = new FadeTransition(Duration.valueOf("500ms"), error);
        errorTrans.setFromValue(0.0);
        errorTrans.setToValue(1.0);
        errorTrans.setCycleCount(2);
        errorTrans.setAutoReverse(true);
        errorTrans.play();
    }

    private final static class HeightTransition extends Transition {

        final Rectangle node;
        final double height;

        public HeightTransition(Duration duration, Rectangle node) {
            this(duration, node, node.getHeight());
        }

        public HeightTransition(Duration duration, Rectangle node, double height) {
            this.node = node;
            this.height = height;
            this.setCycleDuration(duration);
        }

        public Duration getDuration() {
            return getCycleDuration();
        }

        @Override
        protected void interpolate(double frac) {
            this.node.setHeight((1.0 - frac) * height);
        }
    }

    private final static class WidthTransition extends Transition {

        final Rectangle node;
        final double width;

        public WidthTransition(Duration duration, Rectangle node) {
            this(duration, node, node.getWidth());
        }

        public WidthTransition(Duration duration, Rectangle node, double width) {
            this.node = node;
            this.width = width;
            this.setCycleDuration(duration);
        }

        public Duration getDuration() {
            return getCycleDuration();
        }

        @Override
        protected void interpolate(double frac) {
            this.node.setWidth((1.0 - frac) * width);
        }
    }

    private final class ValidateCallback implements Callback<String, Boolean> {
        private ValidateCallback() {
        }

        @Override
        public Boolean call(String param) {
            grantAccess();
            return true;
            /*final boolean accessGranted = "1234".equals(param);
            if (accessGranted) {
                grantAccess();
            } else {
                rejectAccess();
            }
            return accessGranted;*/
        }

    }
}
