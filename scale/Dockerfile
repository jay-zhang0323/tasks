FROM debian:latest

RUN apt-get -y update && \
    apt-get install -y apt-transport-https ca-certificates gnupg curl && \
    echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && \
    curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - && \
    apt-get update -y && apt-get install -y google-cloud-cli kubectl google-cloud-sdk-gke-gcloud-auth-plugin  && apt-get -y autoremove

COPY ./scripts/scale.sh /opt/scale.sh

COPY ./trace-demo-service-a.yaml /opt/trace-demo-service-a.yaml
