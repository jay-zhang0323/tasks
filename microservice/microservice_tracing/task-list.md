## 2023-02-08

| Task Number | Priority | Task Name                                                     | Owner   | Status   |
|-------------|----------|---------------------------------------------------------------|---------|----------|
| 1           | P4       | Add apigee into microservice-tracing demo                     | Tony    | on going |
| 2           | P2       | GKE scheduled scaling                                         | Chunhua | on going |
| 3           | P3       | microservice trace log monitoring and alert                   |         | start    |
| 4           | P2       | How to define the trace log format (trace log in stackdriver) | Tony    | reviewing|



## How to define the trace log format (trace log in stackdriver)

Trace log field cannot be modified. Default fields: https://cloud.google.com/trace/docs/reference/v2/rest/v2/projects.traces/batchWrite#Span
Trace can be exported to Bigquery, then user can only query the fields that they are interested and generate report. https://cloud.google.com/trace/docs/trace-export-bigquery

##  microservice trace log monitoring and alert

Limited use case: https://cloud.google.com/trace/docs/trace-alerting
