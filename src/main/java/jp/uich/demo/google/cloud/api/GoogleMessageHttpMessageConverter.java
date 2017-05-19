package jp.uich.demo.google.cloud.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.ReflectionUtils;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import com.google.protobuf.util.JsonFormat.Printer;

import lombok.Setter;

public class GoogleMessageHttpMessageConverter extends AbstractHttpMessageConverter<Message> {

  private static final Map<Class<?>, Method> methodCache = new ConcurrentHashMap<Class<?>, Method>();

  @Setter
  private Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
  @Setter
  private Parser parser = JsonFormat.parser().ignoringUnknownFields();

  public GoogleMessageHttpMessageConverter() {
    super(StandardCharsets.UTF_8, MediaType.APPLICATION_JSON);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return Message.class.isAssignableFrom(clazz);
  }

  @Override
  protected MediaType getDefaultContentType(Message t) throws IOException {
    return new MediaType(MediaType.APPLICATION_JSON, this.getDefaultCharset());
  }

  @Override
  protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException {
    MediaType contentType = inputMessage.getHeaders().getContentType();
    Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : this.getDefaultCharset());

    Message.Builder builder = getMessageBuilder(clazz);

    this.parser.merge(new InputStreamReader(inputMessage.getBody(), charset), builder);

    return builder.build();
  }

  @Override
  protected void writeInternal(Message message, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException {
    // Content-Typeとエンコードを特定
    MediaType contentType = outputMessage.getHeaders().getContentType();
    if (contentType == null) {
      contentType = this.getDefaultContentType(message);
    }
    Charset charset = contentType.getCharset();
    if (charset == null) {
      charset = this.getDefaultCharset();
    }

    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
    // JSONの書き出し
    this.printer.appendTo(message, outputStreamWriter);
    outputStreamWriter.flush();
  }

  private static Message.Builder getMessageBuilder(Class<? extends Message> clazz) {
    Method method = methodCache.computeIfAbsent(clazz, key -> ReflectionUtils.findMethod(key, "newBuilder"));
    return (Message.Builder) ReflectionUtils.invokeMethod(method, clazz);
  }

}
