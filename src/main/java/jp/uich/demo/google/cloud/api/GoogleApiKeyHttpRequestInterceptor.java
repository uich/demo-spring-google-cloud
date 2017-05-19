package jp.uich.demo.google.cloud.api;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleApiKeyHttpRequestInterceptor implements GoogleCloudClientHttpRequestInterceptor {

  private final String apiKey;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
    throws IOException {
    HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request) {
      @Override
      public URI getURI() {
        return UriComponentsBuilder.fromUri(request.getURI())
          .queryParam("key", GoogleApiKeyHttpRequestInterceptor.this.apiKey)
          .build(true)
          .toUri();
      }
    };

    return execution.execute(requestWrapper, body);
  }
}
