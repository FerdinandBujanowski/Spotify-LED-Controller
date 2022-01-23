package logic.event;

import control.exceptions.EventTimeNegativeException;
import control.type_enums.CurveType;

public class LogicEvent {

    private int msStart, msDuration, msEnd;

    private CurveType curveType;

    public LogicEvent(int msStart, int msEnd) throws EventTimeNegativeException {
        this.updateEventTime(msStart, msEnd);

        this.curveType = CurveType.CONSTANT;
    }

    public void updateEventTime(int newStart, int newEnd) throws EventTimeNegativeException {
        if(newStart < 0) {
            throw new EventTimeNegativeException("Event start time < 0");
        } else if (newEnd < 0) {
            throw new EventTimeNegativeException("Event end time < 0");
        } else if(newEnd <= newStart) {
            throw new EventTimeNegativeException("Event duration negative");
        } else {
            this.msStart = newStart;
            this.msEnd = newEnd;
            this.msDuration = msEnd - msStart;
        }
    }

    public int getMsStart() {
        return this.msStart;
    }
    public int getMsDuration() {
        return this.msDuration;
    }
    public int getMsEnd() {
        return this.msEnd;
    }

    public CurveType getCurveType() {
        return this.curveType;
    }
    public void setCurveType(CurveType curveType) {
        this.curveType = curveType;
    }


}
