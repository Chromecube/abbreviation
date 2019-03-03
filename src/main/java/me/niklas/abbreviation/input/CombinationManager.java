package me.niklas.abbreviation.input;

import bsh.EvalError;
import bsh.Interpreter;
import me.niklas.abbreviation.display.PreviewManager;
import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.enums.GamepadInput;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import me.niklas.abbreviation.util.Combination;
import me.niklas.abbreviation.util.UtilMethods;
import me.niklas.abbreviation.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Niklas on 13.10.2018 in abbreviation
 */
public class CombinationManager implements EventSubscriber {

    private final List<Combination> combinations = new ArrayList<>();
    private final Interpreter interpreter;
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final PreviewManager preview = new PreviewManager();
    private final UtilMethods util;
    private final String defaultCode;
    private File directory;

    /**
     * @param events The {@link EventManager} used to make combinations able to fire events.
     */
    public CombinationManager(EventManager events) {
        this.util = new UtilMethods(events);
        this.interpreter = new Interpreter();

        defaultCode = "import me.niklas.abbreviation.enums.*;\n";

        try {
            interpreter.set("Logger", logger);
            interpreter.set("Utils", util);
            interpreter.set("VersionInfo", new VersionInfo());
            interpreter.set("Combinations", this);
            interpreter.set("EventManager", events);
        } catch (EvalError e) {
            logger.error("An error occurred while setting up the interpreter variables: ", e);
        }
    }

    /**
     * Registers a new {@link Combination}.
     *
     * @param combination The {@link Combination} Fails if the combination is invalid or if the same input is already
     *                    bound to another combination.
     */
    private void register(Combination combination) {
        if (combination.isInvalid()) {
            logger.warn("Invalid Combination, not adding it.");
            return;
        }

        GamepadInput[] indices = combination.getIndices();

        if (combinations.stream().anyMatch(c -> c.hasSameInput(indices))) {
            logger.warn("Combination with same input, not adding it to list: " + Arrays.toString(indices));
            return;
        }
        combinations.add(combination);
    }

    /**
     * Registers a new {@link Combination} by reading in a {@link File}.
     *
     * @param file The {@link File}. If it is empty, it will be deleted.
     */
    private void register(File file) {
        Combination c = new Combination(file);
        if (c.isEmptyCombination()) { //Delete empty combination file
            logger.info("Removing empty combination " + file.getName());
            try {
                logger.debug("Successfully deleted file?: " + file.delete());
            } catch (Exception e) {
                logger.error("Can not delete file:", e);
            }
            return;
        }

        register(c);
    }

    /**
     * Reloads all combinations. Also registers the prefabricated combinations (exit, reload, etc.)
     * <p>
     * Public because it is called by the reload combination.
     */
    @SuppressWarnings("WeakerAccess")
    public void reload() {
        util.showMessage("Reloading");
        combinations.clear();

        register(new Combination(new GamepadInput[]{GamepadInput.BACK_SELECT},
                "combination.setName(\"Application Setup\")", ""));
        register(new Combination(new GamepadInput[]{GamepadInput.BACK_SELECT, GamepadInput.DPAD_LEFT},
                "combination.setName(\"Reload\");", "Combinations.reload();"));
        register(new Combination(new GamepadInput[]{GamepadInput.BACK_SELECT, GamepadInput.DPAD_UP},
                "combination.setName(\"Exit\");", "Utils.exit();return\"Exiting\""));

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return;
        logger.debug(String.format("Found %d Combinations", files.length));
        for (File file : files) {
            register(file);
        }
    }

