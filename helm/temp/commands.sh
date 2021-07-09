# https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo add jetstack https://charts.jetstack.io
helm repo update
#helm show values prometheus-community/kube-prometheus-stack > kps-values.yaml
# (file is edited with several changes, backed up to kps-values.yaml.orig)
# https://stackoverflow.com/questions/64343656/grafana-dashboard-not-working-with-ingress
helm upgrade --install kproms prometheus-community/kube-prometheus-stack \
    -f promcomHelmVals.yaml --atomic -n promcom

# https://docs.microsoft.com/en-us/azure/aks/ingress-tls
# https://docs.microsoft.com/en-us/azure/aks/ingress-internal-ip#create-an-ingress-controller

helm upgrade --install ingcont ingress-nginx/ingress-nginx \
    --set controller.replicaCount=2 \
    --set controller.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set defaultBackend.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set controller.admissionWebhooks.patch.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-dns-label-name"=p2team4revopsmd \
    --set controller.metrics.enabled=true \
    --set controller.metrics.serviceMonitor.enabled=true \
    --atomic -n promcom

# metrics config in lens -
# set to "Lens" first, set url to default/kube-prometheus-stack-prometheus:9090/prometheus
# then set to "Prometheus Operator"

# https://cert-manager.io/docs/installation/kubernetes/#installing-with-helm

helm upgrade --install certman jetstack/cert-manager \
  --set nodeSelector."kubernetes\.io/os"=linux \
  --set webhook.nodeSelector."kubernetes\.io/os"=linux \
  --set cainjector.nodeSelector."kubernetes\.io/os"=linux \
  --set installCRDs=true \
  --atomic -n promcom
# https://escalona-training-2105-1.westus.cloudapp.azure.com/prometheus
# https://escalona-training-2105-1.westus.cloudapp.azure.com/grafana

# adding these dashboards to grafana
# https://github.com/kubernetes/ingress-nginx/tree/master/deploy/grafana/dashboards