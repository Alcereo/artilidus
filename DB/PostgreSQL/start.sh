#!/usr/bin/env bash

docker run --name artilidus-postgres -e POSTGRES_PASSWORD=1234 -p 5432:5432 -d postgres