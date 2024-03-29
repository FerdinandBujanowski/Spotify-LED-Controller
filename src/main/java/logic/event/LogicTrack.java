package logic.event;

import control.exceptions.EventTimeNegativeException;
import control.type_enums.CurveType;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class LogicTrack implements Serializable {

    private ArrayList<LogicEvent> eventArrayList;

    public LogicTrack() {
        this.eventArrayList = new ArrayList<>();
    }

    public void addEventToTrack(int msStart, int msDuration, CurveType curveType, int eventIndex, double userInput) {
        try {
            LogicEvent event = new LogicEvent(msStart, msDuration, userInput);
            event.setCurveType(curveType);
            if(eventIndex == -1) {
                this.eventArrayList.add(event);
            } else {
                this.eventArrayList.add(eventIndex, event);
            }
        } catch (EventTimeNegativeException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentEventCount() {
        return this.eventArrayList.size();
    }

    public int getEventIndex(int ms) {
        for(int i = 0; i < this.eventArrayList.size(); i++) {
            LogicEvent event = this.eventArrayList.get(i);
            if(ms >= event.getMsStart() && ms < (event.getMsStart() + event.getMsDuration())) {
                return i;
            }
        }
        return -1;
    }

    public Double getIntensityAt(Integer ms) {
        int eventIndex = this.getEventIndex(ms);
        if(eventIndex == -1) {
            return 0.d;
        }
        LogicEvent logicEvent = this.eventArrayList.get(eventIndex);
        if(logicEvent == null) {
            return 0.d;
        } else {
            int msInto = ms - logicEvent.getMsStart();
            double x = (double)msInto / (double)logicEvent.getMsDuration();
            if(logicEvent.getCurveType() == CurveType.USER_INPUT) {
                return logicEvent.getUserInput();
            } else {
                return logicEvent.getCurveType().getCurve(x);
            }
        }
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

    public Point getEventTime(int eventIndex) {
        LogicEvent logicEvent = this.eventArrayList.get(eventIndex);
        return new Point(logicEvent.getMsStart(), logicEvent.getMsDuration());
    }

    public CurveType getCurveTypeAt(int eventIndex) {
        return this.eventArrayList.get(eventIndex).getCurveType();
    }

    public double getUserInputAt(int eventIndex) {
        return this.eventArrayList.get(eventIndex).getUserInput();
    }

}
