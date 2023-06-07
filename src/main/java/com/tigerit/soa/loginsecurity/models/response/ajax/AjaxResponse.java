package com.tigerit.soa.loginsecurity.models.response.ajax;

/**
 *
 */
public class AjaxResponse {
    private StatusCode status;

    private ErrorDetails error;

    private PendingResult pending;

    private SuccessDetails success;

    private WarningDetails warning;

    public AjaxResponse(ErrorDetails error) {
        this.status = StatusCode.ERROR;
        this.error = error;
    }

    public AjaxResponse(PendingResult pending) {
        this.status = StatusCode.PENDING;
        this.pending = pending;
    }

    public AjaxResponse(SuccessDetails success) {
        this.status = StatusCode.SUCCESS;
        this.success = success;
    }

    public AjaxResponse(WarningDetails warning) {
        this.status = StatusCode.WARNING;
        this.warning = warning;
    }

    public StatusCode getStatus() {
        return status;
    }

    public ErrorDetails getError() {
        return error;
    }

    public SuccessDetails getSuccess() {
        return success;
    }

    public PendingResult getPending() {
        return pending;
    }

    public WarningDetails getWarning() {
        return warning;
    }
}
