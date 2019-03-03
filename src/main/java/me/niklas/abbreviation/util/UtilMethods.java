package me.niklas.abbreviation.util;


import bsh.EvalError;
import bsh.Interpreter;
import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Niklas on 13.10.2018 in abbreviation
 * All methods could be used by scripts.
 * Because this is not recognized by any IDE or testing program, some inspections might be disabled here.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UtilMethods {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final EventManager events;
    private ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Without an {@link EventManager}, util methods are not able to call events.
     */
    public UtilMethods() {
        events = null;
        logger.warn("Running UtilMethods without an event manager.");
    }

    public UtilMethods(EventManager events) {
        this.events = events;
    }

    /**
     * Opens an {@link URI} in the default application. As an example, this can be an image, a document or a website (URL).
     *
     * @param uri The {@link URI} as a {@link String}.
     */
    public void open(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Exception e) {
            logger.error("Error while trying to open uri: ", e);
        }
    }

    /**
     * Copies a {@link String} to the {@link Clipboard}.
     *
     * @param text The {@link String} to be copied.
     */
    public void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    /**
     * Executes a command in the cmd/shell.
     *
     * @param command The command as a {@link String}.
     */
    public void execute(String command) {

        try {
            if (OperatingSystem.WINDOWS) {
                Runtime.getRuntime().exec("cmd /c " + command);
            } else {
                Runtime.getRuntime().exec("/bin/sh -c " + command);
            }
        } catch (IOException e) {
            logger.error("Could not execute command: ", e);
        }
    }

    /**
     * Executes a piece of Java code. Unlike the {@link me.niklas.abbreviation.input.CombinationManager},
     * no environment variables are set.
     *
     * @param code The code to be executed. Has to be valid Java code, else an exception will be thrown.
     * @return The returned {@link Object}, or else {@code null}.
     */
    public Object executeJava(String code) {
        Interpreter interpreter = new Interpreter();

        try {
            return interpreter.eval(code);
        } catch (EvalError evalError) {
            logger.error("Could not evaluate code: ", evalError);
        }
        return null;
    }

    /**
     * Starts a process. Notice that you either have to specify the absolute path or have to add it to the PATH variable.
     *
     * @param programName The absolute path or the name of the executable added to PATH.
     */
    public void start(String programName) {
        try {
            if (OperatingSystem.WINDOWS) {
                if (programName.trim().endsWith("exe"))
                    Runtime.getRuntime().exec("cmd /c " + programName);
                else
                    Runtime.getRuntime().exec("cmd /c start " + programName);
            } else {
                Runtime.getRuntime().exec("/bin/sh -c " + programName);
            }
        } catch (IOException e) {
            logger.error("Error while running process: ", e);
        }
    }

    /**
     * Joins two {@link String} arrays together.
     *
     * @param first  The first array.
     * @param second The second array.
     * @return The combined array. Length = first.length+second.length.
     */
    private String[] joinArrays(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Displays a question and returns the users input.
     *
     * @param question The question as a {@link String}.
     * @return The answer of the user as a {@link String}.
     */
    public String getInput(String question) {
        return JOptionPane.showInputDialog(question);
    }

    /**
     * Closes the application. Only possible if the {@link EventManager} is not null.
     */
    public void exit() {
        if (events == null) return;

        executor.shutdown();
        events.performExit();
    }

    /**
     * Windows only. Checks whether a process with a specific name is running.
     *
     * @param processName The name of the process (e.g. "firefox")
     * @return Whether the process is running.
     */
    public boolean isProcessRunning(String processName) {
        if (!OperatingSystem.WINDOWS) {
            logger.warn("Non-Windows systems do not support isProcessRunning yet.");
            return false;
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cmd /c tasklist /FO CSV");
        } catch (IOException e) {
            logger.error("Could not execute tasklist: ", e);
        }
        if (process == null) return false;
        final Process threadProcess = process;
        StringBuffer buffer = new StringBuffer();
        executor.submit(() -> {

            BufferedReader input = new BufferedReader(new InputStreamReader(threadProcess.getInputStream()));
            String line;

            try {
                while ((line = input.readLine()) != null)
                    buffer.append(line.split(",")[0].replace('"', ' ').trim()).append("\n");
            } catch (IOException e) {
                logger.error("Could not read from tasklist command input stream: ", e);
            }

        });
        try {
            process.waitFor();
        } catch (Exception e) {
            logger.error("Interrupted: ", e);
        }
        for (String name : buffer.toString().split("\n")) {
            if (name.equalsIgnoreCase(processName)) return true;
        }
        return false;
    }

    /**
     * Displays a message. Size and display time are based on the length of the {@link String}.
     * The message is shown in the center of the screen.
     *
     * @param message The message as a {@link String}.
     */
    public void showMessage(String message) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setUndecorated(true);

        final JPanel panel = new JPanel();
        final JLabel label = new JLabel(message);
        label.setSize(screenSize.width / 3, screenSize.height / 2);

        Font font = label.getFont();
        int textWidth = label.getFontMetrics(font).stringWidth(label.getText());
        int labelWidth = label.getWidth();

        double widthRatio = (double) labelWidth / (double) textWidth;

        int fontSize = (int) (font.getSize() * widthRatio);
        int labelHeight = label.getHeight();

        int fontSizeToUse = fontSize < labelHeight ? fontSize : labelHeight;
        label.setFont(new Font(font.getName(), Font.PLAIN, fontSizeToUse));
        panel.add(label);

        dialog.setBackground(new Color(255, 255, 255, 255));
        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();


        dialog.setLocation((screenSize.width / 2) - (dialog.getWidth() / 2), (screenSize.height / 2) - (dialog.getHeight() / 2));

        dialog.setVisible(true);

        new Thread(() -> {
            //Less than 10 characters: 1 seconds, More than 50 characters: 5 seconds, else: characters/10.
            int length = label.getText().length() * 100;
            length = length < 1000 ? 1000 : length > 5000 ? 5000 : length;

            try {
                Thread.sleep(length);
            } catch (InterruptedException e) {
                logger.error("Interrupted: ", e);
            }
            dialog.dispose();
        }).start();
    }

    /**
     * Fires an event. Only possible if the {@link EventManager} is not null.
     *
     * @param type  The {@link EventType} of the event. Specifies what should be done/has been done.
     * @param param The parameter. Can be of any type, casting happens at the {@link EventSubscriber}'s side.
     */
    public void fire(EventType type, Object param) {
        if (events == null) return;

        events.fire(type, param);
    }
}
