# React / Spring Boot DevOps Example

This is a sample application that provides a "Task Tracker" application. The backend API is written in Java using Spring Boot which connects to a Postgres database and the frontend is a React app.

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

## Helm

### Postgres Release on Minikube

```
helm install example-postgres-release-1 helm/postgres
```

### Backend Release on Minikube

```
helm install -f helm/springboot/environments/minikube.yaml example-springboot-release-1 helm/springboot
```

To see the backend running via Minikube use:

```
minikube service springboot-service
```

### Frontend Release on Minikube

```
helm install -f helm/react-app/environments/minikube.yaml example-react-app-release-1 helm/react-app
```

To see the frontend running via Minikube use:

```
minikube service react-app-service
```

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