package jp.uich.demo.google.cloud.api.vision;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultGoogleCloudVisionApiTest extends GoogleCloudVisionApiTestSupport {

  @Autowired
  private DefaultGoogleCloudVisionApi visionApi;

  @Override
  protected GoogleCloudVisionApi visionApi() {
    return this.visionApi;
  }

  @Test
  @Override
  public void testSingleCall() {
    this.doTestSingleCall();
  }

  @Test
  @Override
  public void testMultipleCall() {
    this.doTestMultipleCall();
  }

}
