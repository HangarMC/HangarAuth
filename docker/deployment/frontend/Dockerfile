FROM node:16-alpine

WORKDIR frontend
ENV TERM xterm-256color

EXPOSE 3000
ENTRYPOINT ["./entrypoint.sh"]

COPY /docker/deployment/frontend/entrypoint.sh /frontend/entrypoint.sh
RUN chmod +x /frontend/entrypoint.sh

COPY /frontend-old/tsconfig.json /frontend/tsconfig.json
COPY /frontend-old/static/ /frontend/static/
COPY /frontend-old/nuxt.config.ts /frontend/nuxt.config.ts
COPY /frontend-old/locales/ /frontend/locales/
COPY /frontend-old/package.json /frontend/package.json
COPY /frontend-old/node_modules/ /frontend/node_modules/
COPY /frontend-old/.nuxt/ /frontend/.nuxt/
COPY /frontend-old/server-middleware/ /frontend/server-middleware/
