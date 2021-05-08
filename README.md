# PaperAuth - Paper's Authentication Portal

This repository contains the source code to Paper's Authentication Portal, PaperAuth.  
It's used to provide one central login to access all PaperMC services, like the forums or Hangar.

## Architecture

PaperAuth is a Spring Boot Application, with a Nuxt/Vue Frontend.

The backend utilizes JDBI for database connections and flyaway for database migrations.

The frontend utilizes nuxt static side generation (SSG), in order to produce serveable static html files. It uses vuetify for UI components.

## Development Setup

You wanna run `yarn run dev` in frontend, so start the frontend server (uses ports 3001) and you wanna start the spring application via intellij (port is 8081).
You can then access the application under the frontend port 3001 and everything gets proxied to the backend properly.

If you wanna test the SSG, run `mvn install` and access the application from the backend port 8081.

## Contributing

All contributions are welcome, however we recommend joining #hangar-dev on the [Paper Discord](https://discord.gg/papermc) and discussing changes with us before,
in order to make sure your changes align with our vision and no redundant work is done.

## Licence

MIT
