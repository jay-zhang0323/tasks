#!/bin/bash

gcloud auth activate-service-account --key-file /var/my-app/someJsonKey
gcloud config set project sej-step05-sandbox
gcloud container clusters get-credentials trace-demo-cluster --region us-central1-c
gcloud container clusters resize trace-demo-cluster --quiet --node-pool default-pool --num-nodes $1 --zone us-central1-c
kubectl apply -f /opt/trace-demo-service-a.yaml
