package com.shokoku.streamfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StreamFixBatchApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext run =
        SpringApplication.run(StreamFixBatchApplication.class, args);
    SpringApplication.exit(run);
  }
}
