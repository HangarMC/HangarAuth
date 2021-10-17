FROM node:12-alpine

WORKDIR frontend
ENV TERM xterm-256color

EXPOSE 3000
ENTRYPOINT ["./entrypoint.sh"]

COPY /docker/deployment/frontend/entrypoint.sh /frontend/entrypoint.sh
RUN chmod +x /frontend/entrypoint.sh

COPY /tsconfig.json /frontend/tsconfig.json
COPY /static/ /frontend/static/
COPY /nuxt.config.ts /frontend/nuxt.config.ts
COPY /locales/ /frontend/locales/
COPY /package.json /frontend/package.json
COPY /node_modules/ /frontend/node_modules/
COPY /.nuxt/ /frontend/.nuxt/
COPY /server-middleware/ /frontend/server-middleware/
