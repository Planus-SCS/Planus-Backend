package scs.planus.global.util.logTracker.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MetaData {
    private String requestURI;
    private String httpMethod;
    private String email;
    private String className;
    private String methodName;
    private Integer lineNumber;
    @Column(length = 50000)
    private String parameter;
}
