CREATE TABLE action_status
(
    id bigserial NOT NULL,
    status_name character varying(100) COLLATE pg_catalog."default",
    description character varying(100) COLLATE pg_catalog."default",
    status character varying(100) COLLATE pg_catalog."default",
    type character varying(100) COLLATE pg_catalog."default",
    toggled_on character varying(100) COLLATE pg_catalog."default",
    created_by character varying(50) COLLATE pg_catalog."default",
    creation_date timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_by character varying(50) COLLATE pg_catalog."default",
    update_date timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT action_status_pkey PRIMARY KEY (id)
)

