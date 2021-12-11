# Sample App for OpenShift health monitoring

This project is a simple `Todo App` written in Java using Quarkus and Camel. There are 5 REST API endpoins:

- /add - `POST` method used to add a new task
- /get - `GET` method used to retrieve all tasks (in JSON format)
- /get/{id} - `GET` method used to retrieve single task (in JSON format)
- /delete/{id} - `DELETE` method used to delete a single task
- /update/{id} - `PUT` method used to update a task

## Running the application in dev mode

You can run the application in dev mode that enables live coding.

For that, you can use the `dev-mode.sh` script (docker is required).

## Running the application in OpenShift

You can run the application in OpenShift using the `openshift-deploy.sh`. This script assumes that you are already logged in to the cluster. You can provide a namespace name as an argument for the script, otherwise `sample-app` will be used.

The source code to build the application on OpenShift is always taken from the GitHub repository. This means that local changes will have no effect on OpenShift deployment when using the script. When testing new changes, you can push them to a new branch on GitHub and specify the branch name as a second parameter of the OpenShift deploy script.

The `openshift-deploy.sh` deploys resources from files located in `openshift` directory in this repository. The `postgres.yaml` is a list of resources containing DC, service and PVC for a postgres instance. The `sampleApp.yaml` is a template containing IS for s2i build of this app, BC, DC, service, route and another IS for the app itself. 
