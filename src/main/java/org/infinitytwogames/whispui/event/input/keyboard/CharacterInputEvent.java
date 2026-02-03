package org.infinitytwogames.whispui.event.input.keyboard;

import org.infinitytwogames.whispui.event.Event;

public class CharacterInputEvent extends Event {
    public final int codepoint;
    public final String character;

    public CharacterInputEvent(int codepoint, char[] chars) {
        this.codepoint = codepoint;
        character = String.valueOf(chars);
    }
}
