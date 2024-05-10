package com.bknote71.codecraft.robocode.event;


import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.proxy.BasicRobotProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final static int MAX_PRIORITY = 100;
    public final static int MAX_EVENT_STACK = 2;
    public final static int MAX_QUEUE_SIZE = 256;

    private EventQueue eventQueue;

    private final boolean[] interruptible = new boolean[MAX_PRIORITY + 1];
    private Event currentTopEvent;
    private int currentTopEventPriority;
    private ScannedRobotEvent dummyScannedRobotEvent;
    private Map<String, Event> eventNames;

    private Robot robot;
    private BasicRobotProxy robotProxy;

    public EventManager(BasicRobotProxy robotProxy) {
        this.robotProxy = robotProxy;
        this.eventQueue = new EventQueue();

        registerEventNames();
        reset();
    }

    private void registerEventNames() {
        eventNames = new HashMap<>();
        dummyScannedRobotEvent = new ScannedRobotEvent(null, 0, 0, 0, 0, 0);
        register(new HitByBulletEvent(0, null));
        register(new HitRobotEvent(null, 0, 0, false));
        register(new StatusEvent(null));
        register(dummyScannedRobotEvent);
    }

    private void register(Event event) {
        event.setDefaultPriority(event);
        Class<? extends Event> type = event.getClass();
        eventNames.put(type.getName(), event);
        eventNames.put(type.getSimpleName(), event);
    }

    public void reset() {
        currentTopEventPriority = Integer.MIN_VALUE;
        eventQueue.clear(true);
    }

    public void cleanup() {
        reset();
        robot = null;
        robotProxy = null;
    }

    public void add(Event event) {
        // event priority ??
        int priority = getEventPriority(event.getClass().getName());
        event.setPriority(priority);

        if (eventQueue != null) {
            // 너무 많으면 안됨
            if (eventQueue.size() > MAX_QUEUE_SIZE) {
                ;
            } else {
                event.setTime(getTime());
                eventQueue.add(event);
            }
        }
    }

    private int getEventPriority(String name) {
        if (name == null) {
            return -1;
        }
        final Event event = eventNames.get(name);
        if (event == null) {
            return -1;
        }

        return event.getPriority();
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<Event>();
        synchronized (eventQueue) {
            for (Event e : eventQueue) {
                events.add(e);
            }
        }
        return events;
    }

    public List<BulletHitEvent> getBulletHitEvents() {
        List<BulletHitEvent> events = new ArrayList<BulletHitEvent>();
        synchronized (eventQueue) {
            for (Event e : eventQueue) {
                if (e instanceof BulletHitEvent) {
                    events.add((BulletHitEvent) e);
                }
            }
        }
        return events;
    }

    public List<HitByBulletEvent> getHitByBulletEvents() {
        List<HitByBulletEvent> events = new ArrayList<HitByBulletEvent>();
        synchronized (eventQueue) {
            for (Event e : eventQueue) {
                if (e instanceof HitByBulletEvent) {
                    events.add((HitByBulletEvent) e);
                }
            }
        }
        return events;
    }

    public List<ScannedRobotEvent> getScannedRobotEvents() {
        List<ScannedRobotEvent> events = new ArrayList<ScannedRobotEvent>();
        synchronized (eventQueue) {
            for (Event e : eventQueue) {
                if (e instanceof ScannedRobotEvent) {
                    events.add((ScannedRobotEvent) e);
                }
            }
        }
        return events;
    }

    public Event getCurrentTopEvent() {
        return currentTopEvent;
    }

    public double getCurrentTopEventPriority() {
        return currentTopEventPriority;
    }

    public boolean isInterruptible(int priority) {
        return interruptible[priority];
    }

    public void setInterruptible(int priority, boolean isInterruptable) {
        if (priority >= 0 && priority < MAX_PRIORITY) {
            interruptible[priority] = isInterruptable;
        }
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public long getTime() {
        return robotProxy.getTime();
    }

    public int getScannedRobotEventPriority() {
        return dummyScannedRobotEvent.getPriority();
    }

    public void processEvents() {
        // remove old events
        eventQueue.clear(getTime() - MAX_EVENT_STACK);

        // 이벤트 정렬?
        // eventQueue.sort();

        Event currentEvent;
        while ((currentEvent = (eventQueue.size() > 0) ? eventQueue.get(0) : null) != null
                && currentEvent.getPriority() >= currentTopEventPriority) { // 우선순위가 높은 이베느가 실행될 수 있도록 해준다.
            if (currentEvent.getPriority() == currentTopEventPriority) { // old 이벤트(현재 실행하는 이벤트, currentTopEvent) 처내기
                if (currentTopEventPriority > Integer.MIN_VALUE && isInterruptible(currentTopEventPriority)) {
                    setInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

                    // We are already in an event handler, took action, and a new event was generated.
                    // So we want to break out of the old handler to process it here.
                    System.out.println("event interrupt, 기존에 처리중인 이벤트가 있는 와중에 비동기적으로 processEvents 가 호출되었기 때문에 발생할 수 있었던 것");
                    System.out.println("우리 코드에서는 비동기적으로 호출될 일이 없지 않나?");
                    System.out.println("현재 이벤트 우선순위: " + currentEvent.getPriority());
                    System.out.println("현재 탑 이벤트의 우선순위: " + currentTopEventPriority);
                    throw new EventInterruptedException(currentEvent.getPriority());
                }
                break;
            }

            int oldTopEventPriority = currentTopEventPriority;
            currentTopEventPriority = currentEvent.getPriority();
            currentTopEvent = currentEvent;
            eventQueue.remove(currentEvent);

            try {
                dispatch(currentEvent);

                setInterruptible(currentTopEventPriority, false);
            } catch (EventInterruptedException e) {
                currentTopEvent = null;
            } catch (RuntimeException e) {
                currentTopEvent = null;
                throw e;
            } catch (Error e) {
                currentTopEvent = null;
                throw e;
            } finally {
                currentTopEventPriority = oldTopEventPriority;
            }
        }
    }

    private void dispatch(Event event) {
        if (robot != null && event != null) {
            try {
                if ((event.getTime() > getTime() - MAX_EVENT_STACK)) {
                    event.dispatch(robot, robotProxy.getStatics());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
