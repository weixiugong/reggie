
FROM openjdk:8
VOLUME /tmp
#ADD 后面的参数是项目名字 / 后面的参数是自定义的别名
ADD reggie-0.0.1-SNAPSHOT.jar /reggie.jar
#这里的最后一个变量需要和前面起的别名相同
ENTRYPOINT ["java","-jar","/reggie.jar"]