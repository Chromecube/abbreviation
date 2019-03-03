package me.niklas.abbreviation.display;

import me.niklas.abbreviation.enums.GamepadInput;
import me.niklas.abbreviation.util.Combination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Niklas on 14.10.2018 in abbreviation
 */
public class PreviewManager {

    private static int INSTANCE = 0;

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Shows a preview in the lower right corner of the screen.
     *
     * @param possible      The possible Combination which could be displayed by the current input. null if there is no match.
     * @param current       The current input list.
     * @param possibilities All sub-combinations possible (limited to 5 possibilities).
     */
    public void showPreview(Combination possible, GamepadInput[] current, List<Combination> possibilities) {
        if (executor.isShutdown()) return;
        INSTANCE = Arrays.hashCode(current); //Set instance

        executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Inputs + (if present) the name of the possible result.
            String tile = possible != null ? GamepadInput.toReadable(current) + ": " + possible.getName() : GamepadInput.toReadable(current);

            //Set up dialog
            JDialog dialog = new JDialog();
            dialog.setAlwaysOnTop(true);
            dialog.setUndecorated(true);

            //Set up panel (vertical layout) and title label
            final JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            final JLabel label = new JLabel(tile);
            panel.add(label);

            //Add all possibilities
            for (Combination p : possibilities) {
                panel.add(new JLabel(p.getMissingPart(current) + ": " + p.getName()));
            }

            //Set background color to white + pack
            dialog.setBackground(new Color(255, 255, 255, 255));
            dialog.add(panel, BorderLayout.CENTER);
            dialog.pack();

            //Show in lower right corner
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            dialog.setLocation(screen.width - dialog.getWidth(), screen.height - dialog.getHeight());

            //Set minimum size: 200x200
            if (dialog.getWidth() < 200) {
                dialog.setSize(200, dialog.getHeight());
            }
            if (dialog.getHeight() < 200) {
                dialog.setSize(dialog.getWidth(), 200);
            }

            //Show dialog
            dialog.setVisible(true);

            //Close when other window opened or shown for 10 seconds
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.debug("Sleep interrupted.");
                }
                if (INSTANCE != Arrays.hashCode(current)) { //New Preview
                    dialog.dispose();
                    return;
                }
            }
        });
    }

    /**
     * Closes the current preview.
     */
    public void closePreview() {
        INSTANCE = 0;
    }

    /**
     * Terminates the preview. This action is NOT REVERSIBLE.
     */
    public void shutdown() {
        closePreview();
        executor.shutdown();
    }
}
