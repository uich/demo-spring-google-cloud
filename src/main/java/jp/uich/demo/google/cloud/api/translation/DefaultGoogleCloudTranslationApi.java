package jp.uich.demo.google.cloud.api.translation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

@Primary
@Component
@ConditionalOnProperty(name = "google.cloud.json-key")
public class DefaultGoogleCloudTranslationApi implements GoogleCloudTranslationApi {

  @Autowired
  private GoogleCredentials credentials;

  private Translate translate;

  @PostConstruct
  void afterPropertiesSet() {
    this.translate = TranslateOptions.newBuilder()
      .setCredentials(this.credentials)
      .build()
      .getService();
  }

  @Override
  public String translate(String source, Locale targetLangLocale) {
    return this.translate.translate(source,
      TranslateOption.targetLanguage(targetLangLocale.getLanguage()))
      .getTranslatedText();
  }

  @Override
  public Map<String, String> translate(List<String> sources, Locale targetLangLocale) {
    Stream<String> targetStream = this.translate.translate(sources,
      TranslateOption.targetLanguage(targetLangLocale.getLanguage()))
      .stream()
      .map(Translation::getTranslatedText);

    return Streams.zip(sources.stream(), targetStream, Maps::immutableEntry)
      .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public String translate(String source, Locale sourceLangLocale, Locale targetLangLocale) {
    return this.translate.translate(source,
      TranslateOption.sourceLanguage(sourceLangLocale.getLanguage()),
      TranslateOption.targetLanguage(targetLangLocale.getLanguage()))
      .getTranslatedText();
  }

  @Override
  public Map<String, String> translate(List<String> sources, Locale sourceLangLocale, Locale targetLangLocale) {
    Stream<String> targetStream = this.translate.translate(sources,
      TranslateOption.sourceLanguage(sourceLangLocale.getLanguage()),
      TranslateOption.targetLanguage(targetLangLocale.getLanguage()))
      .stream()
      .map(Translation::getTranslatedText);

    return Streams.zip(sources.stream(), targetStream, Maps::immutableEntry)
      .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }
}
