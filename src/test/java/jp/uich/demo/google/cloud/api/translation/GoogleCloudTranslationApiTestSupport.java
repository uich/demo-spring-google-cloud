package jp.uich.demo.google.cloud.api.translation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Locale;

public abstract class GoogleCloudTranslationApiTestSupport {

  protected abstract GoogleCloudTranslationApi translationApi();

  public abstract void testTranslateStringLocale();

  public abstract void testTranslateListOfStringLocale();

  public abstract void testTranslateStringLocaleLocale();

  public abstract void testTranslateListOfStringLocaleLocale();

  protected void doTestTranslateStringLocale() {
    assertThat(this.translationApi().translate("mobile phone case", Locale.JAPANESE)).isEqualTo("携帯電話ケース");
  }

  protected void doTestTranslateListOfStringLocale() {
    assertThat(this.translationApi().translate(Arrays.asList("mobile phone case", "bag", "school"), Locale.JAPANESE))
      .containsEntry("mobile phone case", "携帯電話ケース")
      .containsEntry("bag", "バッグ")
      .containsEntry("school", "学校");
  }

  protected void doTestTranslateStringLocaleLocale() {
    assertThat(this.translationApi().translate("mobile phone case", Locale.ENGLISH, Locale.JAPANESE))
      .isEqualTo("携帯電話ケース");
  }

  protected void doTestTranslateListOfStringLocaleLocale() {
    assertThat(this.translationApi().translate(Arrays.asList("mobile phone case", "bag", "school"), Locale.ENGLISH,
      Locale.JAPANESE))
        .containsEntry("mobile phone case", "携帯電話ケース")
        .containsEntry("bag", "バッグ")
        .containsEntry("school", "学校");
  }
}
