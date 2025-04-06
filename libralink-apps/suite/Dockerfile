FROM --platform=linux/amd64 openjdk:11
WORKDIR /suite

COPY target/suite-*.jar /libralink/suite.jar

ENV JAVA_OPTIONS=""
CMD java ${JAVA_OPTIONS} \
    -Daws.accessKeyId="${AWS_ACCESS_KEY}" \
    -Daws.secretKey="${AWS_SECRET_KEY}" \
    -Daws.region="${AWS_REGION}" \
    -Daws.s3.bucketName="${AWS_S3_BUCKET}" \
    -jar /libralink/suite.jar
