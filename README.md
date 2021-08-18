# Check App for OpenShift health monitoring

This project uses Quarkus and Camel to run 4 simple routes to create database table, create new entry, get an entry and drop the database table.

## Running the application in dev mode

You can run the application in dev mode that enables live coding.

For that, you can use the `dev-mode.sh` script (docker is required).

## Running the application in OpenShift

You can run the application in OpenShift using the `openshift-deploy.sh`. This script assumes that you are already logged in to the cluster. You can provide a namespace name as an argument for the script, otherwise `check-app` will be used.
