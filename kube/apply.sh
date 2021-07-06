#! /bin/bash
cd dependencies
kubectl apply -f zookeeper_manifest.yml -n mdtest
kubectl apply -f kafka_manifest.yml -n mdtest
kubectl apply -f consul_manifest.yml -n mdtest
echo -e "############# done deploying dependencies\n"

cd ../configs
kubectl apply -f config_manifest.yml -n mdtest
echo -e "############# done deploying config\n"

# cd ../volumes
# kubectl apply -f fcdbpvc_manifest.yml -n mdtest
# echo -e "############# done deploying pvc\n"

cd ../microsvc
kubectl apply -f gateway_manifest.yml -n mdtest
kubectl apply -f flashcard_manifest.yml -n mdtest
kubectl apply -f quiz_manifest.yml -n mdtest
echo -e "############# done deploying microsvc\n"
