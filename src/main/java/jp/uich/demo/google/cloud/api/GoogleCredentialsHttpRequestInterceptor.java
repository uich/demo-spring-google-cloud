package jp.uich.demo.google.cloud.api;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleCredentialsHttpRequestInterceptor implements GoogleCloudClientHttpRequestInterceptor {

  private final GoogleCredentials credentials;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
    throws IOException {
    String accessToken = this.getAccessToken();

    if (accessToken != null) {
      request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    return execution.execute(request, body);
  }

  private String getAccessToken() throws IOException {
    AccessToken accessToken = this.credentials.getAccessToken();
    if (accessToken == null) {
      return this.newAccessToken();
    }

    Date expirationTime = accessToken.getExpirationTime();
    if (expirationTime == null || expirationTime.getTime() - System.currentTimeMillis() < 5 * 60 * 1000) {
      return this.newAccessToken();
    }

    return accessToken.getTokenValue();
  }

  private synchronized String newAccessToken() throws IOException {
    this.credentials.refresh();
    return this.credentials.getAccessToken().getTokenValue();
  }

}
