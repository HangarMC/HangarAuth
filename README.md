# HangarAuth - Hangar's Authentication Portal

This repository contains the source code to Hangar's Authentication Portal, HangarAuth.  
It's used to provide one central login to access multiple different services, like the forums or Hangar.

## Architecture

HangarAuth makes use of [Ory Kratos](https://www.ory.sh/kratos/) for user management and [Ory Hydra](https://www.ory.sh/hydra/) to integrate other applications.

HangarAuth is a nuxt application, that provides a renderer to render the forms kratos generates. It also implements the consent flow for hydra.
It uses vuetify for the components.

## Development Setup

HangarAuth is a yarn project, so you wanna run `yarn` and then `yarn dev` to start the dev server.

Additionally, you will want to run kratos, hydra, a database and a test mail server. Those have been provided via docker compose. Checkout the docker folder.

## Creating a client


```
hydra clients create \
    --id my-client
    --name MyClient
    --endpoint http://localhost:4445 \
    --token-endpoint-auth-method none \
    --grant-types authorization_code,refresh_token \
    --response-types code \
    --scope openid,offline \
    --callbacks http://localhost:3001/redirect
```

## Contributing

All contributions are welcome, however we recommend joining #hangar-dev on the [Paper Discord](https://discord.gg/papermc) and discussing changes with us before,
in order to make sure your changes align with our vision and no redundant work is done.

## Licence

MIT
