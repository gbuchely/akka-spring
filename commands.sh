cd application/
gradle clean build -x test
sudo docker stop $(sudo docker ps -a -q)
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi $(sudo docker images -a | grep "akka.*"  -o) -f
sudo docker images
