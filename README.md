### Build
```console
user@host:~/px-jpa-rest$ ./mvnw clean package
user@host:~/px-jpa-rest$ ./mvnw install dockerfile:build
user@host:~/px-jpa-rest$ docker push fmrtl73/px-jpa-rest
```

### Test locally with with curl

```
docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d -p 5432:5432 postgres
```

Start the client container and create the people database
```
docker run -it --rm --link some-postgres:postgres postgres psql -h postgres -U postgres
```

```
create database people;
```

Run the rest api and create a record

```
java -jar target/px-jpa-rest-0.1.0.jar --spring.profiles.active=dev
```

```
curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://localhost:8080/people
```

```
curl http://localhost:8080/people
```

### Test with Kubernetes

Create a portworx storage class with repl3
```
kubectl create -f k8s-yaml/px-repl3-sc.yaml
```

Use helm to deploy Postgres and pass in the storage class name

```
helm install --name px-psql --set postgresUser=postgres,postgresPassword=password,persistence.storageClass=px-repl3-sc stable/postgresql
```

Create the people database
```
PGPASSWORD=$(kubectl get secret --namespace default px-psql2-postgresql -o jsonpath="{.data.postgres-password}" | base64 --decode; echo)

kubectl run --namespace default px-psql2-postgresql-client --restart=Never --rm --tty -i --image postgres --env "PGPASSWORD=$PGPASSWORD" --command -- psql -U postgres -h px-psql2-postgresql postgres
```
From psql create the people database: `create database people;`

Exit the shell `\q`.

Deploy rest api

```
kubectl create -f k8s-yaml/jpa-deploy.yaml
```

Get the svc IP

```
PSQL_SERVICE_IP=`kubectl get svc | grep jpa-rest-api | awk '{print $3}'`
```

Create a record using curl

```
curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://$PSQL_SERVICE_IP:8080/people
```
