package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import brave.Span;
import brave.Tracer;

import com.google.cloud.ServiceOptions;

import java.util.Random;

@RestController
@Slf4j
public class MeetingController {

  //Logback logger
  //private static final Log LOGGER = LogFactory.getLog(MeetingController.class);

  //log4j logger
  private static final Logger LOGGER = LogManager.getLogger("CUSTOM_GCP_LOGGER");

  @Autowired
  private Tracer tracer;

  Random r = new Random();

  //get default project id
  String projectId = ServiceOptions.getDefaultProjectId();


  private void logTraceSpan() {
    //Get current trace, span and parent span. 
    Span span = tracer.currentSpan();
    if (span != null) {
        //LOGGER.info("Span ID hex {}", span.context().spanIdString());
        //LOGGER.info("Trace ID hex {}", span.context().traceIdString());
        //LOGGER.info("Parent Span ID hex {}", span.context().parentIdString());
        ThreadContext.put("X-B3-TraceId",String.format("projects/%s/traces/%s", projectId, span.context().traceIdString()));
        ThreadContext.put("X-B3-SpanId",span.context().spanIdString());
    }
    
 }


  @GetMapping("/meet")
  public String meeting() {
    try {
      logTraceSpan();
      LOGGER.info("meeting in progress...");
      Thread.sleep(r.nextInt(500 - 20 + 1) + 20);
    } catch (InterruptedException e) {
    }
    return "meeting finished!";
  }
}
