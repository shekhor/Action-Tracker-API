package com.tigerit.soa.util;

/**
 * Created by DIPU on 4/9/20
 */
public class Defs {

    public static final int DEPARTMENT_ID_MAX_LENGTH=64;
    public static final int DEPARTMENT_NAME_MAX_LENGTH=100;
    public static final int DEPARTMNET_DESCRIPTION_MAX_LENGTH=512;
    public static final int STATUS_LENGTH=100;
    public static final String STATUS_ACTIVE="ACTIVE";
    public static final String STATUS_INACTIVE="INACTIVE";
    public static final String STATUS_DELETED="DELETED";
    public static final String STATUS_ARCHIVED="ARCHIVED";

    public static final String PROJECT_OWNER = "Project Owner";
    public static final String PROJECT_MANAGER = "Project Manager";
    public static final long VERSION_ID=1L;
    public static final int PROJECT_ID_MAX_LENGTH=255;
    public static final int STR_MIN_LEN=1;
    public static final int STR_MAX_LEN=255;

    //TODO: dummy min max limit for String values:
    public static final int ES_PK_MAX_LEN =64;
    public static final long PG_PK_SIZE=Long.MAX_VALUE;

    public static final String PROJECT_INDEX="project";
    public static final String DEPARTMENT_INDEX="department";

}
