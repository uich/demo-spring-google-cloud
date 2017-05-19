package jp.uich.demo.service;

import org.springframework.cache.annotation.Cacheable;

import jp.uich.demo.dto.ImageInfo;

public interface ColorService {

  @Cacheable(cacheNames = "GoogleVision", key = "#imageUrl")
  ImageInfo analyze(String imageUrl);

}
