package jp.uich.demo.google.cloud.api.translation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

@Component
public class SpringGoogleCloudTranslationApi implements GoogleCloudTranslationApi {

  @Resource(name = "googleCloud.restTemplate")
  private RestTemplate restTemplate;

  @Override
  public String translate(String source, Locale targetLangLocale) {
    return this.restTemplate.postForObject(UriComponentsBuilder.fromUri(TRANSLATE_URI)
      .queryParam("q", source)
      .queryParam("target", targetLangLocale.getLanguage())
      .build().toUri(), null, TranslationsListResponse.class)
      .getTranslations()
      .stream()
      .map(TranslationsResource::getTranslatedText)
      .findFirst()
      .get();
  }

  @Override
  public Map<String, String> translate(List<String> sources, Locale targetLangLocale) {
    Stream<String> targetStream = this.restTemplate.postForObject(UriComponentsBuilder.fromUri(TRANSLATE_URI)
      .queryParam("q", sources.toArray())
      .queryParam("target", targetLangLocale.getLanguage())
      .build().toUri(), null, TranslationsListResponse.class)
      .getTranslations()
      .stream()
      .map(TranslationsResource::getTranslatedText);

    return Streams.zip(sources.stream(), targetStream, Maps::immutableEntry)
      .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public String translate(String source, Locale sourceLangLocale, Locale targetLangLocale) {
    return this.restTemplate.postForObject(UriComponentsBuilder.fromUri(TRANSLATE_URI)
      .queryParam("q", source)
      .queryParam("source", sourceLangLocale.getLanguage())
      .queryParam("target", targetLangLocale.getLanguage())
      .build().toUri(), null, TranslationsListResponse.class)
      .getTranslations()
      .stream()
      .map(TranslationsResource::getTranslatedText)
      .findFirst()
      .get();
  }

  @Override
  public Map<String, String> translate(List<String> sources, Locale sourceLangLocale, Locale targetLangLocale) {
    Stream<String> targetStream = this.restTemplate.postForObject(UriComponentsBuilder.fromUri(TRANSLATE_URI)
      .queryParam("q", sources.toArray())
      .queryParam("source", sourceLangLocale.getLanguage())
      .queryParam("target", targetLangLocale.getLanguage())
      .build().toUri(), null, TranslationsListResponse.class)
      .getTranslations()
      .stream()
      .map(TranslationsResource::getTranslatedText);

    return Streams.zip(sources.stream(), targetStream, Maps::immutableEntry)
      .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

}
