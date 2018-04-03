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
user@host:~/px-jpa-rest$ PGPASSWORD=$(kubectl get secret --namespace default px-psql2-postgresql -o jsonpath="{.data.postgres-password}" | base64 --decode; echo)
user@host:~/px-jpa-rest$ kubectl run --namespace default px-psql2-postgresql-client --restart=Never --rm --tty -i --image postgres --env "PGPASSWORD=$PGPASSWORD" --command -- psql -U postgres -h px-psql2-postgresql postgres
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
