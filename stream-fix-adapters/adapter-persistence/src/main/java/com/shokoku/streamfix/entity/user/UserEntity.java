package com.shokoku.streamfix.entity.user;

import com.shokoku.streamfix.audit.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends MutableBaseEntity {

  @Id
  @Column(name = "USER_ID")
  private String userId;

  @Column(name = "USER_NAME")
  private String userName;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "PHONE")
  private String phone;

  public UserEntity(String userName, String password, String email, String phone) {
    this.userId = UUID.randomUUID().toString();
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
  }
}
