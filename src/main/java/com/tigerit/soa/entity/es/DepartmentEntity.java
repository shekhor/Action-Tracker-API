package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by DIPU on 6/01/20
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "department", type = "department")
@EqualsAndHashCode
public class DepartmentEntity extends CommonProperty implements Serializable {
    @Id
    private String id;

    private String organizationId;

    private String organizationName;

    private String departmentName;

    private String description;

    private Long departmentOwner;

    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentEntity that = (DepartmentEntity) o;
        return id == that.id &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(organizationName, that.organizationName) &&
                Objects.equals(departmentName, that.departmentName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(departmentOwner, that.departmentOwner) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organizationId,organizationName, departmentName, description, departmentOwner, status);
    }
}
