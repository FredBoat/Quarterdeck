#!/bin/sh
# pass the name or id of a docker container in the arguments
# returns 1 if the cache table exists, which is one of the last things the fredboat postgres image does when setting
# itself up

docker exec $1 /usr/bin/psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='fredboat_cache';"
