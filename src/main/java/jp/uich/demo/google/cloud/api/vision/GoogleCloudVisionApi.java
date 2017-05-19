package jp.uich.demo.google.cloud.api.vision;

import java.net.URI;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;

public interface GoogleCloudVisionApi {

  static final URI VISION_API_URL = URI.create("https://vision.googleapis.com/v1/images:annotate");

  AnnotateImageResponse call(AnnotateImageRequest request);

  List<AnnotateImageResponse> call(AnnotateImageRequest request, AnnotateImageRequest... additional);
}
