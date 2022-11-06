FROM oryd/kratos:v0.10.1
CMD ["serve", "--watch-courier", "--dev", "-c", "/etc/config/kratos/kratos.yml"]
COPY kratos.yml /etc/config/kratos/kratos.yml
ADD schemas /etc/config/kratos/schemas
ADD templates /etc/config/kratos/templates
COPY hooks /etc/config/kratos/hooks
