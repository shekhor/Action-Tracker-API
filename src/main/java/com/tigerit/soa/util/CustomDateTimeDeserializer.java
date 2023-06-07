package com.tigerit.soa.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Fahim created at 4/29/2020
*/
@Log4j2
public class CustomDateTimeDeserializer extends StdDeserializer<Date> {

    public CustomDateTimeDeserializer() {
        this(null);
    }

    private CustomDateTimeDeserializer(Class<Date> t) {
        super(t);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String date = jsonParser.getText();
        try {
            return formatter.parse(date);
        } catch (Exception ex) {
            log.debug("Error while parsing date: {} ", date, ex);
            throw new RuntimeException("Cannot Parse Date");
        }
    }
}
