getRunningPod() {
  oc get pods -o json | \
    jq -r ".items[] | select(.metadata.name | contains(\"$1\")) | select((.metadata.name | contains(\"build\") or contains(\"deploy\")) | not) | select(.status.phase == \"Running\") | select(.status.containerStatuses[].ready == true) | .metadata.name"
}

waitForRunningPod() {
  x=1
  while [ -z $(getRunningPod $1) ]; do
    echo "Waiting for $1 to be deployed"
    sleep 30
    if [ $x -gt 25 ]; then
      exit $x
    fi
    x=$(( $x + 1 ))
  done
}

waitForSampleAppStart() {
  while [[ ! $(oc logs "$(oc get pods --no-headers=true --selector app=$1 -o custom-columns=NAME:.metadata.name)") == *"Created table"* ]]; do
    echo "Waiting for Sample app to start"
  done
}

SERVER=sample-app-server
NAMESPACE=${1:-"sample-app"}
BRANCH=${2:-"master"}
POSTGRES_LIST="openshift/postgres.yaml"
SAMPLE_APP_TEMPLATE="openshift/sampleApp.yaml"

oc new-project $NAMESPACE

# Just to make sure we really are on the correct project
oc project $NAMESPACE

# Creates resources necessary for postgres
oc create -f ${POSTGRES_LIST}

# Wait until database pod is Running and has exactly 1 out of 1 pod ready
waitForRunningPod sample-app-db

DB_SVC_IP=$(oc get svc sample-app-db -o jsonpath='{.spec.clusterIP}')

oc process -f ${SAMPLE_APP_TEMPLATE} -p DB_SERVICE_IP=${DB_SVC_IP} -p APP_NAME=${SERVER} -p SOURCE_CODE_BRANCH=${BRANCH} | oc create -f -

waitForRunningPod ${SERVER}

waitForSampleAppStart ${SERVER}

ROUTE=$(oc get route --no-headers=true --selector app=${SERVER} -o custom-columns=NAME:.spec.host)

./test.sh ${ROUTE}

# TODO check after deployment (of list and template) if all resources were created/succeeded
# TODO measure how long it took to deploy the whole app/parts of it (get info from deployment pods?)

echo "Done"
