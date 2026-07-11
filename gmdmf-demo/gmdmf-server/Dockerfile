FROM openjdk:17

COPY . /usr/src/myapp

WORKDIR /usr/src/myapp

ENV TZ "Asia/Shanghai"

RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone

RUN java -jar 

ENTRYPOINT ["java", "-jar", "/app.jar"]
