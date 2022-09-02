This directory contains the helm chart that is used to deploy this app.

```
minikube image build -t hangarauth-frontend:2 . -f docker/deployment/frontend/Dockerfile
```
```
helm upgrade --install hangarauth chart/ --set backend.image.repository=hangarauth-backend --set backend.image.tag=4 --set frontend.image.repository=hangarauth-frontend --set frontend.image.tag=2
```
