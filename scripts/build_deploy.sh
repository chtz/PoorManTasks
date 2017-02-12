#!/bin/bash
mvn package docker:build
docker push dockerchtz/pmt:latest
ssh root@pmsl '(docker rm -f pmt; true) && docker rmi dockerchtz/pmt && docker run -p 9090:9090 --name pmt -d dockerchtz/pmt'
