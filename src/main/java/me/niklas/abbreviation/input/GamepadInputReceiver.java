package me.niklas.abbreviation.input;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.enums.GamepadInput;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Niklas on 13.10.2018 in abbreviation
 */
public class GamepadInputReceiver implements Runnable, EventSubscriber {

    private final ControllerManager manager = new ControllerManager();
    private final EventManager eventManager;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger("InputReceiver");
    private ControllerState state;
    private int last = 0;

    public GamepadInputReceiver(EventManager eventManager) {
        this.eventManager = eventManager;
        manager.initSDLGamepad();
        scheduler.scheduleAtFixedRate(this::run, 100, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * @return The current input state. The first pressed button is checked and if it ia not equal to
     * the last pressed one, it is returned.
     */
    private GamepadInput getCurrentInput() {
        if (!isControllerConnected()) return GamepadInput.NONE;
        int result = 0;

        if (state.start) {
            result = 1;
        } else if (state.a) {
            result = 2;
        } else if (state.x) {
            result = 3;
        } else if (state.y) {
            result = 4;
        } else if (state.b) {
            result = 5;
        } else if (state.dpadDown) {
            result = 6;
        } else if (state.dpadLeft) {
            result = 7;
        } else if (state.dpadUp) {
            result = 8;
        } else if (state.dpadRight) {
            result = 9;
        } else if (state.back) {
            result = 10;
        } else if (state.lb) {
            result = 11;
        } else if (state.rb) {
            result = 12;
        } else if (state.leftTrigger > 0.2) {
            result = 13;
        } else if (state.rightTrigger > 0.2) {
            result = 14;
        } else if (state.leftStickY < -0.5) { //Left Down
            result = 15;
        } else if (state.leftStickX < -0.5) { //Left Left
            result = 16;
        } else if (state.leftStickY > 0.5) { //Left Up
            result = 17;
        } else if (state.leftStickX > 0.5) { //Left Right
            result = 18;
        } else if (state.rightStickY < -0.5) { //Right Down
            result = 19;
        } else if (state.rightStickX < -0.5) { //Right Left
            result = 20;
        } else if (state.rightStickY > 0.5) { //Right Up
            result = 21;
        } else if (state.rightStickX > 0.5) { //Right Right
            result = 22;
        } else if (state.leftStickClick) { //Left Click
            result = 23;
        } else if (state.rightStickClick) { //Right Click
            result = 24;
        }


        if (result == last) {
            return GamepadInput.NONE;
        }
        last = result;
        return GamepadInput.valueOf(result);
    }

    /**
     * Updates the current state and checks whether a controller is connected.
     *
     * @return Whether a controller is connected.
     */
    public boolean isControllerConnected() {
        state = manager.getState(0);
        return state.isConnected;
    }

    /**
     * Repeatedly called in order to update the current input. If an input was found, the {@link EventType#TYPED} event is fired.
     */
    @Override
    public void run() {
        if (getCurrentInput() != GamepadInput.NONE) eventManager.fire(EventType.TYPED, GamepadInput.valueOf(last));
    }

    /**
     * Has to be implemented, but unused.
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventType type, Object param) {
    }

    /**
     * Shuts down the repeating task.
     */
    @Override
    public void onExit() {
        logger.debug("Confirm shutdown");
        scheduler.shutdownNow();
    }
}
