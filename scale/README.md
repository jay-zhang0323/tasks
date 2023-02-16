# Create service account and download the json key
gcloud iam service-accounts create gsa-name

## Create secret from json key
kubectl create secret generic gke-serviceaccount --from-literal "someJsonKey=$(cat /tmp/sej.json)"

# Grant the container permission to service account
gcloud projects add-iam-policy-binding sej-step05-sandbox --member "serviceAccount:gsa-name@sej-step05-sandbox.iam.gserviceaccount.com" --role=roles/container.admin

# Make the image to install the gcloud
Please check the Dockerfile

docker tag job:latest gcr.io/sej-step05-sandbox/job:latest
docker push gcr.io/sej-step05-sandbox/job:latest

# Make the job service
Please check the cronjob.yaml

# Use below command to check result
gcloud container node-pools describe default-pool --zone us-central1-c --cluster trace-demo-cluster


## Todo
gcloud components install kubectl google-cloud-sdk-gke-gcloud-auth-plugin
gcloud config set project sej-step05-sandbox
gcloud container clusters get-credentials trace-demo-cluster --region us-central1-c
