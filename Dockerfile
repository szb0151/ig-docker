FROM ubuntu:latest

#Add ENV variables
ENV PGDATA=/var/lib/postgresql/data/pgdata
ENV PG_HOST=0.0.0.0
ENV PG_PORT=5432
ENV IG_DATABASE=image_gallery
ENV IG_USER=image_gallery
ENV IG_PASSWD=6pbs(y*TtZ|h,d*E$10qW.sb06*.#^`h
ENV IG_PASSWD_FILE=/mnt/secrets/postgres_passwd
ENV S3_IMAGE_BUCKET=edu.au.cc.ram-image-gallery-config

ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get install openjdk-11-jre -y

COPY build/libs/java-image-gallery-all.jar /app/
WORKDIR /app

CMD ["java", "-jar", "java-image-gallery-all.jar"]

