FROM ghcr.io/graalvm/graalvm-community:23.0.1-ol8-20241015 as build

WORKDIR /app

COPY . .
COPY ./mvnw ./
#COPY ./conf.properties ./

RUN ./mvnw -P native native:compile

FROM alpine:latest

RUN apk upgrade
RUN apk add gcompat
RUN apk add libc6-compat libstdc++ libgcc

WORKDIR /app

#COPY --from=build app/conf.properties ./
COPY --from=build app/target/DemoPropertiesWatcher ./app

EXPOSE 8080

CMD ["sh", "-c", "./app || tail -f /dev/null"]
