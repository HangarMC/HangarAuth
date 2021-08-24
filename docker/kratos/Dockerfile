FROM oryd/kratos:latest
CMD ["serve", "--dev", "-c", "/etc/config/kratos/kratos.yml"]
COPY kratos.yml /etc/config/kratos/kratos.yml
COPY identity.schema.json /etc/config/kratos/identity.schema.json
