package jp.uich.demo.google.cloud.api.vision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.spi.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.common.collect.Iterables;

@Primary
@Component
@ConditionalOnProperty(name = "google.cloud.json-key")
@ConditionalOnClass(name = "org.eclipse.jetty.servlets.ConcatServlet")
public class DefaultGoogleCloudVisionApi implements GoogleCloudVisionApi, InitializingBean, DisposableBean {

  @Autowired
  private GoogleCredentials credentials;

  private ImageAnnotatorClient vision;

  @Override
  public void afterPropertiesSet() throws IOException {
    this.vision = ImageAnnotatorClient.create(ImageAnnotatorSettings.defaultBuilder()
      .setChannelProvider(ImageAnnotatorSettings.defaultChannelProviderBuilder()
        .setCredentialsProvider(FixedCredentialsProvider.create(this.credentials))
        .build())
      .build());
  }

  @Override
  public AnnotateImageResponse call(AnnotateImageRequest request) {
    BatchAnnotateImagesResponse response = this.vision.batchAnnotateImages(Collections.singletonList(request));
    return Iterables.getFirst(response.getResponsesList(), null);
  }

  @Override
  public List<AnnotateImageResponse> call(AnnotateImageRequest request, AnnotateImageRequest... additional) {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);
    if (ArrayUtils.isNotEmpty(additional)) {
      requests.addAll(Arrays.asList(additional));
    }

    BatchAnnotateImagesResponse response = this.vision.batchAnnotateImages(requests);

    return response.getResponsesList();
  }

  @Override
  public void destroy() throws Exception {
    this.vision.close();
  }

}
