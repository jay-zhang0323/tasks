# Helm spec draft

## General Name Convention

### Chart Names

Chart names must be lower case letters and numbers. Words may be separated with dashes (-):

Examples:

```
drupal
nginx-lego
aws-cluster-autoscaler
```

Neither uppercase letters nor underscores can be used in chart names. Dots should not be used in chart names.

### Version Numbers

Wherever possible, Helm uses SemVer 2 to represent version numbers. (Note that Docker image tags do not necessarily follow SemVer, and are thus considered an unfortunate exception to the rule.)

When SemVer versions are stored in Kubernetes labels, we conventionally alter the + character to an _ character, as labels do not allow the + sign as a value.

### Formatting YAML

YAML files should be indented using two spaces (and never tabs).

### Conditions and Tags

## Install Charts

Recommend using upgrade with the --install option, which makes sure to install the Chart in a new cluster or upgrade the configuration.

```
helm upgrade --install <name> <chart> \
   --wait \
   -f <path to values.yaml> \
   --set <images.my-app=gcr.io/test/my-app:...>
```

## Charts Structure  * 1 day *

### Helm Values

### Helm Subcharts

### Helm Templates


## Labels and Annotations  * 1 day ( 2 days) *

## Role-Based Access Control * 1 day * 

## Automate GitHub Page Charts * 2 days (not including example) *

## Syncing up Chart Repository using GCS * 1 day (not including example) * 

## Full NGINX example * 7 days *

## Best practise
  1. Use available repository ASAP and put it into local setup
```
$ helm repo add stable https://charts.helm.sh/stable

"stable" has been added to your repositories

Then you can search for charts, for example, MySQL:  

$ helm search hub mysql

URL CHART VERSION  APP VERSION DESCRIPTION

https://hub.helm.sh/charts/bitnami/mysql 8.2.3 8.0.22 Chart to create a Highly available MySQL cluster

https://hub.helm.sh/charts/t3n/mysql 0.1.0 8.0.22 Fast, reliable, scalable, and easy to use open-...
```
  2. Use subcharts to manage your dependency
    1. Chart structure
```
backend-chart
  - Chart.yaml
  - charts
      - message-queue
          - Chart.yaml
          - templates
          - values.yaml
      - database
          - Chart.yaml
          - templates
          - values.yaml
  - values.yaml
```
    2. Chart.yaml
```
backend-chart
  - Chart.yaml
  - charts
      - message-queue
          - Chart.yaml
          - templates
          - values.yaml
      - database
          - Chart.yaml
          - templates
          - values.yaml
  - values.yaml
```
    3. values.yaml
```
message-queue:
  enabled: true
  image:
    repository: acme/rabbitmq
    tag: latest
database:
  enabled: false
```
  3. Use label to find resource easily(helpers.tpl)
    1. helpers.tpl
```
message-queue:
  enabled: true
  image:
    repository: acme/rabbitmq
    tag: latest
database:
  enabled: false
```
    2. Use "include" function with labels in the resource template
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-queue
  labels:
{{ include "common.labels" . | indent 4 }}
...

```
  4. Document your charts
    1. Comments in the yaml files
    2. README to explain how to use the charts
    3. NOTES.txt: The file is located in *template/NOTES.txt*. It can be also template with function. The content will be printed after installation and upgrade
  5. Test your charts
     It is recommended to write tests after installation
  6. Ensure Secrets Are Secure
  7. Make your Chart Resuable by Using Template Functions
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  environment: {{ .Values.environment | default "dev" | quote }}
  region: {{ .Values.region | upper | quote }}  

```
  8. Update your Deployment When ConfigMaps or Secrets change
```
kind: Deployment
spec:
  template:
    metadata:
      annotations:
        checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
...

```
  9. Opt Out of Resource Deletion with Resource Policies 
```
kind: Secret
metadata:
  annotations:
    "helm.sh/resource-policy": keep
...
```
 10. Useful Commands for Debugging Helm Charts
    1. helm lint: The linter tool conducts a series of tests to ensure your chart is correctly formed.
    2. helm install --dry-run --debug: This function renders the templates and shows the resulting resource manifests. You can also check all the resources before deployment and ensure the values are set and the templating functions work as expected.
    3. helm get manifest: This command retrieves the manifests of the resources that are installed to the cluster. If the release is not working as expected, this should be the first command you use to find out what is running in the cluster.
    4. helm get values: This command is used to retrieve the release values installed to the cluster. If you have any doubts about computed or default values, this should definitely be in your toolbelt.
 11. Use the lookup Function to Avoid Secret Regeneration
```
{{- $rootPasswordValue := (randAlpha 16) | b64enc | quote }}
{{- $secret := (lookup "v1" "Secret" .Release.Namespace "db-keys") }}
{{- if $secret }}
{{- $rootPasswordValue = index $secret.data "root-password" }}
{{- end -}}
apiVersion: v1
kind: Secret
metadata:
  name: db-keys
  namespace: {{ .Release.Namespace }}
type: Opaque
data:
  root-password: {{ $rootPasswordValue}}  

```
 12. Migrate to Helm 3 for Simpler and More Secure Kubernetes Applications
    1. Removal of Tiller 
    2. Improved chart upgrade strategy
 13. Keep Your Continuous Delivery Pipelines Idempotent

Reference from: https://devpress.csdn.net/k8s/62ebef4789d9027116a0fc89.html
