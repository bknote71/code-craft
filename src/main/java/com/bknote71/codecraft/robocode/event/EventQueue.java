package com.bknote71.codecraft.robocode.event;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("serial")
public class EventQueue extends ArrayList<Event> {

    public void clear(boolean includingSystemEvents) {
        if (includingSystemEvents) {
            super.clear();
            return;
        }

        for (int i = 0; i < size(); i++) {
            Event e = get(i);
            remove(i--);
        }
    }

    public void clear(long clearTime) {
        for (int i = 0; i < size(); i++) {
            Event e = get(i);

            if ((e.getTime() <= clearTime)) {
                remove(i--);
            }
        }
    }

    void sort() {
        Collections.sort(this);
    }
}
