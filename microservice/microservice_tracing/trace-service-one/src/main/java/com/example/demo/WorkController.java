package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.cloud.ServiceOptions;

import java.util.Random;

@RestController
@Slf4j
public class WorkController {

  //logback logger
  //private static final Log LOGGER = LogFactory.getLog(WorkController.class);

  //log4j logger
  private static final Logger LOGGER = LogManager.getLogger(WorkController.class);


  @Value("#{environment.TRACING_DEMO_SERVICE_HOST}")
  String someValue;
  
  @Autowired
  private Environment env;
  
  Random r = new Random();

  String projectId = ServiceOptions.getDefaultProjectId();

  @Autowired
  RestTemplate restTemplate;

  @GetMapping("/log")
	public String log() {
		String message = "This line was written to the log.";
		String secondMessage = "This line was also written to the log with the same Trace ID.";
		LOGGER.info(message);
		LOGGER.info(secondMessage);
		return message;
	}

  public void meeting(String meetUrl) {
    try {
      LOGGER.info("start meeting...");
      //log.info("someValue: " + someValue);
      // Delay for random number of milliseconds.
      // temporately use hard coded URL,will replace this with ENV variable
      String result = restTemplate.getForObject(meetUrl, String.class);
      LOGGER.info(result);
      Thread.sleep(r.nextInt(500));
    } catch (InterruptedException e) {
    }
  }

  @GetMapping("/")
  public String work(HttpServletRequest request) {
    // What is work? Meetings!
    // When you hit this URL, it'll call meetings() 5 times.
    // Each time will have a random delay.
    //String meetUrl = request.getRequestURL().toString() + "/meet";
    String serverName = request.getServerName();
    int portNumber = request.getServerPort();
    String meetUrl = "http://trace-demo-service-b:8080/meet";
    log.info("Demo start!");
    LOGGER.info("starting to work, access:" + meetUrl);
    for (int i = 0; i < 5; i++) {
      this.meeting(meetUrl);
    }
    LOGGER.info("Work complete!");
    log.info("Demo complete!");
    return "finished work!";
  }
}
