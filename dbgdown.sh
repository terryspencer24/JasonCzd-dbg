#API_PORT=8080 SOC_PORT=8081 WEB_PORT=80 WEB_SSL_PORT=443 NET_IP="172.0.0.1/24" VOLUME="dbg-db-volume" docker-compose down  
docker-compose down  
docker rmi -f $(docker images -f "dangling=true" -q)
