package com.shokoku.streamfix.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class MutableBaseEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "MODIFIED_AT")
  private LocalDateTime modifiedAt;

  @LastModifiedBy
  @Column(name = "MODIFIED_BY")
  private String modifiedBy;
}
