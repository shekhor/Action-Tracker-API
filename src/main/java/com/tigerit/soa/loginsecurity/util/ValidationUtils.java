package com.tigerit.soa.loginsecurity.util;

/**
 *
 */
public class ValidationUtils {

    public final static String ALPHANUMERIC_UNDERSCORE_DOT = "^[a-zA-Z0-9](?:[a-zA-Z0-9_.]*[a-zA-Z0-9])?$";
    public final static String ALPHABET_SPACE_HYPHEN_DOT = "^[a-zA-Z](?:[a-zA-Z\\s-.]*[a-zA-Z])?$";
    public final static String ALPHANUMERIC_SPACE_HYPHEN_DOT = "^[a-zA-Z0-9](?:[a-zA-Z0-9\\s-.]*[a-zA-Z0-9])?$";
    public final static String ALPHANUMERIC_SPACE_HYPHEN_UNDERSCORE_DOT = "^[a-zA-Z0-9](?:[a-zA-Z0-9\\s-_.]*[a-zA-Z0-9])?$";
    public final static String ALPHANUMERIC_AND_SPECIAL_CHAR = "^[a-zA-Z0-9](?:[a-zA-Z0-9\\s-._#,]*[a-zA-Z0-9])?$";
    public final static String DOMAIN_NAME_PATTERN = "^[a-z.](?:[a-z\\s.]*[a-z])?$";
    public final static String NUMERIC="^[0-9]*$";

    public final static String NATIONAL_ID_OR_PIN = "[0-9]{10}$|[0-9]{17}$";

    public final static String NUMERIC_TEN_DIGIT = "[0-9]{10}$";
    public final static String NUMERIC_TWELVE_DIGIT = "[0-9]{12}$";


    public final static int MAX_USERNAME_SIZE = 50;
    public final static int MAX_EMAIL_SIZE = 100;
    public final static int MAX_PASSWORD_SIZE = 50;
    public final static int MIN_PASSWORD_SIZE = 6;
    public final static int MAX_FULL_NAME_SIZE = 200;
    public final static int MAX_WEBSITE_SIZE = 100;
    public final static int MAX_DESIGNATION_SIZE = 100;
    public final static int MAX_PHONE_SIZE = 15;
    public final static int MAX_MOBILE_SIZE = 15;
    public static final int MAX_NUMBER_SIZE = 6;

    //    Partner
    public static final int MAX_PARTNER_NAME_SIZE = 100;
    public static final int MAX_TEXT_SIZE = 250;
    public final static int MAX_EMAIL_DOMAIN_SIZE = 12;


    public static final int MAX_NAME_SIZE = 60;
}
