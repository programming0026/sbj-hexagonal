FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
RUN --mount=type=bind,source=pom.xml,target=pom.xml,readonly \
    --mount=type=bind,source=src,target=src,readonly \
    --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline && \
    mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:21-alpine
ENV TZ=America/Mexico_City
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY --from=build /app/target/*.jar /workspace/app.jar
COPY --chmod=0755 ./run.sh /run.sh
ENTRYPOINT ["/run.sh"]
