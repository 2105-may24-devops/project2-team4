az group create -g 2105-may24-devops-nick-aks-rg -l westus
az aks create \
    --resource-group 2105-may24-devops-nick-aks-rg \
    --name 2105-training-aks \
    --node-count 2 \
    --node-vm-size Standard_DS2_v2 \
    --generate-ssh-keys

# https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
#helm show values prometheus-community/kube-prometheus-stack > kps-values.yaml
# (file is edited with several changes, backed up to kps-values.yaml.orig)
# https://stackoverflow.com/questions/64343656/grafana-dashboard-not-working-with-ingress
helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
    -f kps-values.yaml

# https://docs.microsoft.com/en-us/azure/aks/ingress-tls
# https://docs.microsoft.com/en-us/azure/aks/ingress-internal-ip#create-an-ingress-controller
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx \
    --set controller.replicaCount=2 \
    --set controller.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set defaultBackend.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set controller.admissionWebhooks.patch.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-dns-label-name"=escalona-training-2105-1 \
    --set controller.metrics.enabled=true \
    --set controller.metrics.serviceMonitor.enabled=true

# metrics config in lens -
# set to "Lens" first, set url to default/kube-prometheus-stack-prometheus:9090/prometheus
# then set to "Prometheus Operator"

# https://cert-manager.io/docs/installation/kubernetes/#installing-with-helm
helm repo add jetstack https://charts.jetstack.io
helm repo update
helm install cert-manager jetstack/cert-manager \
  --set nodeSelector."kubernetes\.io/os"=linux \
  --set webhook.nodeSelector."kubernetes\.io/os"=linux \
  --set cainjector.nodeSelector."kubernetes\.io/os"=linux \
  --set installCRDs=true

# https://escalona-training-2105-1.westus.cloudapp.azure.com/prometheus
# https://escalona-training-2105-1.westus.cloudapp.azure.com/grafana

# adding these dashboards to grafana
# https://github.com/kubernetes/ingress-nginx/tree/master/deploy/grafana/dashboards