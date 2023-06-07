package com.tigerit.soa.service.email;

import java.util.List;

/**
 * Created by DIPU on 6/4/20
 */
public interface EmailService {
    void sendSimpleMail(String to, String subject, String body);

    void sendSimpleMail(List<String> to, List<String> cc, String subject, String body);

}
