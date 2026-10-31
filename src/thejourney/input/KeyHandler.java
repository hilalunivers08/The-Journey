package thejourney.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private final InputState input;

    public KeyHandler(InputState input) {
        this.input = input;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used: gameplay actions are handled on key press and release.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        input.press(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        input.release(e.getKeyCode());
    }
}
