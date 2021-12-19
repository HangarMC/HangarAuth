# HangarAuth - Hangar's Authentication Portal

This repository contains the source code to Hangar's Authentication Portal, HangarAuth.  
It's used to provide one central login to access multiple different services, like the forums or Hangar.

## Architecture

HangarAuth makes use of [Ory Kratos](https://www.ory.sh/kratos/) for user management and [Ory Hydra](https://www.ory.sh/hydra/) to integrate other applications.

HangarAuth is a nuxt application, that provides a renderer to render the forms kratos generates. It also implements the consent flow for hydra.
It uses vuetify for the components.

## Development Setup

HangarAuth is a yarn project, so you wanna run `yarn` and then `yarn dev` inside the `frontend` folder to start the dev server.

Additionally, you will want to run kratos, hydra, a database and a test mail server. Those have been provided via docker compose. Checkout the docker folder.

## Creating a client

Run the following command inside the `hydra` container to create the hydra client.

```
hydra clients create \
    --id my-client
    --name MyClient
    --endpoint http://localhost:4445 \
    --token-endpoint-auth-method none \
    --grant-types authorization_code,refresh_token \
    --response-types code \
    --scope offline_access,openid,offline,email,profile \
    --callbacks http://localhost:3000/login
```

## Client for deployment with logout
| id | client\_name | redirect\_uris | grant\_types                            | response\_types | scope | frontchannel\_logout\_uri | frontchannel\_logout\_session\_required | post\_logout\_redirect\_uris |
| :--- | :--- | :--- |:----------------------------------------|:----------------| :--- | :--- | :--- | :--- |
| hangar | Hangar | https://hangar.benndorf.dev/login | authorization\_code%pipe%refresh\_token | code | offline\_access openid offline email profile | https://hangar-auth.benndorf.dev/oauth/frontchannel-logout | false | https://hangar.benndorf.dev/handle-logout |


## Contributing

All contributions are welcome, however we recommend joining #development on the [Hangar Discord](https://discord.gg/zvrAEbvJ4a) and discussing changes with us before,
in order to make sure your changes align with our vision and no redundant work is done.

## Licence

MIT
