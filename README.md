# React / Spring Boot DevOps Example Project

This is a sample application that provides a "Task Tracker" application. The backend API is written in Java using Spring Boot which connects to a Postgres database and the frontend is a React app.

Technologies used:

- [React](https://react.dev/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Postgres](https://www.postgresql.org/)
- [Docker](https://www.docker.com/)
- [Kubernetes](https://kubernetes.io/)
- [Minikube](https://minikube.sigs.k8s.io/docs/)
- [Helm](https://helm.sh/)

## Local Setup

To setup on your local, first run:

```
docker compose build
```

This will build the Docker image for the Spring Boot application which has a custom Dockerfile located at `springboot/Dockerfile`.

Once that is done ensure that all node dependencies are installed by running:

```
docker compose run react-app npm install
```

By keeping this step outside of the `docker-compose.yml` file you save a lot of start-up time that would be spend running the install command each time.

Finally - run the following command to bring up the project locally:

```
docker compose up [-d]
```

You can now use the following links:

1. [http://localhost:3000](http://localhost:3000) will contain the frontend React application.
2. [http://localhost:8080](http://localhost:8080) will contain the backend Spring Boot application, but you will want to go to [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks) to see a display of all the tasks in your browser.

## Helm & Minikube Setup

To deploy this application on a Minikube cluster (local Kubernetes cluster) we can use Helm. To use this part of the setup, you must have the following already installed on your local system:

- [Docker](https://docs.docker.com/get-docker/)
- [Kubernetes / kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [Helm](https://helm.sh/docs/intro/install/)

Be sure to start your Minikube cluster before attempting to install the Helm releases. This can be done by running `minikube start`.

Be sure to install the Helm releases in the following order:

1. Postgres
2. Spring Boot Backend
3. React Frontend

### Postgres Release on Minikube

Postgres can be installed on the Minikube cluster so that the Spring Boot application has a database to use. Without it the application will fail to run. To install Postgres on your Minikube cluster using the following command:

```
helm install example-postgres-release-1 helm/postgres
```

Note `example-postgres-release-1` can be replaced with whatever release name you want to use.

### Backend Release on Minikube

To install the Spring Boot release on your Minikube cluster, using the following command:

```
helm install -f helm/environments/minikube.yaml example-springboot-release-1 helm/springboot
```

Note `example-springboot-release-1` can be replaced with whatever release name you want to use.

To see the backend running via Minikube use:

```
minikube service springboot-service
```

This will open up a browser window that will show the Spring Boot application running. Navigate to `/api/tasks` and you should see JSON containing the current tasks. When the application just starts, that JSON code should look like:

```
[
   {
      "id":1,
      "title":"Task #1",
      "description":"Description for task 1.",
      "reminder":true
   },
   {
      "id":2,
      "title":"Task #2",
      "description":"Description for task 2.",
      "reminder":true
   },
   {
      "id":3,
      "title":"Task #3",
      "description":"Description for task 3.",
      "reminder":true
   }
]
```

### Frontend Release on Minikube

```
helm install -f helm/environments/minikube.yaml example-react-app-release-1 helm/react-app
```

Note `example-react-app-release-1` can be replaced with whatever release name you want to use.

To see the frontend running via Minikube use:

```
minikube service react-app-service
```

This will open up a browser window that will show the React application running.

### Extra Tips/Tricks

If you have already installed via Helm then trying to re-install will produce the following error:

```
Error: INSTALLATION FAILED: cannot re-use a name that is still in use
```

In this case you will either need to use the `upgrade` command or `uninstall` command. The `upgrade` command will allow you to upgrade your deployment so that you can rollback and should be used in most real world cases. The `uninstall` command will remove the release entirely at which point you can then re-install the release.

To upgrade a release using helm use the `upgrade` commmand. An example:

```
helm upgrade example-springboot-release-1 helm/springboot -f helm/springboot/environments/minikube.yaml
```

Alternatively, you can uninstall a deployment using the `uninstall` command. An example looks like:

```
helm uninstall example-postgres-release-1
```

## Minikube `kubectl` Commands

After running `minikube start` your `kubectl` context will be set to use Minikube. This means any commands you run with `kubectl` should connect to Minikube.

If you ever want to just restart your Minikube cluster - that can easily be done by using `minikube delete` and then running `minikube start` again after. This will allow you to reset Minikube to a blank slate.

---

Get all pods, services, deployments and replicasets running on Minikube:

```
kubectl get all
```

Describe a pod, service, deployment or replicaset on the cluster:

```
kubectl describe [TYPE NAME_PREFIX]
```

eg: `kubectl describe deployment postgres-deployment` or `kubectl describe pod/react-app-deployment-657d6b7f4d-9ckf6`

Login to a pod to run a command (like cURL):

```
kubectl exec -it <pod-name> -- /bin/sh
```

eg: `kubectl exec -it pod/react-app-deployment-74986868db-8stn8 -- /bin/sh`. Once in a pod like this you could run commands such as cURL to test if your DNS are setup properly, something like `curl http://springboot-service:8080/api/tasks` will test if a React pod can connect to the Spring Boot service to get JSON responses back from the backend API.

## Postgres Commands

I am not as familiar with using Postgres as I am other database tools, so I wanted to create a list of Postgres commands here for my own reference.

To connect to the Postgres command line tool while working on your local, run the following command:

```
docker compose exec postgres psql -h localhost -U compose-postgres
```

You can then show all the databases table by using:

```
\l
```

You can select a database table to use by using:

```
\c [database-table-name]
```

In our case, that would be:

```
\c compose-postgres
```

From there you can describe the tables of the database by using:

```
\dt
```

If you want to then describe a particlar table, you can use:

```
\d [table-name]
```

In the case of this application, you would want to use:

```
\d tasks
```

If you want to see the contents of the `tasks` table you can use:

```
TABLE tasks;
```

Finally, when you are ready to quit the Postgres CLI, you can use:

```
\q
```