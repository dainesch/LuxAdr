FROM payara/server-full:5.182


ADD ./deploy $DEPLOY_DIR

USER root

ADD https://jdbc.postgresql.org/download/postgresql-42.2.4.jar ${PAYARA_PATH}/glassfish/domains/domain1/lib/

ADD ./asadmin.txt ${PAYARA_PATH}/asadmin.txt

RUN chmod 777 ${PAYARA_PATH}/asadmin.txt \
	&& chmod 644 ${PAYARA_PATH}/glassfish/domains/domain1/lib/*.jar \
	&& chmod a+x ${PAYARA_PATH}/generate_deploy_commands.sh

USER payara

