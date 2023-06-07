package com.tigerit.soa.config;

import com.tigerit.soa.loginsecurity.component.exception.ActrServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;

/**
 *
 */
@Slf4j
public class RedisKey {
    public static final String UID_QUEUE = "nid2:{uid_queue}:id";
    public static final String PROCESSING_UID_QUEUE = "processing:{uid_queue}:";
    public static final String NID2_LOOKUP_CACHE_NAME = "nid2LookupRedisCache";
    public static final String NID2_DASHBOARD_CACHE_NAME = "nid2DashboardCache";
    public static final String NID2_SLA_FOR_USER_CACHE = "nid2:slaForUserCache";
    public static final String NID2_PARTNER_USER_INFO_CACHE = "nid2:partnerUserCache";
    public static final String SAID_USED_SET = "nid2:said_used_set:";
    public static final String NID2_AFIS_BILLING_REQUEST_CACHE = "nid2:afisRequest:";
    public static final String NID2_CARD_APP_ID_GEN_KEY = "nid2:cardapp:id";
    public static final String NID2_CARD_APP_FORM_NO_GEN_KEY = "nid2:cardapp:formno";
    public static final String VOTER_UPDATE_LOCK = "nid2:{voter_update_lock}";
    public static final String NID2_PORTAL_USER_ID_GEN_KEY = "nid2:portaluser:id";
    public static final String NID2_PIN_NUMBER_SEQUENCE_NEW = "nid2:pin_number:seq:new:";
    public static final String NID2_PIN_NUMBER_SEQUENCE_RELEASED = "nid2:pin_number:seq:released:";
    public static final String NID2_VOTER_NUMBER_SEQUENCE_NEW = "nid2:voter_area:seq:new:";
    public static final String NID2_VOTER_NUMBER_SEQUENCE_RELEASED = "nid2:voter_area:seq:released:";
    public static final String NID2_VOTER_SL_NO = "nid2:voter_area:{%s}:gender:{%s}:slno";

    public static String generateRedisCashKey(final String str) {
        try {
            log.debug("Voter Count result redis Key params {}", str);
            String key = DigestUtils.sha1Hex(str.getBytes());
            log.debug("Generated redis key to store report Result Or LookupChain: {}", key);
            return key;
        } catch (Exception ex) {
            log.error("Exception while Generating Redis key for report result Or Lookup Chain", ex);
            throw new ActrServiceException(HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
        }
    }

    public static String getVoterUpdateLockKey(String id) {
        return VOTER_UPDATE_LOCK + ":" + id;
    }
}
