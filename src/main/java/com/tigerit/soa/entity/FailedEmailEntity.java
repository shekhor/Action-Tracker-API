package com.tigerit.soa.entity;

import com.tigerit.soa.util.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by DIPU on 6/4/20
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "FAILED_EMAILS_ACTR")
@NoArgsConstructor
public class FailedEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = StringListConverter.class)
    @Column(name= "TO_MAILS")
    private List<String> toMails;

    @Convert(converter = StringListConverter.class)
    @Column(name="CC_MAILS")
    private List<String> ccMails;

    @Column(name="SUBJECT")
    private String subject;

    @Column(name="BODY")
    private String body;

    @Column(name="FAILED_CAUSE")
    private String failedCause;
}
