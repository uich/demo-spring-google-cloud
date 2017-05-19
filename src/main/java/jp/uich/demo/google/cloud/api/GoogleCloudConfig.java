package jp.uich.demo.google.cloud.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.google.auth.oauth2.GoogleCredentials;

import jp.uich.demo.rest.LoggingRequestResponseInterceptor;
import lombok.SneakyThrows;

@Configuration
public class GoogleCloudConfig {

  @Autowired
  GoogleCloudProperties properties;

  @Bean(name = "googleCloud.restTemplate")
  RestTemplate googleCloudApiRestTemplate() {
    return new RestTemplateBuilder()
      .additionalMessageConverters(new GoogleMessageHttpMessageConverter())
      .additionalMessageConverters(new GoogleGenericJsonHttpMessageConverter()
        .setUnwrapRootValue(true))
      .additionalInterceptors(this.googleCloudRequestInterceptor())
      // for debug
      .additionalInterceptors(new LoggingRequestResponseInterceptor())
      .build();
  }

  @Bean
  GoogleCloudClientHttpRequestInterceptor googleCloudRequestInterceptor() {
    if (this.properties.getJsonKey() != null) {
      return new GoogleCredentialsHttpRequestInterceptor(this.googleCredentials());
    }
    if (this.properties.getApiKey() != null) {
      return new GoogleApiKeyHttpRequestInterceptor(this.properties.getApiKey());
    }
    throw new IllegalStateException("Neither google.cloud.json-key nor google.cloud.api-key is defined.");
  }

  @Bean
  @ConditionalOnProperty(name = "google.cloud.json-key")
  @SneakyThrows
  GoogleCredentials googleCredentials() {
    return GoogleCredentials.fromStream(this.properties.getJsonKey().getInputStream())
      .createScoped(this.properties.getScopes());
  }
}
