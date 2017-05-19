package jp.uich.demo.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ClientHttpRequestInterceptor} For logging HTTP Request/Response。
 *
 * @author uich
 */
public class LoggingRequestResponseInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
    throws IOException {
    return Execution.from(request, body)
      .logRequest()
      .execute(execution)
      .logResponse()
      .getResponse();
  }

  @Slf4j(topic = "Http Notification Req/Res")
  private static class Execution {
    private final String uuid = UUID.randomUUID().toString();

    private HttpRequest request;
    private byte[] body;
    @Getter
    private ClientHttpResponse response;

    Execution(HttpRequest request, byte[] body) {
      this.request = request;
      this.body = body;
    }

    static Execution from(HttpRequest request, byte[] body) {
      return new Execution(request, body);
    }

    Execution execute(ClientHttpRequestExecution execution) throws IOException {
      this.response = execution.execute(this.request, this.body);
      return this;
    }

    Execution logRequest() {
      final String requestBody = ArrayUtils.isNotEmpty(this.body)
        ? StringUtils.toEncodedString(this.body, extractCharset(this.request.getHeaders()))
        : null;

      log.info("Request:[uuid:[{}], method:[{}], uri:[{}], body[{}], headers:[{}]]", this.uuid,
        this.request.getMethod(),
        this.request.getURI(), requestBody, this.request.getHeaders());

      return this;
    }

    Execution logResponse() {
      SafeResponse safe = new SafeResponse(this.response);

      log.info("Response:[uuid:[{}], status:[{}], text:[{}], body[{}], headers:[{}]]", this.uuid, safe.getStatusCode(),
        safe.getStatusText(), safe.getResponseBodyAsText(), safe.getHttpHeaders());

      this.response = safe.createRecycledClientHttpResponse();

      return this;
    }
  }

  private static Charset extractCharset(HttpHeaders httpHeaders) {
    return Optional.ofNullable(httpHeaders.getContentType())
      .map(MediaType::getCharset)
      .orElse(StandardCharsets.UTF_8);
  }

  @Getter
  private static class SafeResponse {

    private final ClientHttpResponse original;

    private final Charset responseCharset;

    private final String responseBodyAsText;
    private final String statusText;
    private final HttpStatus statusCode;
    private final HttpHeaders httpHeaders;

    SafeResponse(ClientHttpResponse response) {
      this.original = response;
      this.responseCharset = extractCharset(response.getHeaders());

      this.responseBodyAsText = ignoreError(() -> StreamUtils.copyToString(response.getBody(), this.responseCharset));
      this.statusText = ignoreError(response::getStatusText);
      this.statusCode = ignoreError(response::getStatusCode);
      this.httpHeaders = ignoreError(response::getHeaders);
    }

    ClientHttpResponse createRecycledClientHttpResponse() {
      return new RecycledClientHttpResponse(this.original, this.responseBodyAsText, this.responseCharset);
    }

    private static <T> T ignoreError(Callable<T> callable) {
      try {
        return callable.call();
      } catch (Throwable t) {
        // nop 通常の失敗ログに任せる
        return null;
      }
    }
  }

  private static class RecycledClientHttpResponse implements ClientHttpResponse {

    private final ClientHttpResponse original;
    private final InputStream reopenableInputStream;

    RecycledClientHttpResponse(ClientHttpResponse original, String responseBodyAsText, Charset responseCharset) {
      this.original = original;
      this.reopenableInputStream = responseBodyAsText != null
        ? new ByteArrayInputStream(responseBodyAsText.getBytes(responseCharset))
        : null;
    }

    @Override
    public HttpHeaders getHeaders() {
      return this.original.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
      return this.reopenableInputStream;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
      return this.original.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
      return this.original.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
      return this.original.getStatusText();
    }

    @Override
    public void close() {
      this.original.close();
    }
  }
}
