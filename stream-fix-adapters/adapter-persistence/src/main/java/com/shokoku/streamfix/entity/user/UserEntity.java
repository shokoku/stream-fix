package com.shokoku.streamfix.entity.user;

import com.shokoku.streamfix.aduit.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends MutableBaseEntity {

  @Id
  @Column(name = "USER_ID")
  private String userID;

  @Column(name = "USER_NAME")
  private String userName;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "PHONE")
  private String phone;

  public UserEntity(String userName, String password, String email, String phone) {
    this.userID = UUID.randomUUID().toString();
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
  }
}
