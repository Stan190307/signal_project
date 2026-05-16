package com.alerts.decorator;

import com.alerts.Alert;

public class PriorityAlertDecorator {

    private String priority;

    public PriorityAlertDecorator(Alert alert) {


    }

    public void triggerAlert() {
        System.out.println("PRIORITY: " + priority);

    }


}
