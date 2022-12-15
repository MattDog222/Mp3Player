package app;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class PlayButtonHandler {
    private State state = State.NOT_PLAYING;

    public State toggle() {
        State newState = switch (state) {
            case PLAYING -> State.PAUSED;
            case PAUSED, NOT_PLAYING -> State.PLAYING;
        };

        state = newState;
        return newState;
    }

    /**
     * Updates the state
     * @param state
     * @return whether the set was successful
     */
    public boolean setState(State state) {
        if (this.state == state)
            return false;
        this.state = state;
        return true;
    }

    public enum State {
        PLAYING, PAUSED, NOT_PLAYING
    }
}
