cd application/
gradle clean build -x test
docker stop $(docker ps -a -f "name=akkaspring_" -q)
docker rm $(docker ps -a -f "name=akkaspring_" -q)
docker rmi $(docker images "akka*" -q) -f
docker images
