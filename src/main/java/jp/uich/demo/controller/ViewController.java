package jp.uich.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.uich.demo.service.ColorService;

@Controller
public class ViewController {

  @Autowired
  private ColorService colorService;

  @GetMapping("/image/analyze")
  String view(Model model, @RequestParam String imageUrl) {
    model.addAttribute(this.colorService.analyze(imageUrl));
    model.addAttribute("imageUrl", imageUrl);
    return "view";
  }
}
