-- sudo docker network create --driver bridge isolated_nw
-- sudo docker run -d -p 2379:2379 --network=isolated_nw -e ETCD_LISTEN_CLIENT_URLS='http://0.0.0.0:2379' -e ETCD_ADVERTISE_CLIENT_URLS='http://localhost:2379' --name=etcd quay.io/coreos/etcd
-- sudo docker run -p 8180:8080 --network=isolated_nw -e SPRING_PROFILES_ACTIVE='consumer' -e WEB_ENVIRONMENT='true' --name=consumer1 nodeetcd
-- sudo docker run --network=isolated_nw -e SPRING_PROFILES_ACTIVE='worker' -e WEB_ENVIRONMENT='false' --name=worker1 nodeetcd
-- sudo docker run -p 8280:8080 --network=isolated_nw -e SPRING_PROFILES_ACTIVE='publisher' -e WEB_ENVIRONMENT='true' --name=publisher1 nodeetcd

-- sudo docker run -p 8180:8080 --network=isolated_nw2 -e SPRING_PROFILES_ACTIVE='consumer' -e WEB_ENVIRONMENT='true' --name=consumer2 noderedis

