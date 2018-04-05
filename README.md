### Build
```console
user@host:~/px-jpa-rest$ ./mvnw clean package
user@host:~/px-jpa-rest$ ./mvnw install dockerfile:build
user@host:~/px-jpa-rest$ docker push fmrtl73/px-jpa-rest
```

### Test locally with with curl

```console
user@host:~/px-jpa-rest$ docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d -p 5432:5432 postgres
```

Start the client container and create the people database
```console
user@host:~/px-jpa-rest$ docker run -it --rm --link some-postgres:postgres postgres psql -h postgres -U postgres
create database people;
\q
```

Run the rest api and create a record

```console
user@host:~/px-jpa-rest$ java -jar target/px-jpa-rest-0.1.0.jar --spring.profiles.active=dev
```

```console
user@host:~/px-jpa-rest$ curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://localhost:8080/people
user@host:~/px-jpa-rest$ curl http://localhost:8080/people
```

### Test with Kubernetes

Create a portworx storage class with repl3 and use helm to deploy Postgres and pass in the storage class name

```console
user@host:~/px-jpa-rest$ kubectl create -f k8s-yaml/px-repl3-sc.yaml
user@host:~/px-jpa-rest$ helm install --name px-psql --set postgresUser=postgres,postgresPassword=password,persistence.storageClass=px-repl3-sc stable/postgresql
```

Create the people database
```console
user@host:~/px-jpa-rest$ PGPASSWORD=$(kubectl get secret --namespace default px-psql-postgresql -o jsonpath="{.data.postgres-password}" | base64 --decode; echo)
user@host:~/px-jpa-rest$ kubectl run --namespace default px-psql-postgresql-client --restart=Never --rm --tty -i --image postgres --env "PGPASSWORD=$PGPASSWORD" --command -- psql -U postgres -h px-psql2-postgresql postgres
create database people;
\q
```
Deploy rest api

```console
user@host:~/px-jpa-rest$ kubectl create -f k8s-yaml/jpa-deploy.yaml
```

Get the svc IP and curl some data

```console
user@host:~/px-jpa-rest$ PSQL_SERVICE_IP=`kubectl get svc | grep jpa-rest-api | awk '{print $3}'`
user@host:~/px-jpa-rest$ curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://$PSQL_SERVICE_IP:8080/people
user@host:~/px-jpa-rest$ curl http://$PSQL_SERVICE_IP:8080/people
```

### Test with Kubernetes and Statefulset

Find out and set your ETCD_HOST_URL, you can also have the patroni chart deploy etcd for you by removing the Etcd.Host=$ETCD_HOST_URL and Etcd.DeployChart=false settings in the helm command below. There is no way to set the storage class for the Patroni deployed ETCD however, which is why pointing to an existing ETCD may be required.

```console
user@host:~/px-jpa-rest$ ETCD_HOST_URL=192.168.56.70:2379
```
Create a repliced deployment of Postgres using Patroni helm chart
```console
user@host:~/px-jpa-rest$ kubectl create -f k8s-yaml/px-repl2-sc.yaml
user@host:~/px-jpa-rest$ helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/
user@host:~/px-jpa-rest$ helm install --name px --set persistentVolume.storageClass=px-repl2-sc,Etcd.Host=$ETCD_HOST_URL,Etcd.DeployChart=false,Replicas=3 incubator/patroni
```
Deploy the rest api
```console
user@host:~/px-jpa-rest$ kubectl create -f k8s-yaml/jpa-deploy-patroni.yaml
```

Get the svc IP and curl some data

```console
user@host:~/px-jpa-rest$ PSQL_SERVICE_IP=`kubectl get svc | grep jpa-rest-api | awk '{print $3}'`
user@host:~/px-jpa-rest$ curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://$PSQL_SERVICE_IP:8080/people
user@host:~/px-jpa-rest$ curl http://$PSQL_SERVICE_IP:8080/people
```
