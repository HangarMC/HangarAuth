FROM oryd/kratos:latest
CMD ["serve", "--watch-courier", "--dev", "-c", "/etc/config/kratos/kratos.yml"]
COPY kratos.yml /etc/config/kratos/kratos.yml
COPY identity.schema.json /etc/config/kratos/identity.schema.json
ADD templates /etc/config/kratos/templates