    /**
     * Edits a {@link Combination} using the default editor (determined by {@link Desktop#edit(File)}).
     *
     * @param combination The {@link Combination}. Prefabricated combinations can not be edited.
     */
    private void edit(Combination combination) {
        if (combination.isValidPrefabricatedCombination()) {
            util.showMessage("Can not edit prefabricated combinations");
            return;
        }
        preview.closePreview();
        combination.requireSetup();
        File file = combination.getFile();
        if (!file.exists() || !file.isFile()) {
            try {
                logger.debug("Created new file? " + file.createNewFile());
                combination.writeTemplate();
            } catch (IOException e) {
                logger.error("Could not create new Combination File: ", e);
            }
        }
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            logger.error("Could not open Editor: ", e);
        }
    }

    /**
     * Evaluates the {@code runScript} part of a {@link Combination}.
     *
     * @param combination The {@link Combination} to be executed.
     */
    private void invoke(Combination combination) {
        if (combination.isInvalid()) {
            logger.warn("Trying to run an invalid combination");
            return;
        }
        try {
            Object result = interpreter.eval(defaultCode + combination.getRunScript());

            if (result != null) {
                util.showMessage(result.toString());
            }
        } catch (EvalError evalError) {
            logger.error("Could not evaluate code: ", evalError);
            util.showMessage("Error: " + evalError.getErrorText() + "(Line " + evalError.getErrorLineNumber() + ")");
        }
    }

    /**
     * @return The directory where the combination files are located.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Opens a preview window for a {@link GamepadInput} array.
     *
     * @param current The current, but not final, input.
     */
    private void preview(GamepadInput[] current) {
        Combination precise = getCombinationForInput(current);
        List<Combination> possibilities = new ArrayList<>();

        //Find all possible (from start to current point matching) combinations. Maximum: 5.
        int index = 0;
        while (index < combinations.size() && possibilities.size() < 6) {
            Combination combination = combinations.get(index);
            if (combination.isPartOf(current)) possibilities.add(combination);
            index++;
        }

        preview.showPreview(precise, current, possibilities);
    }

    /**
     * A utility method for {@link #invoke(Combination)}, but checks the combination before it calls the method.
     *
     * @param combination The {@link Combination} which should be executes (the run script).
     */
    private void run(Combination combination) {
        if (combination == null || combination.isInvalid()) {
            util.showMessage("Not found");
        } else {
            invoke(combination);
        }
        preview.closePreview();
    }

    /**
     * Transforms a {@link GamepadInput} array into a {@link Combination} and runs it after that.
     *
     * @param current The current {@link GamepadInput}.
     */
    private void run(GamepadInput[] current) {
        run(getCombinationForInput(current));
    }

    /**
     * Finds a {@link Combination} by checking their input.
     *
     * @param input The {@link GamepadInput} array which should be found. Creates a new one by using
     *              {@link Combination#getCombinationForInput(GamepadInput[], File)} if none was found.
     * @return A matching {@link Combination}.
     */
    private Combination getCombinationForInput(GamepadInput[] input) {
        for (Combination combination : combinations) {
            if (combination.hasSameInput(input)) {
                return combination;
            }
        }

        return Combination.getCombinationForInput(input, directory);
    }

    /**
     * Handles:
     * - EDIT_COMBINATION (Parses and transfers it to {@link #edit(Combination)}
     * - RUN_COMBINATION (Parses and transfers it to {@link #run(GamepadInput[])}
     * - RELOAD (Performs the reload. With {@link String} param it changes the {@link #directory}, otherwise simple reload)
     * - SHOW_PREVIEW (Displays the preview using {@link #preview(GamepadInput[])}
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventType type, Object param) {
        if (type == EventType.EDIT_COMBINATION) {
            if (param instanceof GamepadInput[]) {
                edit(getCombinationForInput((GamepadInput[]) param));
            } else {
                logger.error("Invalid param at EDIT_COMBINATION");
            }
        } else if (type == EventType.RUN_COMBINATION) {
            if (param instanceof GamepadInput[]) {
                run((GamepadInput[]) param);
            } else {
                logger.error("Invalid param at RUN_COMBINATION");
            }
        } else if (type == EventType.RELOAD) {
            if (param instanceof String) {
                String path = (String) param;
                File dir = new File(path);

                if (path.length() == 0 || !dir.exists() || !dir.isDirectory()) {
                    dir = new File(System.getProperty("user.dir") + File.separator + "combinations");
                    if (!dir.exists()) logger.debug("Could create combinations directory? " + dir.mkdir());
                }

                directory = dir;
                reload();
            } else {
                if (directory != null) reload();
            }
        } else if (type == EventType.SHOW_PREVIEW) {
            if (param instanceof GamepadInput[]) {
                preview((GamepadInput[]) param);
            } else {
                logger.error("Invalid param at SHOW_PREVIEW");
            }
        }
    }

    @Override
    public void onExit() {
        logger.debug("Confirm shutdown");
        preview.shutdown();
    }
}
