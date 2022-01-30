package logic.song;

import control.exceptions.EventTimeNegativeException;
import control.type_enums.CurveType;

import java.util.ArrayList;

public class LogicTrack {

    private ArrayList<LogicEvent> eventArrayList;

    public LogicTrack() {
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

    public int getEventIndex(int ms) {
        for(int i = 0; i < this.eventArrayList.size(); i++) {
            LogicEvent event = this.eventArrayList.get(i);
            if(ms >= event.getMsStart() && ms < event.getMsEnd()) {
                return i;
            }
        }
        return -1;
    }

    public void removeEventAtIndex(int eventIndex) {
        this.eventArrayList.remove(eventIndex);
    }

    public LogicEvent[] getEventsCopyArray() {
        LogicEvent[] eventCopy = new LogicEvent[this.eventArrayList.size()];
        for(int i = 0; i < eventCopy.length; i++) {
            eventCopy[i] = this.eventArrayList.get(i);
        }
        return eventCopy;
    }

    public CurveType[] getCurveTypes() {
        CurveType[] curveTypeCopy = new CurveType[this.eventArrayList.size()];
        for(int i = 0; i < curveTypeCopy.length; i++) {
            curveTypeCopy[i] = this.eventArrayList.get(i).getCurveType();
        }
        return curveTypeCopy;
    }
}
