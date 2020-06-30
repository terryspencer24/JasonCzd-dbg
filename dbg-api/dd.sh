NM=hello
IMG=jasoncasiday/gs-spring-boot:1.0.0
docker stop $NM
docker rm $NM
docker rmi -f $(docker images -f "dangling=true" -q)
docker create --name $NM --network dbgnet --restart always --publish 127.0.0.1:8080:8080 $IMG
docker start $NM
#docker run -p 8080:8080 --name $NM -d -t $IMG

