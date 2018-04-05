kubectl create -f k8s-yaml/px-repl3-sc.yaml
helm install --name px-psql --set postgresUser=postgres,postgresPassword=password,persistence.storageClass=px-repl3-sc stable/postgresql
