package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

@RestController
@Slf4j
public class MeetingController {

  private static final Log LOGGER = LogFactory.getLog(MeetingController.class);

  Random r = new Random();

  @GetMapping("/meet")
  public String meeting() {
    try {
      LOGGER.info("meeting in progress...");
      Thread.sleep(r.nextInt(500 - 20 + 1) + 20);
    } catch (InterruptedException e) {
    }
    return "meeting finished!";
  }
}
