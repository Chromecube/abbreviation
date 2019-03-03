package me.niklas.abbreviation.util;

import bsh.Interpreter;
import me.niklas.abbreviation.enums.GamepadInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Niklas on 13.10.2018 in abbreviation
 */
public class Combination {

    private final File file;
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private String name;
    private GamepadInput[] indices;
    private boolean valid;
    private String initScript = "";
    private String runScript = "";
    private boolean requiresSetup = true;

    /**
     * @param file The {@link File} which should be read.
     */
    public Combination(File file) {
        Objects.requireNonNull(file);
        this.file = file;

        setup();
    }

    /**
     * (This constructor is for prefabricated scripts only)
     *
     * @param indices    The Combination which has to be entered to trigger this action.
     * @param initScript The initialization script.
     * @param runScript  The run script.
     */
    public Combination(GamepadInput[] indices, String initScript, String runScript) {
        this.file = null;
        requiresSetup = false;

        if (indices == null) {
            valid = false;
            return;
        }
        valid = true;
        this.indices = indices;
        this.initScript = initScript != null ? initScript : "";
        this.runScript = runScript != null ? runScript : "";
        executeInitScript();
    }

    /**
     * Generates a file name based on {@link GamepadInput}s.
     *
     * @param inputs The {@link GamepadInput}s.
     * @return A qualified file name. Based on the index of the {@link GamepadInput}, separated by a '-'.
     */
    private static String getFileNameForInputs(GamepadInput[] inputs) {
        StringBuilder builder = new StringBuilder();
        for (GamepadInput input : inputs) {
            builder.append(input.ordinal()).append("-");
        }
        String name = builder.toString();
        return name.substring(0, name.length() - 1) + ".txt";
    }

    /**
     * @param inputs    The {@link GamepadInput}s.
     * @param directory The directory where the file should be in.
     * @return The combination file.
     * @see #getFileNameForInputs(GamepadInput[])
     * <p>
     * Returns a file according to the {@link GamepadInput}s of a combination.
     */
    private static File getFileForInput(GamepadInput[] inputs, File directory) {
        return new File(directory.getAbsolutePath() + File.separatorChar + getFileNameForInputs(inputs));
    }

    /**
     * @param inputs    The {@link GamepadInput}s
     * @param directory The directory where the file should be in.
     * @return The combination.
     * @see #getFileNameForInputs(GamepadInput[])
     * @see #getFileForInput(GamepadInput[], File)
     * <p>
     * Returns a Combination based on {@link GamepadInput}s.
     */
    public static Combination getCombinationForInput(GamepadInput[] inputs, File directory) {
        return new Combination(getFileForInput(inputs, directory));
    }

