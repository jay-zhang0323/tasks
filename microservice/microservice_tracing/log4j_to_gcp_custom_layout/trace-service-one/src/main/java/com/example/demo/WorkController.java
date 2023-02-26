package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.google.cloud.ServiceOptions;

import java.util.Random;
import java.io.IOException;


import brave.Span;
import brave.Tracer;



@RestController
@Slf4j
public class WorkController {

  //logback logger
  //private static final Log LOGGER = LogFactory.getLog(WorkController.class);

  //log4j logger
  private static final Logger LOGGER = LogManager.getLogger("CUSTOM_GCP_LOGGER");

  @Value("#{environment.TRACING_DEMO_SERVICE_HOST}")
  String someValue;
  
  @Autowired
  private Environment env;

  @Autowired
  private Tracer tracer;
  
  Random r = new Random();

  //get default project id
  String projectId = ServiceOptions.getDefaultProjectId();

  @Autowired
  RestTemplate restTemplate;

  @GetMapping("/log")
	public String log(HttpServletRequest request) {

        //System.setProperty("es.logs.base_path", "target");
        //System.setProperty("es.logs.cluster_name", "_cluster");
        logTraceSpan();
       
        //TEST: set json format string into log message
        //Map<String,String> msgMap = new HashMap<>();
        //msgMap.put("myKey", "myValue");
        //JSONObject msg = new JSONObject(msgMap);
        //LOGGER.info(msg.toString());


		String message = "This line was written to the log.";
		String secondMessage = "This line was also written to the log with the same Trace ID.";
		LOGGER.info(message);
		LOGGER.info(secondMessage);
		return message;
	}

  private void logTraceSpan() {
     //Get current trace, span and parent span. 
     Span span = tracer.currentSpan();
     if (span != null) {
         //ThreadContext.put("ProjectId", projectId);
         ThreadContext.put("X-B3-TraceId",String.format("projects/%s/traces/%s", projectId, span.context().traceIdString()));
         ThreadContext.put("X-B3-SpanId",span.context().spanIdString());
         //ThreadContext.put("X-B3-ParentSpanId",span.context().parentIdString());
     }
     
  }

  public void meeting(String meetUrl) {
    try {
      logTraceSpan();
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
    logTraceSpan();
    String serverName = request.getServerName();
    int portNumber = request.getServerPort();
    String meetUrl = "http://trace-demo-service-b:8080/meet";

    log.info("Demo start!");
    LOGGER.info("starting to work, access:" + meetUrl);
    for (int i = 0; i < 5; i++) {
      LOGGER.debug("meeting " + i);
      this.meeting(meetUrl);
    }
    LOGGER.info("Work complete!");
    log.info("Demo complete!");
    return "finished work!";
  }
}
