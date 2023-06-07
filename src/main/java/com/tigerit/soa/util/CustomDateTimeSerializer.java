package com.tigerit.soa.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Fahim created at 4/29/2020
*/
public class CustomDateTimeSerializer extends StdSerializer<Date>  {

    public CustomDateTimeSerializer() {
        this(null);
    }

    private CustomDateTimeSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        gen.writeString(formatter.format(value));
    }
}