    /**
     * Sets up the class. Reads in the file and executes the init script.
     */
    private void setup() {
        if (!requiresSetup) return;
        requiresSetup = false;

        //Create File if it does not exist
        if (!file.exists() || !file.isFile()) {
            valid = false;
            indices = new GamepadInput[0];
            return;
        }

        //Read indices from file name
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        String[] strings = name.split("-");
        int[] numbers = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                numbers[i] = Integer.valueOf(strings[i]);
            } catch (NumberFormatException e) {
                logger.error("Could not format number: " + e);
                valid = false;
                indices = new GamepadInput[0];
                return;
            }
        }

        indices = GamepadInput.toGamepadInputs(numbers);

        //Read scripts
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            StringBuilder tmp = new StringBuilder();
            String line;
            boolean inRunScriptPart = true; //False -> In init script. True -> In run script-

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("//")) continue; //Skip comments
                if (line.startsWith(":")) {
                    if (line.startsWith(":init")) {
                        runScript += tmp.toString();
                        tmp.setLength(0);
                        inRunScriptPart = false;
                    } else if (line.startsWith(":run")) {
                        initScript += tmp.toString();
                        tmp.setLength(0);
                        inRunScriptPart = true;
                    }
                } else {
                    tmp.append(line).append('\n');
                }
            }
            if (tmp.length() > 0) { //Something is stored -> Add to runScript or initScript part
                if (inRunScriptPart) {
                    runScript += tmp;
                } else {
                    initScript += tmp;
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while reading from combination file: ", e);
        }

        executeInitScript();

        valid = true;
    }

    /**
     * Write the template file to the {@link File}.
     */
    public void writeTemplate() {
        if (!file.exists() || !file.isFile()) return;
        try (var reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("template.txt"))
                ));
             var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
        ) {
            int current;
            while ((current = reader.read()) != -1) {
                writer.write(current);
            }
        } catch (Exception e) {
            logger.error("Error while writing template: ", e);
        }
    }

    /**
     * Executes the initialization script. Called when the Combination is set up correctly.
     * <i>Can</i>, but should not be called multiple times.
     */
    private void executeInitScript() {
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.set("combination", this);
            interpreter.eval(initScript);
        } catch (Exception e) {
            logger.error("An error occurred while running the init script: ", e);
        }
    }

    /**
     * Check whether the {@code part} of {@link GamepadInput}s is apart of the own {@link #indices}.
     *
     * @param part An array of {@link GamepadInput}.
     * @return Whether this {@code part} is part of the own {@link #indices} array.
     */
    public boolean isPartOf(GamepadInput[] part) {
        if (!valid) return false; //This combination is invalid
        if (requiresSetup) setup(); //Set up if not done yet
        if (part.length >= indices.length) return false; //The part's length is larger than the own -> impossible
        for (int i = 0; i < part.length; i++) { //If any input varies -> return false
            if (!part[i].equals(indices[i])) return false;
        }
        return true;
    }

    /**
     * @param part The part which is already given.
     * @return A readable version from the end of the part to the end of the {@link #indices} array.
     */
    public String getMissingPart(GamepadInput[] part) {
        if (!isPartOf(part)) return ""; //Not actually a part

        StringBuilder result = new StringBuilder();

        for (int i = part.length; i < indices.length; i++) {
            result.append(" + ")
                    .append(indices[i].name())
                    .append("[")
                    .append(indices[i].ordinal())
                    .append("]");
        }

        return result.toString().trim();
    }

    /**
     * @return Whether this Combination is a prefabricated one.
     */
    public boolean isValidPrefabricatedCombination() {
        return file == null && valid;
    }

    /**
     * @return Whether this Combination is invalid.
     */
    public boolean isInvalid() {
        if (requiresSetup) setup();
        return !valid;
    }

    /**
     * @return Whether this combination is empty
     */
    public boolean isEmptyCombination() {
        return valid && initScript.trim().length() == 0 && runScript.trim().length() == 0;
    }

    /**
     * Make it required to perform a setup again. Used to refresh the combination information after editing it.
     */
    public void requireSetup() {
        requiresSetup = true;
    }

    /**
     * @param input Should be equal to {@link #indices}.
     * @return Whether the input and the {@link #indices} are equal.
     */
    public boolean hasSameInput(GamepadInput[] input) {
        if (!valid) return false;
        if (input.length != indices.length) return false;

        for (int i = 0; i < input.length; i++) {
            if (input[i] != indices[i]) return false;
        }

        return true;
    }

    /**
     * @return if invalid or without name: "Unnamed combination". Else: The name which was set (e.g. in the init script).
     */
    public String getName() {
        if (!valid) return "Unnamed combination";
        if (requiresSetup) setup();
        if (name == null) name = "Unnamed Combination";
        return name;
    }

    /**
     * Sets the name of the Combination. Suppressing unused because this method will probably be called
     * by combinations.
     *
     * @param name The name of the Combination.
     */
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The file where the combination is stored. {@code null} if this is a prefabricated combination.
     */
    public File getFile() {
        return file;
    }

    /**
     * @return The run script which is executed when the combination is invoked.
     */
    public String getRunScript() {
        if (requiresSetup) setup();
        return runScript;
    }

    /**
     * @return The combination of {@link GamepadInput}'s which need to be pressed in order
     * to invoke the combination.
     */
    public GamepadInput[] getIndices() {
        return indices;
    }

    @Override
    public boolean equals(Object o) {
        if (requiresSetup) setup();
        if (this == o) return true;
        if (!(o instanceof Combination)) return false;

        Combination combo1 = (Combination) o;

        return hashCode() == combo1.hashCode();
    }

    /**
     * @return The hash code determined by the hash code of the indices.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(indices);
    }
}
