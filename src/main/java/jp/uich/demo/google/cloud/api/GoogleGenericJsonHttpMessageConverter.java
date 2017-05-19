package jp.uich.demo.google.cloud.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.jackson2.JacksonFactory;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class GoogleGenericJsonHttpMessageConverter extends AbstractHttpMessageConverter<GenericJson> {

  private String wrappingField = "data";
  private boolean unwrapRootValue = false;

  private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  public GoogleGenericJsonHttpMessageConverter() {
    super(StandardCharsets.UTF_8, MediaType.APPLICATION_JSON);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return GenericJson.class.isAssignableFrom(clazz);
  }

  @Override
  protected MediaType getDefaultContentType(GenericJson message) throws IOException {
    return new MediaType(MediaType.APPLICATION_JSON, this.getDefaultCharset());
  }

  @Override
  protected GenericJson readInternal(Class<? extends GenericJson> clazz, HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException {
    MediaType contentType = inputMessage.getHeaders().getContentType();
    Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : this.getDefaultCharset());

    JsonParser parser = this.jsonFactory.createJsonParser(inputMessage.getBody(), charset);

    if (this.unwrapRootValue == false) {
      return parser.parse(clazz);
    }

    JsonToken currentToken = parser.getCurrentToken();
    if (currentToken == null) {
      currentToken = parser.nextToken();
    }
    if (currentToken != JsonToken.END_OBJECT) {
      parser.skipToKey(this.wrappingField);
      return parser.parse(clazz);
    }

    return null;
  }

  @Override
  protected void writeInternal(GenericJson message, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException {
    MediaType contentType = outputMessage.getHeaders().getContentType();
    if (contentType == null) {
      contentType = this.getDefaultContentType(message);
    }
    Charset charset = contentType.getCharset();
    if (charset == null) {
      charset = this.getDefaultCharset();
    }

    JsonGenerator generator = this.jsonFactory.createJsonGenerator(outputMessage.getBody(), charset);
    generator.serialize(message);
    generator.flush();
  }

}
