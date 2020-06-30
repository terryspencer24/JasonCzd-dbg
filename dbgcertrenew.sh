#!/bin/bash
docker-compose -f docker-compose.yml -f docker-compose_newcert.yml run certbot renew \
&& docker-compose -f docker-compose.yml -f docker-compose_newcert.yml kill -s SIGHUP dbg-webserver
