package com.tigerit.soa.loginsecurity.util;

public class RegistrationDef {
    public static final String EMAIL_VERIFICATION_SUBJECT ="EMAIL VERIFICATION FOR ACTION TRACKER";
    public static final String DOMAIN = "192.168.5.242";
    public static final String PORT = "7003";
    public static final String PATH = "/action-tracker/verifyUser";
    public static final String EMAIL_VERIFICATION_URL = "http://"+DOMAIN+":"+PORT+PATH;
    public static final int verificationTimeLimitInMinutes = 60 ;
}
