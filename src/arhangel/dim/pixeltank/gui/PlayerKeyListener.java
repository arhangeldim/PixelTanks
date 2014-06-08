package arhangel.dim.pixeltank.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class PlayerKeyListener implements KeyListener {
    Logger logger = LoggerFactory.getLogger(PlayerKeyListener.class);

    private Set<Integer> events = new HashSet<>();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        events.add(e.getKeyCode());
        logger.info("Pressed: {}", events);

    }

    @Override
    public void keyReleased(KeyEvent e) {
        events.remove(e.getKeyCode());
    }
}
