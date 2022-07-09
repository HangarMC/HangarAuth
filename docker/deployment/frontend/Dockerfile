FROM node:16-alpine

WORKDIR frontend
ENV TERM xterm-256color

EXPOSE 3000
ENTRYPOINT ["./entrypoint.sh"]

COPY /docker/deployment/frontend/entrypoint.sh /frontend/entrypoint.sh
RUN chmod +x /frontend/entrypoint.sh

COPY /frontend/.output /frontend/
