package jp.uich.demo.google.cloud.api.vision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.common.collect.Iterables;

@Component
public class SpringGoogleCloudVisionApi implements GoogleCloudVisionApi {

  @Resource(name = "googleCloud.restTemplate")
  private RestTemplate restTemplate;

  @Override
  public AnnotateImageResponse call(AnnotateImageRequest request) {
    BatchAnnotateImagesRequest content = BatchAnnotateImagesRequest.newBuilder()
      .addRequests(request)
      .build();

    BatchAnnotateImagesResponse response = this.restTemplate.postForObject(VISION_API_URL, content,
      BatchAnnotateImagesResponse.class);

    return Iterables.getFirst(response.getResponsesList(), null);
  }

  @Override
  public List<AnnotateImageResponse> call(AnnotateImageRequest request, AnnotateImageRequest... additional) {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);
    if (ArrayUtils.isNotEmpty(additional)) {
      requests.addAll(Arrays.asList(additional));
    }

    BatchAnnotateImagesRequest content = BatchAnnotateImagesRequest.newBuilder()
      .addAllRequests(requests)
      .build();

    BatchAnnotateImagesResponse response = this.restTemplate.postForObject(VISION_API_URL, content,
      BatchAnnotateImagesResponse.class);

    return response.getResponsesList();
  }

}
