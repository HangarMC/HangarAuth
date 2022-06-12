FROM oryd/kratos:latest
CMD ["serve", "--watch-courier", "--dev", "-c", "/etc/config/kratos/kratos.yml"]
COPY kratos.yml /etc/config/kratos/kratos.yml.orig
ADD schemas /etc/config/kratos/schemas
ADD templates /etc/config/kratos/templates
COPY hooks /etc/config/kratos/hooks
# replace api host env var in kratos.yml
ARG API_HOST
ARG BACKEND_HOST
USER root
RUN apk add -U --no-cache gettext
RUN envsubst < /etc/config/kratos/kratos.yml.orig > /etc/config/kratos/kratos.yml
USER ory
