package jp.uich.demo.google.cloud.api.vision;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;

public abstract class GoogleCloudVisionApiTestSupport {

  protected abstract GoogleCloudVisionApi visionApi();

  public abstract void testSingleCall();

  public abstract void testMultipleCall();

  protected void doTestSingleCall() {
    String imageUrl = "https://ssl-stat.amebame.com/pub/content/8763370595/account/204/item/65e80aab-8339-4bf1-b4af-3c9bf421ad2c.jpg";

    AnnotateImageRequest request = this.createRequest(imageUrl,
      Type.LABEL_DETECTION,
      Type.IMAGE_PROPERTIES,
      Type.LOGO_DETECTION,
      Type.WEB_DETECTION,
      Type.TEXT_DETECTION);

    AnnotateImageResponse response = this.visionApi().call(request);
    assertThat(response.getLabelAnnotationsList()).isNotEmpty();
    assertThat(response.getImagePropertiesAnnotation().getDominantColors().getColorsList()).isNotEmpty();
    assertThat(response.getLogoAnnotationsList()).isNotNull();
    assertThat(response.getWebDetection().getWebEntitiesList()).isNotEmpty();
    assertThat(response.getTextAnnotationsList()).isNotEmpty();
  }

  protected void doTestMultipleCall() {
    String imageUrl1 = "https://ssl-stat.amebame.com/pub/content/8763370595/account/26830/item/58d8b8aa-22dd-4084-ba61-08a03d5a0df0.jpg";
    String imageUrl2 = "https://ssl-stat.amebame.com/pub/content/8763370595/account/26830/item/15805492-b929-444b-afb1-835f8dbbf7c8.jpg";

    AnnotateImageRequest request1 = this.createRequest(imageUrl1, Type.LABEL_DETECTION);
    AnnotateImageRequest request2 = this.createRequest(imageUrl2, Type.LABEL_DETECTION, Type.IMAGE_PROPERTIES);

    List<AnnotateImageResponse> responses = this.visionApi().call(request1, request2);

    assertThat(responses.get(0).getLabelAnnotationsList()).isNotEmpty();
    assertThat(responses.get(0).getImagePropertiesAnnotation().getDominantColors().getColorsList()).isEmpty();

    assertThat(responses.get(1).getLabelAnnotationsList()).isNotEmpty();
    assertThat(responses.get(1).getImagePropertiesAnnotation().getDominantColors().getColorsList()).isNotEmpty();
  }

  AnnotateImageRequest createRequest(String imageUrl, Type... featureTypes) {
    return AnnotateImageRequest.newBuilder()
      .addAllFeatures(Arrays.stream(featureTypes)
        .map(type -> Feature.newBuilder()
          .setType(type)
          .build())
        .collect(Collectors.toList()))
      .setImage(Image.newBuilder()
        .setSource(ImageSource.newBuilder()
          .setImageUri(imageUrl))
        .build())
      .build();
  }
}
