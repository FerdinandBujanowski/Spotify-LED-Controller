package logic.event;

import control.exceptions.EventTimeNegativeException;
import control.type_enums.CurveType;

import java.io.Serializable;

public class LogicEvent implements Serializable {

    private int msStart, msDuration;

    private CurveType curveType;
    private double userInput;

    public LogicEvent(int msStart, int msDuration, double userInput) throws EventTimeNegativeException {
        this.updateEventTime(msStart, msDuration);

        this.curveType = CurveType.CONSTANT;
        this.userInput = userInput;
    }

    public void updateEventTime(int newStart, int newDuration) throws EventTimeNegativeException {
        if(newStart < 0) {
            throw new EventTimeNegativeException("Event start time < 0");
        } else if (newDuration < 0) {
            throw new EventTimeNegativeException("Event end time < 0");
        } else {
            this.msStart = newStart;
            this.msDuration = newDuration;
        }
    }

    public int getMsStart() {
        return this.msStart;
    }
    public int getMsDuration() {
        return this.msDuration;
    }

    public CurveType getCurveType() {
        return this.curveType;
    }
    public void setCurveType(CurveType curveType) {
        this.curveType = curveType;
    }

    public double getUserInput() {
        return this.userInput;
    }
}
