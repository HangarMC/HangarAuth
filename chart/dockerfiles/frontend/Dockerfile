FROM node:16-alpine

USER node
WORKDIR frontend
ENV TERM xterm-256color
ENV HOST 0.0.0.0

EXPOSE 3000
ENTRYPOINT ["./entrypoint.sh"]

COPY --chown=node:node --chmod=744 /chart/dockerfiles/frontend/entrypoint.sh /frontend/entrypoint.sh
COPY --chown=node:node /frontend/.output /frontend/
