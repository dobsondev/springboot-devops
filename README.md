# React / Spring Boot DevOps Example Project

[![Continuous Integration](https://github.com/dobsondev/springboot-devops/actions/workflows/ci.yml/badge.svg)](https://github.com/dobsondev/springboot-devops/actions/workflows/ci.yml)

This is a sample application that provides a "Task Tracker" application. The backend API is written in Java using Spring Boot which connects to a Postgres database and the frontend is a React app.

Technologies used:

- [React](https://react.dev/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Postgres](https://www.postgresql.org/)
- [Docker](https://www.docker.com/)
- [Kubernetes](https://kubernetes.io/)
- [Minikube](https://minikube.sigs.k8s.io/docs/)
- [Helm](https://helm.sh/)

## Local Development Setup

The local development build of this project leverages `docker compose` to create a quick a simple connection between the different services.

To spin up the local development environment, simply run the following command:

```bash
docker compose up [-d]
```

You can now use the following links:

1. [http://localhost:3000](http://localhost:3000) will contain the frontend React application.
2. [http://localhost:8080](http://localhost:8080) will contain the backend Spring Boot application, but you will want to go to [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks) to see a display of all the tasks in your browser.

### React Development

The React application is setup so that any changes while running `docker compose up -d` will hot reload on the site. So simply make whatever changes are needed and you will see those reflected immediately.

### Spring Boot Development

To work on the Spring Boot application, make whatever changes you need to for the application. Once that is done you will need to build it. Most likely, your local computer will have a different version of Java than that of the container, so the best course of action is to connect to the container via:

```bash
docker compose exec springboot /bin/bash
```

Once connected to the container, change directories into the `/app` directory:

```bash
cd /app
```

From the `/app` directory you can build the project using:

```bash
./gradlew build
```

From there you can exit the application, and restart the containers using the following two commands:

```bash
docker compose down -v
docker compose up -d
```

#### TODO

- Hot reloading should work for Spring Boot since we mount `./springboot` to `/app` in our `docker-compose.yml` file, but it does not appear to. Need to look into that issue more because this is not ideal for development workflow.

## Helm & Minikube Setup

To deploy this application on a Minikube cluster (local Kubernetes cluster) we can use Helm. To use this part of the setup, you must have the following already installed on your local system:

- [Docker](https://docs.docker.com/get-docker/)
- [Kubernetes / kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [Helm](https://helm.sh/docs/intro/install/)

Note: all of these commands are assumed to be run from the root directory of this project. The `helm` commands have relative paths in the command so these commands will not work as written if you are not in the project root.

Be sure to start your Minikube cluster before attempting to install the Helm releases. This can be done by running the following command:

```bash
minikube start
```

You will also need to install the `ingress` addon for Minikube to allow ingress to our backend service. More information about this process can be found at https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/. This can be done with the following command:

```bash
minikube addons enable ingress
```

You can verify the Ingress Controller pod is running with the following command:

```bash
kubectl get pods -n ingress-nginx
```

Be sure to install the Helm releases in the following order:

1. Postgres
2. Spring Boot Backend
3. React Frontend

### Postgres Release on Minikube

Postgres can be installed on the Minikube cluster so that the Spring Boot application has a database to use. Without it the application will fail to run. To install Postgres on your Minikube cluster using the following command:

```bash
helm install example-postgres-release-1 helm/postgres
```

Note `example-postgres-release-1` can be replaced with whatever release name you want to use. You can verify the pod is running by using the following command and checking that the status is set to "Running":

```bash
kubectl get pods --selector=name=postgres-pod
```

### Backend Release on Minikube

To install the Spring Boot release on your Minikube cluster, using the following command:

```bash
helm install example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml
```

Note `example-springboot-release-1` can be replaced with whatever release name you want to use. You can verify the pod is running by using the following command and checking that the status is set to "Running":

```bash
kubectl get pods --selector=name=springboot-pod
```

To see the backend service running via Minikube use:

```bash
minikube service springboot-service
```

This will open up a browser window that will show the Spring Boot application running. Note that by default this will open the Spring Boot application on a URL that doesn't have an endpoint associated with it so it will appear as though there is an error even though there is not. Navigate to `/api/tasks` and you should see JSON containing the current tasks. When the application just starts, that JSON code should look like:

```json
[
   {
      "id":1,
      "title":"Task #1",
      "description":"Description for task 1.",
      "completed":true
   },
   {
      "id":2,
      "title":"Task #2",
      "description":"Description for task 2.",
      "completed":false
   },
   {
      "id":3,
      "title":"Task #3",
      "description":"Description for task 3.",
      "completed":true
   },
   {
      "id":4,
      "title":"Task #4",
      "description":"Description for task 3.",
      "completed":false
   }
]
```

### Frontend Release on Minikube

To install the React application release on your Minikube cluster, using the following command:

```bash
helm install example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml
```

Note `example-react-app-release-1` can be replaced with whatever release name you want to use. You can verify the pods are running by using the following command and checking that the status is set to "Running":

```bash
kubectl get pods --selector=name=react-app-pod
```

To see the frontend service running via Minikube use:

```bash
minikube service react-app-service
```

This will open up a browser window that will show the React application running. Note: at this point the React application will not connect to the backend Spring Boot service. We need to start the `minikube tunnel` as described below to allow the Spring Boot and React Ingress to work.

### Minikube Tunnel

As the application stands right now, the Spring Boot and React Ingress will not work until a Minikube Tunnel is created. We need the Ingress because our React application actually calls the Spring Boot backend from our browser - so need it to be publically available to our browser.

Append `127.0.0.1 springboot.local` and `127.0.0.1 reactapp.local` to your `/etc/hosts` file on MacOS (NOTE: Do **NOT** use the Minikube IP).

Run the following command (and keep the terminal window open) to open up a Minikube tunnel to the ingress. This command may prompt you for your sudo password:

```bash
minikube tunnel
```

Go to [http://springboot.local/](http://springboot.local/) and you should see the same output as you did when running `minikube service springboot-service`. Go to [http://reactapp.local/](http://reactapp.local/) you will see the frontend React app running.

## Minikube `kubectl` Commands

After running `minikube start` your `kubectl` context will be set to use Minikube. This means any commands you run with `kubectl` should connect to Minikube.

If you ever want to just restart your Minikube cluster - that can easily be done by using `minikube delete` and then running `minikube start` again after. This will allow you to reset Minikube to a blank slate.

---

Get all pods, services, deployments and replicasets running on Minikube:

```bash
kubectl get all
```

Describe a pod, service, deployment or replicaset on the cluster:

```bash
kubectl describe [TYPE NAME_PREFIX]
```

eg: `kubectl describe deployment postgres-deployment` or `kubectl describe pod/react-app-deployment-657d6b7f4d-9ckf6`

Login to a pod to run a command (like cURL):

```bash
kubectl exec -it <pod-name> -- /bin/sh
```

eg: `kubectl exec -it pod/react-app-deployment-74986868db-8stn8 -- /bin/sh`. Once in a pod like this you could run commands such as cURL to test if your DNS are setup properly, something like `curl http://springboot-service:8080/api/tasks` will test if a React pod can connect to the Spring Boot service to get JSON responses back from the backend API.

## Continuous Integration / Continuous Delivery

### Integration

Each pull request into the `main` branch on GitHub will trigger the `./github/workflows/ci.yml` to run. This will perform the following checks:

1. Ensure we can login to DockerHub using the setup GitHub secrets
2. Ensure the React Docker image can be built successfully
3. Ensure the Spring Boot Docker image can be built successfully

#### TODO

1. Automated testing needs to be written for each application and ran through this workflow

### Delivery

There are a few ways that delivery can happen for this application currently. First, a user may manually run the "Push Docker Images to DockerHub" (`./github/workflows/cd-build-and-push.yml`) workflow via the [Actions](https://github.com/dobsondev/springboot-devops/actions/workflows/cd-build-and-push.yml) tab on the GitHub repo. This allows a user to manually push up the images using the pre-defined workflows.

There is also an automatic continuous delivery mechanism setup. If GitHub detects changes to either the `./springboot` or `./react-app` directories when merged to main then either the `./github/workflows/cd-latest-springboot-image.yml` or `./github/workflows/cd-latest-react-image.yml` will automatically trigger. Both of these workflows do the same thing as the manual "Push Docker Images to DockerHub" workflow but automatically when changes happen.

There is one more part to the continuous delivery system where if a release is published then a GitHub workflow at `./github/workflows/cd-release.yml` will be automatically triggered. This will build new Docker images for both the React application and Spring Boot application and create a new tag with the same tag as the GitHub release and push those new tags to DockerHub. This is meant to simulate an actual new release of the container. Of course in a real world scenario you would not have the React app and Spring Boot application using the same tag, but since this is just for a demo we are using the same tag.

#### TODO

1. Create CD for Kubernetes cluster deployment using Helm - maybe temporarily make a AWS EKS cluster with Terraform and deploy to it just as a test then turn it off after validating it works

## Docker

Create a push the Spring Boot image to Dockerhub:

```bash
docker build -t dobsondev/springboot-devops_springboot:v0.2 ./springboot
docker push dobsondev/springboot-devops_springboot:v0.2
```

Create a push the React image to Dockerhub:

```bash
docker build -t dobsondev/springboot-devops_react:v0.2 ./react-app
docker push dobsondev/springboot-devops_react:v0.2
```

## Helm

If you have already installed a release to the Minikube cluster via Helm then trying to re-install will produce the following error:

```bash
Error: INSTALLATION FAILED: cannot re-use a name that is still in use
```

In this case you will either need to use the `upgrade` command or `uninstall` command. The `upgrade` command will allow you to upgrade your deployment so that you can rollback and should be used in most real world cases. The `uninstall` command will remove the release entirely at which point you can then re-install the release.

### Update a Helm Release

To update a Helm release use the following command:

```bash
helm upgrade [release-name] [chart-directory] [-f [environment-yaml]]
```

For example, to upgrade the Spring Boot application Helm release use:

```bash
helm upgrade example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml
```

As another example, to upgrade the React application Helm release, run the following command:

```bash
helm upgrade example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml
```

### Uninstall a Helm Release

To uninstall a Helm release use the following command:

```bash
helm uninstall [release-name]
```

For example, to unistall the React application Helm release, run the following command:

```bash
helm uninstall example-react-app-release-1
```

As another example, to uninstall the Spring Boot application release use:

```bash
helm uninstall example-springboot-release-1
```

### Overwrite a Helm Value

To overwrite a Helm value, simple add `--set valueName=valueValue` to your command line commands. For example, to overwrite the tag for the Spring Boot image when installing the Helm release, use the following:

```bash
helm install example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml --set imageTag=v0.3-alpha-3
```

If you wanted to overwrite the tag for a Spring Boot release upgrade, you could use the following:

```bash
helm upgrade example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml --set imageTag=v0.3-alpha-3
```

To overwrite the tag for the React image when installing the Helm release, use the following:

```bash
helm install example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml --set imageTag=v0.3-alpha-3
```

If you wanted to overwrite the tag for a React release upgrade, you could use the following:

```bash
helm upgrade example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml --set imageTag=v0.3-alpha-3
```

## Postgres

### Connect to Postgres on Local Development Environment

To connect to the Postgres command line tool while working on your local development environmment (using `docker compose`), run the following command:

```bash
docker compose exec postgres psql -h localhost -U compose-postgres
```

### Connect to Postgres on Minikube Cluster

To access the Postgres database in the Minikube cluster, use the following commands:

```bash
kubectl get pods --selector=name=postgres-pod
```

Note the Pod name and add it into the following command:

```bash
kubectl exec -it pod/[postgres-pod-name] -- psql -h localhost -U postgres
```

From there you can use Postgres commands as described in the section later in this README. Just as an example of a common set of commands you will want to run - to connect to the `postgres` database and then list the contents of the `tasks` table use the following two commands:

```sql
\c postgres
TABLE tasks;
```

### Helpful Postgres Commands

Once connected to Postgres, you can then show all the databases table by using:

```sql
\l
```

You can select a database table to use by using:

```sql
\c [database-table-name]
```

In our case, that would be:

```sql
\c compose-postgres
```

From there you can describe the tables of the database by using:

```sql
\dt
```

If you want to then describe a particlar table, you can use:

```sql
\d [table-name]
```

In the case of this application, you would want to use:

```sql
\d tasks
```

If you want to see the contents of the `tasks` table you can use:

```sql
TABLE tasks;
```

Finally, when you are ready to quit the Postgres CLI, you can use:

```sql
\q
```