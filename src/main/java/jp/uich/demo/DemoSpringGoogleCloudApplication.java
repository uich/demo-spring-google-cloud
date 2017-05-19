package jp.uich.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoSpringGoogleCloudApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoSpringGoogleCloudApplication.class, args);
  }
}
