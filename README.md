# RSO: katalog destinacij microservice

## Prerequisites

```bash
docker run -d --name pg-katalog-destinacij -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=katalog-destinacij -p 5432:5432 postgres:13
```

## Build and run commands
```bash
mvn clean package
cd api/target
java -jar katalog-destinacij-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8080/v1/images

## Docker commands
```bash
docker build -t katalog .   
docker images
docker run katalog    
docker tag katalog rso/katalog   
docker push rso/katalog  
```
```bash
docker network ls  
docker network rm rso
docker network create rso
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 --network rso postgres:13
docker inspect pg-image-metadata
docker run -p 8080:8080 --network rso -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://pg-image-metadata:5432/image-metadata rso/katalog
```

## Kubernetes
```bash
kubectl version
kubectl --help
kubectl get nodes
kubectl create -f katalog-deployment.yaml 
kubectl apply -f katalog-deployment.yaml 
kubectl get services 
kubectl get deployments
kubectl get pods
kubectl logs katalog-deployment-6f59c5d96c-rjz46
kubectl delete pod katalog-deployment-6f59c5d96c-rjz46
```

Kubernetes secrets configuration: https://kubernetes.io/docs/tasks/configmap-secret/managing-secret-using-kubectl/

```bash
kubectl create secret generic pg-pass --from-literal=password=mypassword
kubectl get secrets
kubectl describe secret pg-pass
```


