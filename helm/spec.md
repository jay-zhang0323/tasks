# Helm spec draft

# Menu

- [Overview](#1-overview)
- [General Name Convention](#2-general-name-convention)
- [Create Charts](#3-create-charts)
  - [Helm Value](#31-helm-values)
  - [Helm Template](#32-helm-templates)
  - [Helm Subcharts](#33-helm-subcharts)
  - [Labels and Annotations](#34-labels-and-annotations)
- [Deploy and Test](#4-deploy-and-test)
  - [Deploy Charts](#41-deploy-charts)
  - [Test Charts](#42-test-charts)
- [Best Practices](#5-best-practices)
  - [Role Based Access Control](#51-role-based-access-control)
  - []

## 1. Overview

Overview of this spec. 

## 2. General Name Convention

### 2.1 Chart Names

Chart names must be lower case letters and numbers. Words may be separated with dashes (-):

Examples:

```
drupal
nginx-lego
aws-cluster-autoscaler
```

Neither uppercase letters nor underscores can be used in chart names. Dots should not be used in chart names.

### 2.2 Version Numbers

Wherever possible, Helm uses SemVer 2 to represent version numbers. (Note that Docker image tags do not necessarily follow SemVer, and are thus considered an unfortunate exception to the rule.)

When SemVer versions are stored in Kubernetes labels, we conventionally alter the + character to an _ character, as labels do not allow the + sign as a value.

### 2.3 Formatting YAML

YAML files should be indented using two spaces (and never tabs).

### 2.4 Conditions and Tags

## 3. Create Charts

### 3.1 Helm Values

This part of the best practices guide covers using values. In this part we provide recommendations on how you should structure and use your values, with focus on designing a chart's values.yaml file.

1. Variable names should begin with a lowercase letter, and words should be separated with camelcase:

Example:

```
chicken: true
chickenNoodleSoup: true
```

*NOTE: All of Helm's built-in variables begin with an uppercase letter to easily distinguish them from user-defined values: .Release.Name, .Capabilities.KubeVersion*

2. Flat vs Nested Values:

Nested:

```
server:
  name: nginx
  port: 80
```

Flat:

```
serverName: nginx
serverPort: 80
```

For optimal safety, a nested value must be checked at every level:

```
{{ if .Values.server }}
  {{ default "none" .Values.server.name }}
{{ end }}
```
But for flat configuration, such checks can be skipped, making the template easier to read and use. For this reason, in general flatten value is flavored over nested value.

When there are a large number of related variables, and at least one of them is non-optional, nested values may be used to improve readability.

3. Quote all Strings:

YAML's type coercion rules are sometimes counterintuitive. For example, `foo: false` is not the same as `foo: "false"`. Large integers like `foo: 12345678` will get converted to scientific notation in some cases.

The easiest way to avoid type conversion errors is to be explicit about strings, and implicit about everything else. Or, in short, quote all strings.

Recommended: 

```
foo: "false"
```
Not Recommended:

```
foo: false
```


Similarly, to avoid the integer casting issues, it is advantageous to store your integers as strings as well, and use `{{ int $value }}` in the template to convert from a string back to an integer.

Example:

```
foo: "123456"
...
...
bar: {{ int .Values.foo }}
``` 

4. Make value.yaml easy to override from `--set`

There are three potential sources of values:

- A chart's values.yaml file
- A values file supplied by `helm install -f` or `helm upgrade -f`
- The values passed to a `--set` or `--set-string` flag on helm install or helm upgrade

Sometimes users may want to override the values via either the `-f` flag or with the `--set` option. For example, use the value for dev environment, or troubleshooting a bug. 

```
# helm install mychart -f value-dev.yaml
# helm install mychart -s 'foo=bar'
```

Since `--set` is more limited in expressiveness, the first guidelines for writing your values.yaml file is make it easy to override from `--set`.

For this reason, it's often better to structure your values file using maps.

Difficult to use with `--set`:

```
servers:
  - name: foo
    port: 80
  - name: bar
    port: 81
 ```
 
The above cannot be expressed with `--set` in Helm <=2.4. In Helm >=2.5, accessing the port on foo is `--set servers[0].port=80`. Not only is it harder for the user to figure out, but it is prone to errors if at some later time the order of the servers is changed.

Easy to use:

```
servers:
  foo:
    port: 80
  bar:
    port: 81
 ```
 
Accessing foo's port is much more obvious: `--set servers.foo.port=80`.

5. Document the values

Every defined property in values.yaml should be documented. The documentation string should begin with the name of the property that it describes, and then give at least a one-sentence description.

Incorrect:

```
# the host name for the webserver
serverHost: example
serverPort: 9191
```

Correct:

```
# serverHost is the host name for the webserver
serverHost: example
# serverPort is the HTTP listener port for the webserver
serverPort: 9191
```

Beginning each comment with the name of the parameter it documents makes it easy to grep out documentation, and will enable documentation tools to reliably correlate doc strings with the parameters they describe.

### 3.2 Helm Templates
  
#### Structure of templates/

The `templates/` directory should be structured as follows:

  - Template files should have the extension .yaml if they produce YAML output. The extension .tpl may be used for template files that produce no formatted content.
  - Template file names should use dashed notation (my-example-configmap.yaml), not camelcase.
  - Each resource definition should be in its own template file.
  - Template file names should reflect the resource kind in the name. e.g. foo-pod.yaml, bar-svc.yaml
  
#### Names of Defined Templates should be namespaced
  
Defined templates (templates created inside a `{{ define }}` directive) are globally accessible. That means that a chart and all of its subcharts will have access to all of the templates created with `{{ define }}`.

For that reason, all defined template names should be namespaced.

Correct:

```
{{- define "nginx.fullname" }}
{{/* ... */}}
{{ end -}}
```  
Incorrect:

```
{{- define "fullname" -}}
{{/* ... */}}
{{ end -}}
```

It is highly recommended that new charts are created via `helm create` command as the template names are automatically defined as per this best practice.

#### Formatting Templates

Templates should be indented using two spaces (never tabs).

Template directives should have whitespace after the opening braces and before the closing braces:

Correct:

```
{{ .foo }}
{{ print "foo" }}
{{- print "bar" -}}
```
  
Incorrect:

```
{{.foo}}
{{print "foo"}}
{{-print "bar"-}}
```

Templates should chomp whitespace where possible:

```
foo:
  {{- range .Values.items }}
  {{ . }}
  {{ end -}}
```

Blocks (such as control structures) may be indented to indicate flow of the template code.

```
{{ if $foo -}}
  {{- with .Bar }}Hello{{ end -}}
{{- end -}}
```
  
However, since YAML is a whitespace-oriented language, it is often not possible for code indentation to follow that convention.

#### Whitespace in Generated Templates

It is preferable to keep the amount of whitespace in generated templates to a minimum. In particular, numerous blank lines should not appear adjacent to each other. But occasional empty lines (particularly between logical sections) is fine.

This is best:

```
apiVersion: batch/v1
kind: Job
metadata:
  name: example
  labels:
    first: first
    second: second
```

This is okay:

```
apiVersion: batch/v1
kind: Job

metadata:
  name: example

  labels:
    first: first
    second: second
```

This should be avoided:

```
apiVersion: batch/v1
kind: Job

metadata:
  name: example





  labels:
    first: first

    second: second

```

#### Comments (YAML Comments vs. Template Comments)
  
Both YAML and Helm Templates have comment markers.

YAML comments:

```
# This is a comment
type: sprocket
```
  
Template Comments:

```
{{- /*
This is a comment.
*/}}
type: frobnitz
```
  
Template comments should be used when documenting features of a template, such as explaining a defined template:

```
{{- /*
mychart.shortname provides a 6 char truncated version of the release name.
*/}}
{{ define "mychart.shortname" -}}
{{ .Release.Name | trunc 6 }}
{{- end -}}
```
  
Inside of templates, YAML comments may be used when it is useful for Helm users to (possibly) see the comments during debugging.

```
# This may cause problems if the value is more than 100Gi
memory: {{ .Values.maxMem | quote }}
```
  
The comment above is visible when the user runs helm install --debug, while comments specified in {{- /* */}} sections are not.
  
### 3.3 Helm Subcharts

Helm subcharts are useful when 2 or more services have a common dependency. These subcharts have their own values and templates. They can also share a global value in the values, where both (parent and subchart) can use the same value.

There are a few notes about application subcharts.

- A subchart is considered "stand-alone", which means a subchart can never explicitly depend on its parent chart.
For that reason, a subchart cannot access the values of its parent.
- A parent chart can override values for subcharts.
- Helm has a concept of global values that can be accessed by all charts.

One example scenario for using subcharts is:

1. The microservices expose their APIs via urls.
2. As a standard practice there can be multiple different deployments at different phases (sanity/regression/performance/etc.), they should be completely isolated environments and there can be respective CI/CD pipelines for each of them.
3. Sometimes we need to create an ad-hoc isolated environment manually from certain releases in order to troubleshoot/reproduce Bugs.
4. For the above scenarios, we may need a different <helm-release-name> to vary from environment to environment, for example, it might be alpha, beta, troubleshooting etc. Hence all the respective micro-services API endpoint URL???s will need to change to avoid confliction, for example https://alpha-order-management/api, https://prod-weather/api etc.
5. In this case, if we start hard-coding <helm-release-name> for all API endpoint URL environment variables, the respective maintenance effort grows over time. Also for manual deployment it becomes difficult to configure each dependency separately.

As a possible solution, a suggested subcharts structure is like below:

```
microservices-chart
????- Chart.yaml
????- charts
????  - order-management
????????  - Chart.yaml
????????  - templates
????????  - values.yaml
????- customer-management
????????- Chart.yaml
????????- templates
????????- values.yaml
  - weather
  ????- Chart.yaml
????????- templates
????????- values.yaml
  -templates
    - _helpers.tpl
????- values.yaml
  - README.md
  - .helmignore
```

In file Chart.yaml of the parent chart, add all the Subcharts path, version and condition to enable/disable:
   
```
apiVersion: v2
name: microservices-chart
description: A Parent Helm chart for Kubernetes
type: application
# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates
version: 0.1.0
# All sub micro-services Helm Charts that are needed
dependencies:
- name: order-management
  repository: file://charts/order-management
  version: 0.1.0
  condition: order-management.enabled
- name: customer-management
  repository: file://charts/customer-management
  version: 0.1.0
  condition: customer-management.enabled
- name: weather
  repository: file://charts/weather
  version: 0.1.0
  condition: weather.enabled  
   
```

In file Values.yaml, define all the global variables and option to enable/disable the Subcharts:
   
```
### Global Values if any ###
global: {}

### Sub Charts Specific Values ###
# Order Management
order-management:
  enabled: true
  image:
    version:
# Customer Management
customer-management:
  enabled: true
  image:
    version:
# Weather
weather:
  enabled: true
  image:
    version:
   
```

Define the micro-service API endpoints as Helm variables in the file templates/_helpers.tpl in parent chart. This makes <helm-release-name> dynamic for all micro-services API endpoints:
   
```
{{/*
Define Parent Chart Global Variables (mainly API endpoints)
*/}}

{{/*
Order Management API endpoint URL.
*/}}
{{- define "global.order-management.url" -}}
{{- printf "http://%s-order-management/api" .Release.Name -}}
{{- end -}}
   
{{/*
Customer Management API endpoint URL.
*/}}
{{- define "global.customer-management.url" -}}
{{- printf "http://%s-customer-management/api" .Release.Name -}}
{{- end -}}
   
{{/*
Weather API endpoint URL.
*/}}
{{- define "global.weather.url" -}}
{{- printf "http://%s-weather/api" .Release.Name -}}
{{- end -}}

```

In order to use the defined API endpoints in each inter-dependent micro-service, first we need to declare the required variables in respective subchart in file templates/_helpers.tpl, so that the respective Subcharts are aware about the incoming variables from Parent Helm Chart:
   
```
{{/*
Declaring Parent Chart Global Variables
*/}}
{{- define "global.order-management.url" -}}
{{- end -}}
{{- define "global.customer-management.url" -}}
{{- end -}}
{{- define "global.weather.url" -}}
{{- end -}}
```

Use the defined variable and defaults to Charts local variable, so that we don???t break the individual Helm Chart and still they can be deployed separately:
   
- charts/weather/values.yaml:
  
```
  # Default values for weather and oder_management API endpoint.

env:
  ORDER_MANAGEMENT_API_ENDPOINT: "https://order-management/api"
  WEATHER_API_ENDPOINT: "https://weather/api"
```

  - charts/weather/templates/deployment.yaml:
  
```
  containers:
    - name: {{ .Chart.Name }}
      securityContext:
        {{- toYaml .Values.securityContext | nindent 12 }}
      image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
      imagePullPolicy: {{ .Values.image.pullPolicy }}
      env:
        - name: ORDER_HISTORY_API_ENDPOINT
          value: {{ include "global.order-history.url" . | default .Values.env.ORDER_HISTORY_API_ENDPOINT }}
        - name: PAYMENT_SOLUTION_API_ENDPOINT
          value: {{ include "global.payment-solution.url" . | default .Values.env.PAYMENT_SOLUTION_API_ENDPOINT }}
```

### 3.4 Labels and Annotations
   
Either labels or annotations are used to attach metadata to Kubernetes objects. The difference is that Labels can be used to select objects and to find collections of objects that satisfy certain conditions. In contrast, annotations are not used to identify and select objects. The metadata in an annotation can be small or large, structured or unstructured, and can include characters not permitted by labels.
   
Therefore, an item of metadata should be a label under the following conditions:

  - It is used by Kubernetes to identify/select this resource
  - It is useful to expose to operators for the purpose of querying the system.

For example, helm.sh/chart: NAME-VERSION is used as a label so that operators can conveniently find all of the instances of a particular chart to use.

If an item of metadata is not used for querying, it should be set as an annotation instead.

### Standard Labels

The following table defines common labels that Helm charts use. Helm itself never requires that a particular label be present. Labels that are marked REC are recommended, and should be placed onto a chart for global consistency. Those marked OPT are optional. These are idiomatic or commonly in use, but are not relied upon frequently for operational purposes.

| Name	| Status | 	Description |
|--------------------------|-----------|-------------------------------------------------------------|
|app.kubernetes.io/name |	REC | 	This should be the app name, reflecting the entire app. Usually {{ template "name" . }} is used for this. This is used by many Kubernetes manifests, and is not Helm-specific.|
|helm.sh/chart | 	REC | 	This should be the chart name and version: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}.|
|app.kubernetes.io/managed-by | 	REC | 	This should always be set to {{ .Release.Service }}. It is for finding all things managed by Helm.|
|app.kubernetes.io/instance | 	REC | 	This should be the {{ .Release.Name }}. It aids in differentiating between different instances of the same application.|
|app.kubernetes.io/version | 	OPT | 	The version of the app and can be set to {{ .Chart.AppVersion }}.|
|app.kubernetes.io/component | 	OPT | 	This is a common label for marking the different roles that pieces may play in an application. For example, |app.kubernetes.io/component: frontend.|
|app.kubernetes.io/part-of | 	OPT | 	When multiple charts or pieces of software are used together to make one application. For example, application software and a database to produce a website. This can be set to the top level application being supported.|
   
Example:
   
```
# label example
apiVersion: apps/v1
kind: StatefulSet
metadata:
  labels:
    app.kubernetes.io/name: mysql
    app.kubernetes.io/instance: mysql-abcxzy
    app.kubernetes.io/version: "5.7.21"
    app.kubernetes.io/component: database
    app.kubernetes.io/part-of: wordpress
    app.kubernetes.io/managed-by: helm
```
   
### Annotations

Annotations, like labels, are key/value maps:

```
"metadata": {
  "annotations": {
    "key1" : "value1",
    "key2" : "value2"
  }
}
```
   
The keys and the values in the map must be strings. In other words, you cannot use numeric, boolean, list or other types for either the keys or the values.

Valid annotation keys have two segments: an optional prefix and name, separated by a slash (/). The name segment is required and must be 63 characters or less, beginning and ending with an alphanumeric character ([a-z0-9A-Z]) with dashes (-), underscores (_), dots (.), and alphanumerics between. The prefix is optional. If specified, the prefix must be a DNS subdomain: a series of DNS labels separated by dots (.), not longer than 253 characters in total, followed by a slash (/).

If the prefix is omitted, the annotation Key is presumed to be private to the user. Automated system components (e.g. kube-scheduler, kube-controller-manager, kube-apiserver, kubectl, or other third-party automation) which add annotations to end-user objects must specify a prefix.

The kubernetes.io/ and k8s.io/ prefixes are reserved for Kubernetes core components.


## 4. Deploy and Test
   
### 4.1 Deploy Charts
   
Recommend using upgrade with the --install option, which makes sure to install the Chart in a new cluster or upgrade the configuration.

```
helm upgrade --install <name> <chart> \
   --wait \
   -f <path to values.yaml> \
   --set <images.my-app=gcr.io/test/my-app:...>
```
   
### 4.2 Test Charts
   

## 5. Best Practices

### 5.1 Role-Based Access Control
   
Kubernetes RBAC is a key security control to ensure that cluster users and workloads have only the access to resources required to execute their roles.
   
RBAC resources includes the following kinds of resources:

  - ServiceAccount (namespaced)
  - Role (namespaced)
  - ClusterRole
  - RoleBinding (namespaced)
  - ClusterRoleBinding
   
#### YAML Configuration

RBAC and ServiceAccount configuration should happen under separate keys. They are separate things. Splitting these two concepts out in the YAML disambiguates them and makes this clearer.

```
rbac:
  # Specifies whether RBAC resources should be created
  create: true

serviceAccount:
  # Specifies whether a ServiceAccount should be created
  create: true
  # The name of the ServiceAccount to use.
  # If not set and create is true, a name is generated using the fullname template
  name:
This structure can be extended for more complex charts that require multiple ServiceAccounts.

someComponent:
  serviceAccount:
    create: true
    name:
anotherComponent:
  serviceAccount:
    create: true
    name:
```

#### RBAC Resources Should be Created by Default

`rbac.create` should be a boolean value controlling whether RBAC resources are created. The default should be true. Users who wish to manage RBAC access controls themselves can set this value to false.
   
#### Using RBAC Resources

`serviceAccount.name` should be set to the name of the ServiceAccount to be used by access-controlled resources created by the chart. If `serviceAccount.create` is true, then a ServiceAccount with this name should be created. If the name is not set, then a name is generated using the fullname template, If `serviceAccount.create` is false, then it should not be created, but it should still be associated with the same resources so that manually-created RBAC resources created later that reference it will function correctly. If `serviceAccount.create` is false and the name is not specified, then the default ServiceAccount is used.

The following helper template should be used for the ServiceAccount.

```
{{/*
Create the name of the service account to use
*/}}
{{- define "mychart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "mychart.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
```

  ### 5.2 Others
  
  ### 5.2.1 Document your charts
    1. Comments in the yaml files
    2. README to explain how to use the charts
    3. NOTES.txt: The file is located in *template/NOTES.txt*. It can be also template with function. The content will be printed after installation and upgrade

  ### 5.2.2 Test your charts
     It is recommended to write tests after installation

  ### 5.2.3 Ensure Secrets Are Secure

  ### 5.2.4 Make your Chart Resuable by Using Template Functions
  
  ```
  apiVersion: v1
  kind: ConfigMap
  metadata:
????  name: {{ .Release.Name }}-configmap
  data:
????  environment: {{ .Values.environment | default "dev" | quote }}
????  region: {{ .Values.region | upper | quote }}  

  ```
  ### 5.2.5 Update your Deployment When ConfigMaps or Secrets change

  ```
  kind: Deployment
  spec:
????  template:
????????  metadata:
????????????  annotations:
????????????????  checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
  ...

  ```

 ### 5.2.6 Useful Commands for Debugging Helm Charts
  1. `helm lint`: The linter tool conducts a series of tests to ensure your chart is correctly formed.
  2. `helm install --dry-run --debug`: This function renders the templates and shows the resulting resource manifests. You can also check all the resources before deployment and ensure the values are set and the templating functions work as expected.
  3. `helm get manifest`: This command retrieves the manifests of the resources that are installed to the cluster. If the release is not working as expected, this should be the first command you use to find out what is running in the cluster.
  4. `helm get values`: This command is used to retrieve the release values installed to the cluster. If you have any doubts about computed or default values, this should definitely be in your toolbelt.

## Automate GitHub Page Charts * 2 days (not including example) *

## Syncing up Chart Repository using GCS * 1 day (not including example) * 

## Full NGINX example * 7 days *

  
