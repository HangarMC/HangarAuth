FROM node:16-alpine

WORKDIR frontend
ENV TERM xterm-256color

EXPOSE 3000
ENTRYPOINT ["./entrypoint.sh"]

COPY /docker/deployment/frontend/entrypoint.sh /frontend/entrypoint.sh
RUN chmod +x /frontend/entrypoint.sh

COPY /frontend/tsconfig.json /frontend/tsconfig.json
COPY /frontend/static/ /frontend/static/
COPY /frontend/nuxt.config.ts /frontend/nuxt.config.ts
COPY /frontend/locales/ /frontend/locales/
COPY /frontend/package.json /frontend/package.json
COPY /frontend/node_modules/ /frontend/node_modules/
COPY /frontend/.nuxt/ /frontend/.nuxt/
COPY /frontend/server-middleware/ /frontend/server-middleware/
