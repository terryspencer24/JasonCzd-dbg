NM=dbg-webserver
IMG=jasoncasiday/dbg-webserver:1.0.0
docker stop $NM
docker rm $NM
docker rmi -f $(docker images -f "dangling=true" -q)
docker create --name $NM --network dbgnet --restart always --publish 80:80 $IMG
docker start $NM
#docker run -p 80:80 --name $NM -d -t $IMG
