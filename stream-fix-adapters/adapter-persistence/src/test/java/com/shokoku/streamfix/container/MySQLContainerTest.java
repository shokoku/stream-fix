package com.shokoku.streamfix.container;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * MySQL Testcontainer 동작 확인을 위한 기본 테스트 클래스
 *
 * <p>Docker를 통해 MySQL 컨테이너가 정상적으로 실행되고, 해당 DB에 연결할 수 있는지 검증합니다.
 */
@Testcontainers
@DisplayName("MySQL Testcontainer 기본 동작 테스트")
class MySQLContainerTest {

  @Container
  static final MySQLContainer<?> mysql =
      new MySQLContainer<>("mysql:8.0.33")
          .withDatabaseName("test_streamfix")
          .withUsername("test")
          .withPassword("test");

  @Test
  @DisplayName("MySQL 컨테이너가 정상적으로 시작되어야 한다")
  void mysqlContainer_ShouldStart_Successfully() {
    // given & when & then
    assertThat(mysql.isRunning()).isTrue();
    assertThat(mysql.getDatabaseName()).isEqualTo("test_streamfix");
    assertThat(mysql.getUsername()).isEqualTo("test");
    assertThat(mysql.getPassword()).isEqualTo("test");
  }

  @Test
  @DisplayName("MySQL 컨테이너에 직접 연결할 수 있어야 한다")
  void shouldConnect_ToMySQLContainer_Directly() throws Exception {
    // given
    String jdbcUrl = mysql.getJdbcUrl();
    String username = mysql.getUsername();
    String password = mysql.getPassword();

    // when & then
    try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT 1 as test_value")) {

      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getInt("test_value")).isEqualTo(1);
    }
  }

  @Test
  @DisplayName("MySQL 버전이 올바르게 설정되어야 한다")
  void mysqlVersion_ShouldBe_CorrectVersion() throws Exception {
    // given
    String jdbcUrl = mysql.getJdbcUrl();
    String username = mysql.getUsername();
    String password = mysql.getPassword();

    // when & then
    try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT VERSION() as version")) {

      assertThat(resultSet.next()).isTrue();
      String version = resultSet.getString("version");
      assertThat(version).startsWith("8.0");
    }
  }
}
