cd application/
gradle clean build -x test
sudo docker stop $(sudo docker ps -a -q)
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi akkaspring_consumer akkaspring_worker akkaspring_workerb akkaspring_processor akkaspring_publisher -f
sudo docker images
