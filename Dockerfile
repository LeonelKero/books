FROM ubuntu:latest
LABEL authors="user"
LABEL maintainer="waboleonel@gmail.com"

ENTRYPOINT ["top", "-b"]