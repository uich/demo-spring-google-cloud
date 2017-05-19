package jp.uich.demo.google.cloud.api.translation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringGoogleCloudTranslationApiTest extends GoogleCloudTranslationApiTestSupport {

  @Autowired
  SpringGoogleCloudTranslationApi translationApi;

  @Override
  protected GoogleCloudTranslationApi translationApi() {
    return this.translationApi;
  }

  @Test
  @Override
  public void testTranslateStringLocale() {
    this.doTestTranslateStringLocale();
  }

  @Test
  @Override
  public void testTranslateListOfStringLocale() {
    this.doTestTranslateListOfStringLocale();
  }

  @Test
  @Override
  public void testTranslateStringLocaleLocale() {
    this.doTestTranslateStringLocaleLocale();
  }

  @Test
  @Override
  public void testTranslateListOfStringLocaleLocale() {
    this.doTestTranslateListOfStringLocaleLocale();
  }

}
