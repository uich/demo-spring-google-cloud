package jp.uich.demo.google.cloud.api;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("google.cloud")
public class GoogleCloudProperties {

  private List<String> scopes;
  private Resource jsonKey;
  private String apiKey;

}
