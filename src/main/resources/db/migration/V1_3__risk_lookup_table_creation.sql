CREATE TABLE risk_lookup
(
    id bigserial NOT NULL,
    risk_name character varying(256) NOT NULL,
    data_type character varying(100) NOT NULL,
    default_value character varying(1024),
    description character varying(256),
    status character varying(100) DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);
