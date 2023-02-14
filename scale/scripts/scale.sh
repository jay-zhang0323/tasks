#!/bash/bin

gcloud auth activate-service-account --key-file /var/my-app/someJsonKey
gcloud container clusters resize trace-demo-cluster --quiet --node-pool default-pool --num-nodes $1 --zone us-central1-c
