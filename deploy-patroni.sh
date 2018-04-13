ETCD_HOST_URL=10.128.0.2:2379
kubectl create -f k8s-yaml/px-repl1-sc.yaml
helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/
helm install --name px --set persistentVolume.storageClass=px-repl1-sc,Etcd.Host=$ETCD_HOST_URL,Etcd.DeployChart=false,Replicas=3 incubator/patroni
