package com.alerts.decorator;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator{
    private long interval;

    public RepeatedAlertDecorator(Alert alert,  long interval) {
        super(alert);
        this.interval  = interval;
    }

    public void TriggerAlert(){

    }
}
