package jp.uich.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.uich.demo.dto.ImageInfo;
import jp.uich.demo.service.ColorService;

@RestController
public class ApiController {

  @Autowired
  private ColorService colorService;

  @GetMapping("/api/image/analyze")
  ImageInfo getImageInfo(@RequestParam String imageUrl) {
    return this.colorService.analyze(imageUrl);
  }
}
