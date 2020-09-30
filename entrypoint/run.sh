#!/bin/bash

set -euxo pipefail

cd /app

# wait for database to be ready
touch ~/.pgpass
chmod 0600 ~/.pgpass

until PGPASSWORD=$DB_PASSWORD psql -w -h "$DB_HOST" -U "$DB_USER" "$DB_NAME" -c '\l'; do
  echo "Postgres isn't ready yet..." >&2
  sleep 1
done
echo "Postgres ready, continuing" >&2

# migrate database
su -c "/env/bin/python spongeauth/manage.py migrate" spongeauth

set +euxo pipefail

# run worker - necessary for background sso syncs
./entrypoint/run_worker.sh &

# run
while true; do
	su -c "/env/bin/python spongeauth/manage.py runserver 0.0.0.0:8000" spongeauth
done
