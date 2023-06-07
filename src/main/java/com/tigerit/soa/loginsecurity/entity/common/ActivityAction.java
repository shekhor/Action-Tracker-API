package com.tigerit.soa.loginsecurity.entity.common;

public enum ActivityAction {
    INSERT(0),
    UPDATE(1),
    DELETE(2),
    BATCH_ACTIVITY_ACTION(3),
    ACTIVATE(4);

    private final int action;

    private ActivityAction(int var3) {
        this.action = var3;
    }

    public int getAction() {
        return this.action;
    }
}