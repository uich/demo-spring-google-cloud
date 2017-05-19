package jp.uich.demo.google.cloud.api.translation;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface GoogleCloudTranslationApi {

  static final URI TRANSLATE_URI = URI.create("https://translation.googleapis.com/language/translate/v2");
  static final URI DETECT_URI = URI.create("https://translation.googleapis.com/language/translate/v2/detect");

  String translate(String source, Locale targetLangLocale);

  Map<String, String> translate(List<String> sources, Locale targetLangLocale);

  String translate(String source, Locale sourceLangLocale, Locale targetLangLocale);

  Map<String, String> translate(List<String> sources, Locale sourceLangLocale, Locale targetLangLocale);
}
