package me.niklas.abbreviation.tray;

import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import me.niklas.abbreviation.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by Niklas on 25.02.2019 in abbreviation
 */
public class TrayManager implements EventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final EventManager events;
    private TrayIcon icon;
    private Image image;
    private boolean activated = false;

    /**
     * @param events The {@link EventManager} used to fire events when clicking on menu items.
     */
    public TrayManager(EventManager events) {
        this.events = events;
        if (!SystemTray.isSupported()) {
            logger.error("System tray is not supported on that system.");
            return;
        }
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("logo.ico")));
        } catch (IOException e) {
            logger.error("Can not read icon from resources:", e);
            return;
        }

        icon = new TrayIcon(image);

        activated = true;
        register();
    }

    /**
     * @param image  An {@link Image}, size 16x16 pixels.
     * @param events The {@link EventManager} used to fire events when clicking on menu items.
     */
    public TrayManager(Image image, EventManager events) {
        this.events = events;
        if (image == null) {
            activated = false;
            throw new IllegalArgumentException("The image parameter is null!");
        }
        if (!SystemTray.isSupported()) {
            logger.error("System tray is not supported on that system.");
            activated = false;
            return;
        }
        this.image = image;
        icon = new TrayIcon(this.image);
        activated = true;
        register();

    }

    /**
     * Sets up and registers the {@link TrayIcon}.
     */
    private void register() {
        if (!SystemTray.isSupported()) { //Fallback. Should already be checked in constructor.
            logger.error("System tray is not supported on that system.");
            activated = false;
            return;
        }

        try {
            PopupMenu menu = new PopupMenu();
            MenuItem reload = new MenuItem("Reload");
            MenuItem exit = new MenuItem("Exit");

            reload.addActionListener(event -> events.fire(EventType.RELOAD, null));
            exit.addActionListener(event -> events.performExit());

            menu.add(reload);
            menu.add(exit);

            icon.setPopupMenu(menu);
            icon.setToolTip("Abbreviation Version Nr. " + VersionInfo.VERSION);

            SystemTray.getSystemTray().add(icon);
        } catch (Exception e) {
            activated = false;
            logger.error("Failed to register the tray icon:", e);
        }
    }

    /**
     * @return Whether the tray icon is activated. If the tray icon is not activated, it is either not supported
     * or the resources were invalid.
     */
    private boolean isActivated() {
        return activated;
    }

    /**
     * Displays a message.
     *
     * @param caption The title of the message.
     * @param text    The text of the message.
     * @param type    The {@link java.awt.TrayIcon.MessageType} of the message.
     */
    private void displayMessage(String caption, String text, TrayIcon.MessageType type) {
        if (!isActivated()) return;

        icon.displayMessage(caption, text, type);
    }

    /**
     * Shortened version if {@link #displayMessage(String, String, TrayIcon.MessageType)}.
     *
     * @param text The text of the message.
     */
    private void displayMessage(String text) {
        displayMessage("Abbreviation", text, TrayIcon.MessageType.NONE);
    }

    /**
     * Handles:
     * - SHOW_MESSAGE (Parsed the {@link String} message and shows a message using {@link #displayMessage(String)})
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventType type, Object param) {
        if (type == EventType.SHOW_MESSAGE) {
            if (param instanceof String) {
                displayMessage((String) param);
            } else {
                logger.warn("Invalid parameter in SHOW_MESSAGE.");
            }
        }
    }

    /**
     * Removes the tray icon from the tray bar.
     */
    @Override
    public void onExit() {
        logger.debug("Confirm shutdown");
        if (activated) SystemTray.getSystemTray().remove(icon);
        icon = null;
    }
}
