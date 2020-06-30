API_PORT=9080 SOC_PORT=9081 WEB_PORT=90 NET_IP="173.0.0.1/24" VOLUME=dbg-test-db-volume docker-compose -p dbg-test --project-directory .. down
#docker rmi -f $(docker images -f "dangling=true" -q)
