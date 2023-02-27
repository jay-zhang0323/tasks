## 2023-02-08

| Task Number | Priority | Task Name                                                     | Owner   | Status   |
|-------------|----------|---------------------------------------------------------------|---------|----------|
| 1           | P4       | Add apigee into microservice-tracing demo                     | Tony    | on going |
| 2           | P2       | GKE scheduled scaling                                         | Chunhua | on going |
| 3           | P3       | microservice trace log monitoring and alert                   |         | start    |
| 4           | P2       | How to define the trace log format (trace log in stackdriver) | Tony    | reviewing|
| 5           | P3       | Export trace to GCP with Opentelemetry                      |   Tony      | Done    |
| 6           | P3       | Export trace to GCP with Spring Sleuth                        |   Tony      | Done    |
| 7           | P2       | Spring Boot log and Cloud Trace correlation                   | Tony    | Done |
| 7.1           | P3       | Spring Boot logging with logback (support GCP logging by default)   |         | done    |
| 7.2           | P2       | Sping Boot logging with log4j (doesn't support GCP logging by default) | Tony    |done|
| 7.3           | P3       | Spring Boot logging with Log4j + log4j_to_slf4j adapter    |         | done    |
| 7.4           | P2       | Sping Boot logging as JSON structure with log4j default JSON template (adding project id and trace id into JSON structure)  | Tony    | done|
| 7.5           | P2       | Sping Boot logging as JSON structure with log4j custom template (adding project id and trace id into JSON structure)  | Tony    | done|




## How to define the trace log format (trace log in stackdriver)

Trace log field cannot be modified. Default fields: https://cloud.google.com/trace/docs/reference/v2/rest/v2/projects.traces/batchWrite#Span
Trace can be exported to Bigquery, then user can only query the fields that they are interested and generate report. https://cloud.google.com/trace/docs/trace-export-bigquery

##  microservice trace log monitoring and alert

Limited use case: https://cloud.google.com/trace/docs/trace-alerting

##  Spring Boot log and Cloud Trace correlation 

### Spring Boot logging with logback (support GCP logging by default) 

Spring support ouptut logging to GCP Cloud logging by default, the trace id are written into logs by spring-gcp-starter-logging library by default. 

### Sping Boot logging with log4j (doesn't support GCP logging by default)

Spring doesn't support similar library as  spring-gcp-starter-logging for log4j. Need to figure out other way to implement log correlation. There are 2 possible ways:

### Spring Boot logging with Log4j + log4j_to_slf4j adapter 

Use log4j_to_slf4j adapter to bridge the log4j to slf4j. So that the logs written by log4j are adapted into slf4j in spring, which will then use logback library to ingest logs to GCP. 

### Sping Boot logging as JSON structure with log4j default JSON template (adding project id and trace id into JSON structure)

The other way is using log4j to ouptut the logs in JSON format, and insert project id and traceid correctly. (e.g. "projects/<project_id>/traces/<trace_id>"
)

However when using default JSON template, all the custom fields are added inside of "JsonPayload" section, which doesn't match the requirement. So we canot work it out by simply using default JSON template.

###  Sping Boot logging as JSON structure with log4j custom template (adding project id and trace id into JSON structure)

Tried the other way by using the custom template to format the json structure from the scratch. In this way we can control all the custom fields and add them into the right place in the log structure. 

