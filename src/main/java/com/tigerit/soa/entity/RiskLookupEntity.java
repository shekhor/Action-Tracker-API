package com.tigerit.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

/*
Fahim created at 5/10/2020
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "risk_lookup")
public class RiskLookupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "risk_name")
    private String riskName;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;
}
