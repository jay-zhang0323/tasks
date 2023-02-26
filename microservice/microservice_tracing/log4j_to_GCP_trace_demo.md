## Trace Details

From Trace details view, the logs are correlated with each traces:

<img src="./trace_with_correlated_logs.png" width=100%>

From Cloud Logging console, filter logs with trace id, all the correlated logs can be shown:

<img src="./logs_under_trace.png" width=100%>

Log example:

```
{
insertId: "tuzpewrnjcwf4ue3"
jsonPayload: {
context: "default"
logger: "com.example.demo.WorkController"
message: "meeting finished!"
thread: "http-nio-8080-exec-1"
}
labels: {
compute.googleapis.com/resource_name: "gke-tracing-demo-space-default-pool-07767c4a-muim"
k8s-pod/app: "trace-demo-service-a"
k8s-pod/pod-template-hash: "668d87d85b"
}
logName: "projects/demo-project/logs/stdout"
receiveTimestamp: "2023-02-20T08:33:35.852328243Z"
resource: {
labels: {
cluster_name: "tracing-demo-space"
container_name: "trace-demo-service-a-java"
location: "asia-northeast1-c"
namespace_name: "default"
pod_name: "trace-demo-service-a-668d87d85b-8s7kd"
project_id: "demo-project"
}
type: "k8s_container"
}
severity: "INFO"
spanId: "2b2709f3f0c71eb8"
timestamp: "2023-02-20T08:33:33.470Z"
trace: "projects/demo-project/traces/63f3305be7412a922b2709f3f0c71eb8"
}
```

## Implementation

### Overview

Log4j2 provides a default JSON layout for user to output the logs in JSON format. However the default JSON layout doesn't fit the format of Cloud Logging. Specifically, in order to implement Cloud logging and Cloud trace correlation, the log much have a `trace` field in the top level with the value in format `projects/PROJECT_ID/traces/TRACE_ID`. Though user can add custom fields into the JSON layout, those fields fall into the `jsonPayload` section rather than the top section, see example below:

```
{
  "insertId": "aer1maqg042y0lox",
  "jsonPayload": {
    "threadId": 31,
    "trace": "projects/demo-project/traces/63f6d7ca7421c3ec4de750449b9a65ab",
    "endOfBatch": false,
    "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
    "message": "meeting finished!",
    "spanId": "4de750449b9a65ab",
    "threadPriority": 5,
    "timeMillis": 1677121484879,
    "level": "INFO",
    "loggerName": "com.example.demo.WorkController",
    "contextMap": {
      "X-B3-TraceId": "projects/demo-project/traces/63f6d7ca7421c3ec4de750449b9a65ab",
      "ProjectId": "demo-project",
      "traceId": "63f6d7ca7421c3ec4de750449b9a65ab",
      "X-B3-SpanId": "4de750449b9a65ab",
      "spanId": "4de750449b9a65ab",
      "X-B3-ParentSpanId": null
    },
    "thread": "http-nio-8080-exec-1",
    "addtionalField": "demo-test",
    "project": "demo-project",
    "parent": "${ctx:X-B3-ParentSpanId}"
  },
  "timestamp": "2023-02-23T03:04:44.880330826Z",
  "severity": "INFO",
  "labels": {
    "k8s-pod/pod-template-hash": "6994c58585",
    "k8s-pod/app": "trace-demo-service-a",
    "compute.googleapis.com/resource_name": "gke-tracing-demo-space-default-pool-07767c4a-muim"
  },
  "receiveTimestamp": "2023-02-23T03:04:45.830817583Z"
}
```

Therefore even by adding customer field "trace", the log still cannot correlate with the trace correctly. 

In order to create a log layout which fits the requirement. We have to create a custom layout to outout the fields as exactly what we need. 

### pom.xml

Include log4j2 from spring boot start:

