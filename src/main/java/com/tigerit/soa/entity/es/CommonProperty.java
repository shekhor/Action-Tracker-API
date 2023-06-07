package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tigerit.soa.util.CustomDateTimeDeserializer;
import com.tigerit.soa.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
Fahim created at 5/10/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonProperty {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private Date createTime;

    private Long createTimeInMs;

    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private Date editTime;

    private Long editTimeInMs;

    private String editedBy;

    private Long versionId;

    public void setTimeAndUser(String userName) {
        if (this.createTime == null) {
            this.createTime = new Date();
            this.createdBy = userName;
            this.createTimeInMs = createTime.getTime();
            this.versionId = 1l;
        }

        this.editTime = new Date();
        this.editedBy = userName;
        this.editTimeInMs = editTime.getTime();
    }
}
