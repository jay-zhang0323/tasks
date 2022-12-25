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

## Charts Structure

### Helm Values

### Helm Subcharts

### Helm Templates

## Labels and Annotations

## Role-Based Access Control



