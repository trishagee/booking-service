FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LC_ALL=C.UTF-8

RUN apt-get update && apt-get install -y apt-utils apt-transport-https

RUN apt-get install -y \
  # Utilities \
  curl unzip wget software-properties-common socat man-db gnupg2 pass lsof \
  # VCS \
  git \
  # JVM \
  openjdk-8-jre-headless openjdk-11-jdk-headless openjdk-16-jdk-headless openjdk-17-jdk-headless maven \
  # Docker
  docker docker-compose \
  && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-16-openjdk-amd64
