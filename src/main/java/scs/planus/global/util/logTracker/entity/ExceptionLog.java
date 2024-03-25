package scs.planus.global.util.logTracker.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ExceptionLog extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private MetaData metaData;
    @Embedded
    private ExceptionData exceptionData;

    @Builder
    public ExceptionLog(MetaData metaData, ExceptionData exceptionData) {
        this.metaData = metaData;
        this.exceptionData = exceptionData;
    }
}
