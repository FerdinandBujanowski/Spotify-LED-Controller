package gui.main_panels.led_panel;

import control.led.*;

public class LedEditWindow implements LedGraphicUnit {

    private LedControl ledControl;

    public LedEditWindow(LedControl ledControl) {
        this.ledControl = ledControl;
    }

    public void addLayer() {
        this.ledControl.addLayer();
    }
}
