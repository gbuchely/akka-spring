cd application/
gradle clean build -x test
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi akkaspring_consumer1 akkaspring_worker1 akkaspring_worker2 akkaspring_publisher1 -f
docker images
