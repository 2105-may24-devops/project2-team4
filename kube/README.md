# Kubernetes manifest files

## Initial Setup
The manifests in the `kube/dependencies` folder need to be applied before the manifests in the `kube/microsvc`
The `kube/microsvc/configs` folder contains manifests for environment variables needed by the containers.

Kubernetes has two ways of finding the service. According to the [Docs](https://kubernetes.io/docs/concepts/services-networking/connect-applications-service/#accessing-the-service), you can use environment variables, and you can use the DNS, but DNS is an add-on (it's there in AKS). The DNS method is used in these manifests. According to [this link](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/#namespaces-of-services), you can resolve the name of a service to its clusterIP with the name matching this pattern: `[svc.name].[namespace]`.

The secret files will need to be created using the `kubectl create secret` command.

## TODO List
- Some of the deployments need to become stateful sets (flashcard, kafka, zookeeper, maybe consul)
- Optional: Use Ingress to replace the gateway microsvc
- Optional: Add security policy stuff