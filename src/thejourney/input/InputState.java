package thejourney.input;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InputState {

    private final Set<Integer> downKeys = ConcurrentHashMap.newKeySet();
    private final Set<Integer> justPressedKeys = ConcurrentHashMap.newKeySet();

    public void press(int keyCode) {
        if (downKeys.add(keyCode)) {
            justPressedKeys.add(keyCode);
        }
    }

    public void release(int keyCode) {
        downKeys.remove(keyCode);
    }

    public boolean isDown(int keyCode) {
        return downKeys.contains(keyCode);
    }

    public boolean wasPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    public void endFrame() {
        justPressedKeys.clear();
    }

    public void clear() {
        downKeys.clear();
        justPressedKeys.clear();
    }
}
