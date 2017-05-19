package jp.uich.demo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.common.collect.Streams;

import jp.uich.demo.dto.ImageInfo;
import jp.uich.demo.dto.ImageInfo.ColorInfo;
import jp.uich.demo.dto.ImageInfo.LabelInfo;
import jp.uich.demo.dto.ImageInfo.RGB;
import jp.uich.demo.google.cloud.api.translation.GoogleCloudTranslationApi;
import jp.uich.demo.google.cloud.api.vision.GoogleCloudVisionApi;

@Service
public class DefaultColorService implements ColorService {

  @Autowired
  private GoogleCloudVisionApi visionApi;
  @Autowired
  private GoogleCloudTranslationApi translationApi;

  @Override
  public ImageInfo analyze(String imageUrl) {
    AnnotateImageResponse response = this.visionApi.call(AnnotateImageRequest.newBuilder()
      .addFeatures(Feature.newBuilder()
        .setType(Type.LABEL_DETECTION)
        .build())
      .addFeatures(Feature.newBuilder()
        .setType(Type.IMAGE_PROPERTIES)
        .build())
      .addFeatures(Feature.newBuilder()
        .setType(Type.WEB_DETECTION)
        .build())
      .setImage(Image.newBuilder()
        .setSource(ImageSource.newBuilder()
          .setImageUri(imageUrl))
        .build())
      .build());

    ImageInfo.Builder builder = ImageInfo.builder();

    List<String> englishLabels = Streams.concat(response.getLabelAnnotationsList().stream()
      .map(EntityAnnotation::getDescription),
      response.getWebDetection().getWebEntitiesList().stream()
        .map(WebEntity::getDescription))
      .collect(Collectors.toList());

    Map<String, String> japaneseTranslated = this.translationApi.translate(englishLabels, Locale.JAPANESE);

    builder.labels(Streams.concat(response.getLabelAnnotationsList().stream()
      .map(entity -> new LabelInfo(japaneseTranslated.get(entity.getDescription()), entity.getScore())),
      response.getWebDetection().getWebEntitiesList().stream()
        .map(entity -> new LabelInfo(japaneseTranslated.get(entity.getDescription()), entity.getScore())))
      .filter(labelInfo -> StringUtils.isNotBlank(labelInfo.getText()))
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toList()));

    builder.colors(response.getImagePropertiesAnnotation().getDominantColors().getColorsList().stream()
      .map(colorInfo -> new ColorInfo(new RGB(colorInfo.getColor()), colorInfo.getScore()))
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toList()));

    return builder.build();
  }
}
