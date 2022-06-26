package gui.main_panels.event_panel;

import control.event.EventGraphicUnit;
import control.event.TimeMeasure;
import control.event.EventRequestAcceptor;
import control.type_enums.CurveType;

public class GraphicTimeMeasure extends GraphicEvent {

    private EventRequestAcceptor eventRequestAcceptor;
    private EventGraphicUnit eventGraphicUnit;

    public GraphicTimeMeasure(TimeMeasure timeMeasure, EventRequestAcceptor eventRequestAcceptor, EventGraphicUnit eventGraphicUnit) {
        super(
                0,
                CurveType.CONSTANT,
                timeMeasure.getMsStart(),
                timeMeasure.getLengthOneBar() * timeMeasure.getBarsDuration(),
                eventGraphicUnit,
                eventRequestAcceptor
        );

        this.eventRequestAcceptor = eventRequestAcceptor;
        this.eventGraphicUnit = eventGraphicUnit;
    }

}
