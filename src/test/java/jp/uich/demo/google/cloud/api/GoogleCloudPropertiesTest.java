package jp.uich.demo.google.cloud.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleCloudPropertiesTest {

  @Autowired
  private GoogleCloudProperties googleCloudProperties;

  @Test
  @SneakyThrows
  public void test() {
    System.out.println(this.googleCloudProperties);
    System.out.println(
      StreamUtils.copyToString(this.googleCloudProperties.getJsonKey().getInputStream(), StandardCharsets.UTF_8));

    assertThat(this.googleCloudProperties.getScopes()).isNotEmpty();
    Map<String, String> serviceAccountProperties = new ObjectMapper()
      .readValue(this.googleCloudProperties.getJsonKey().getInputStream(), new TypeReference<Map<String, String>>() {});
    assertThat(serviceAccountProperties).isNotEmpty();
    assertThat(serviceAccountProperties).containsEntry("type", "service_account");
  }

}
