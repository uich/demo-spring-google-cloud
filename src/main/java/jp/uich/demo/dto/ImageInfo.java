package jp.uich.demo.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(builderClassName = "Builder")
public class ImageInfo implements Serializable {

  private final List<LabelInfo> labels;
  private final List<ColorInfo> colors;

  @Data
  public static class LabelInfo implements Serializable, Comparable<LabelInfo> {
    private final String text;
    private final Float score;

    @Override
    public int compareTo(LabelInfo o) {
      return Comparator.comparing(LabelInfo::getScore).compare(this, o);
    }

  }

  @Data
  public static class ColorInfo implements Serializable, Comparable<ColorInfo> {
    private final RGB rgb;
    private final Float score;

    @Override
    public int compareTo(ColorInfo o) {
      return Comparator.comparing(ColorInfo::getScore).compare(this, o);
    }
  }

  @Data
  @RequiredArgsConstructor
  public static class RGB implements Serializable {

    private static final int MAX_VALUE = 0xff;

    private final int red;
    private final int green;
    private final int blue;

    public RGB(com.google.type.Color color) {
      this.red = (int) color.getRed();
      this.green = (int) color.getGreen();
      this.blue = (int) color.getBlue();
    }

    public String htmlRgb() {
      return "#"
        + StringUtils.leftPad(Integer.toHexString(this.red), 2, '0')
        + StringUtils.leftPad(Integer.toHexString(this.green), 2, '0')
        + StringUtils.leftPad(Integer.toHexString(this.blue), 2, '0');
    }

    public RGB opposite() {
      return new RGB(MAX_VALUE - this.red, MAX_VALUE - this.green, MAX_VALUE - this.blue);
    }

    public RGB complementary() {
      int base = Collections.min(Arrays.asList(this.red, this.green, this.blue)) + MAX_VALUE;
      return new RGB(base - this.red, base - this.green, base - this.blue);
    }
  }

}
