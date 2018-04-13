kubectl create -f k8s-yaml/px-repl1-sc.yaml
helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/
helm install --name px -f k8s-yaml/patroni-values.yaml incubator/patroni
