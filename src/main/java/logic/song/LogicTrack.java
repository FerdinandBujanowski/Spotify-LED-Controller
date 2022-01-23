package logic.song;

import control.exceptions.EventTimeNegativeException;

import java.util.ArrayList;

public class LogicTrack {

    private String trackName;
    private ArrayList<LogicEvent> eventArrayList;

    public LogicTrack(String trackName) {
        this.trackName = trackName;
        this.eventArrayList = new ArrayList<>();
    }

    public void addEventToTrack(int msStart, int msEnd) {
        int msDuration = msEnd - msStart;
        try {
            LogicEvent event = new LogicEvent(msStart, msEnd);
            LogicEvent overlappingEvent = this.getOverlappingEvent(event);
            while(overlappingEvent != null) {
                if(event.getMsStart() >= overlappingEvent.getMsStart()) {
                    event.updateEventTime(overlappingEvent.getMsEnd(), overlappingEvent.getMsEnd() + msDuration);
                } else {
                    event.updateEventTime(event.getMsStart(), overlappingEvent.getMsStart());
                }
                overlappingEvent = this.getOverlappingEvent(event);
            }
            this.eventArrayList.add(event);
        } catch (EventTimeNegativeException e) {
            e.printStackTrace();
        }
    }

    private LogicEvent getOverlappingEvent(LogicEvent newEvent) {
        for(LogicEvent event : this.eventArrayList) {
            if(this.eventOverlap(newEvent, event) || this.eventOverlap(event, newEvent)) {
                return event;
            }
        }
        return null;
    }

    public boolean eventOverlap(LogicEvent eventA, LogicEvent eventE) {
        return eventA.getMsStart() < eventE.getMsStart() && eventA.getMsEnd() > eventE.getMsStart();
    }
}