```
       <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter</artifactId>
	        <exclusions>
		        <exclusion>
			        <groupId>org.springframework.boot</groupId>
			        <artifactId>spring-boot-starter-logging</artifactId>
		        </exclusion>
	        </exclusions>
        </dependency>
            
        <!-- Add Log4j2 Dependency -->
        <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-log4j2</artifactId>
            	<!--
                	<exclusions>
                		<exclusion>
                      			<groupId>org.apache.logging.log4j</groupId>
                      			<artifactId>log4j-slf4j-impl</artifactId>
                		</exclusion>
            		</exclusions>
        	    -->
        </dependency>

```

Include Jackson packages for json format output:

```
  <!-- json format -->
        <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-core</artifactId>
        </dependency>
        <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-databind</artifactId>
        </dependency>
```

### log4j2.xml

Define a Custom Appender "CustomGCPAppender" with CustomLayout, which will be implemented later for customized JSON format. 

Define a Logger with name "CUSTOM_GCP_LOGGER" to use this appender. Also add this appender to root level. 

```
   <Appenders>
        <Console name="CustomGCPAppender">
            <CustomGCPLayout />
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="CUSTOM_GCP_LOGGER" level="debug" additivity="false">
            <AppenderRef ref="CustomGCPAppender" />
        </Logger>

        <Root level="info">
            <AppenderRef ref="CustomGCPAppender" />
        </Root>
    </Loggers>
```

### CustomGCPLayout

Generate the Custom Layout class as referred in [this doc](https://logging.apache.org/log4j/2.x/manual/extending.html#layouts).

The most important method is ` toSerializable(LogEvent event)`. Here is the place to control which fields are included in the logs and how they are formated. Here in order to match the log trace correlation. We add the field `trace` and `spanId`, the values are stored in the [Context Data](https://logging.apache.org/log4j/2.x/log4j-core/apidocs/org/apache/logging/log4j/core/LogEvent.html#getContextData--) of the LogEvent during the application runtime.  

```
    @Override
    public String toSerializable(LogEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("timestamp", event.getTimeMillis());
        putIfNotNull("severity", event.getLevel().toString(), map);
        putIfNotNull("thread", event.getThreadName(), map);
        putIfNotNull("logger", event.getLoggerFqcn(), map);
        putIfNotNull("message", event.getMessage().getFormattedMessage(), map);
        putIfNotNull("exception", getThrowableAsString(event.getThrownProxy()), map);
        putIfNotNull("trace", event.getContextData().getValue("X-B3-TraceId"), map);
        putIfNotNull("spanId", event.getContextData().getValue("X-B3-SpanId"), map);


        try {
            StringBuilderWriter writer = new StringBuilderWriter();
            OBJECT_MAPPER.writeValue(writer, map);
            writer.append(DEFAULT_EOL);
            return writer.toString();
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }
```

### Application

In the application, make sure we choose the right logger `CUSTOM_GCP_LOGGER`:

```
 //log4j logger
  private static final Logger LOGGER = LogManager.getLogger("CUSTOM_GCP_LOGGER");
```

In order to correlate logs and traces, we need to get the correct project id and the trace id during the runtime.

For project id, we can do that by using the code below:

```
import com.google.cloud.ServiceOptions;
...
...

//get default project id
String projectId = ServiceOptions.getDefaultProjectId();

```

For trace id, since Spring Sleuth has recorded the trace and span information. We just need to fetch the trace and span id from the current trace context. 
Spring Sleuth provided a `Tracer` class to help with this purpose. Once we get the current trace and span, we store them into the Thread Context which can be shared with the LogEvent. 

```
import brave.Span;
import brave.Tracer;

...
...

 @Autowired
 private Tracer tracer;
 
 ...
 ...
 
  private void logTraceSpan() {
     //Get current trace and span. 
     Span span = tracer.currentSpan();
     if (span != null) {
         //ThreadContext.put("ProjectId", projectId);
         ThreadContext.put("X-B3-TraceId",String.format("projects/%s/traces/%s", projectId, span.context().traceIdString()));
         ThreadContext.put("X-B3-SpanId",span.context().spanIdString());
         //ThreadContext.put("X-B3-ParentSpanId",span.context().parentIdString());
     }
     
  }
  
```
