FROM ${base.sdnc.image.repo} AS stage0
FROM ${base.image.repo}

ENV ODL_HOME /opt/opendaylight
ENV IMAGEPATH ${base.image.repo}//${image.name}
ENV IMAGENAMES "ONAP SDNC"//"SDNR Patches"
ENV A1_ADAPTER_NORTHBOUND=false
ENV CCSDKFEATUREVERSION ${ccsdk.features.version}
ENV CCSDKFEATURE_SDNR_GROUPID ${ccsdk.feature.groupid}
ENV FEATURES_SDNR ${ccsdk.feature.repopath}
ENV SDNR_DM_REPO mvn:${CCSDKFEATURE_SDNR_GROUPID}/sdnr-wt-feature-aggregator/${CCSDKFEATUREVERSION}/xml/features, \
    mvn:${CCSDKFEATURE_SDNR_GROUPID}/sdnr-wt-feature-aggregator-devicemanager-base/${CCSDKFEATUREVERSION}/xml/features, \
    mvn:${CCSDKFEATURE_SDNR_GROUPID}/sdnr-wt-feature-aggregator-devicemanager/${CCSDKFEATUREVERSION}/xml/features, \
    mvn:${CCSDKFEATURE_SDNR_GROUPID}/sdnr-wt-feature-aggregator-devicemanager-unm-generic/${CCSDKFEATUREVERSION}/xml/features, \
    mvn:${CCSDKFEATURE_SDNR_GROUPID}/sdnr-wt-feature-aggregator-oauth/${CCSDKFEATUREVERSION}/xml/features

ENV SDNR_FEATURES_BOOT_INITIAL odl-restconf-all,odl-netconf-topology,odl-netconf-callhome-ssh,odl-mdsal-all,odl-mdsal-apidocs,odl-daexim-all,odl-restconf-nb-rfc8040
ENV JAVA_OPTS="-Xms512m -Xmx3g" 
ENV ODL_ADMIN_PASSWORD "Kp8bJ4SXszM0WXlhak3eHlcse2gAw84vaoGGmJvUy2U"
USER root

# Install sudo and IP utilities
RUN apk update && apk --no-cache add gcompat sudo iputils openssl bash unzip rsync nss python3
# Enable wheel group
RUN sed -e 's/# %wheel ALL=(ALL) NOPASSWD: ALL/%wheel ALL=(ALL) NOPASSWD: ALL/g' -i /etc/sudoers
# Create odl user
RUN addgroup -S odl && adduser -S odl -G odl && addgroup odl wheel
#RUN addgroup odl wheel

# copy ODL
COPY --chown=odl:odl odl/karaf-${odl.karaf.version} $ODL_HOME
RUN ln -s /opt/opendaylight /opt/opendaylight/current

# copy apps dependencies
COPY --chown=odl:odl system /tmp/system
RUN rsync -a --ignore-existing /tmp/system $ODL_HOME
# copy patches
#COPY patches/pax-web-jetty-${odl.ops4j.version}.jar $ODL_HOME/system/org/ops4j/pax/web/pax-web-jetty/${odl.ops4j.version}/
#COPY patches/aaa-authn-api-${odl.aaa.version}.jar $ODL_HOME/system/org/opendaylight/aaa/aaa-authn-api/${odl.aaa.version}/
# Add Frinx patches
#COPY  --chown=odl:odl frinx/org/opendaylight/netconf $ODL_HOME/system/org/opendaylight/netconf/

COPY --chown=odl:odl system/org/apache/kafka $ODL_HOME/system/org/apache/kafka
RUN true
COPY --chown=odl:odl system/com/github/luben $ODL_HOME/system/com/github/luben
RUN true
COPY --chown=odl:odl system/org/lz4 $ODL_HOME/system/org/lz4
RUN true
COPY --chown=odl:odl system/org/xerial/snappy $ODL_HOME/system/org/xerial/snappy

# Patch WT Apps

#RUN rm -r $ODL_HOME/$FEATURES_SDNR
#COPY --chown=odl:odl $FEATURES_SDNR $ODL_HOME/$FEATURES_SDNR
# Copy scripts
RUN true
COPY --from=stage0 --chown=odl:odl /opt/onap /opt/onap
RUN true
COPY --chown=odl:odl scripts/startODL.sh /opt/onap/sdnc/bin/
RUN true
COPY --chown=odl:odl scripts/installCerts.py /opt/onap/sdnc/bin/
# Copy repository
# visualVM configuration
RUN true
COPY --chown=odl:odl etc $ODL_HOME/etc

#Adjust repositories
RUN sed -i -e "\|featuresRepositories|s|$|, ${SDNR_DM_REPO}|"  $ODL_HOME/etc/org.apache.karaf.features.cfg
#Adjust featuresboot
RUN sed -i -e "\|featuresBoot *=|s|$|,${SDNR_FEATURES_BOOT_INITIAL}|"  $ODL_HOME/etc/org.apache.karaf.features.cfg

RUN chmod 775 /opt/onap/sdnc/bin/*.sh
#RUN apk add curl
#back to user odl

# Install ssl and java certificates
ENV JAVA_SECURITY_DIR /etc/ssl/certs/java
ENV SDNC_STORE_DIR /opt/onap/sdnc/data/stores

COPY --from=stage0 $SDNC_STORE_DIR/truststoreONAPall.jks $JAVA_SECURITY_DIR/
COPY --from=stage0 $SDNC_STORE_DIR/truststoreONAPall.jks $SDNC_STORE_DIR/
RUN keytool -importkeystore -srckeystore $JAVA_SECURITY_DIR/truststoreONAPall.jks -srcstorepass changeit -destkeystore $JAVA_SECURITY_DIR/cacerts  -deststorepass changeit -noprompt
RUN keytool -importkeystore -srckeystore $JAVA_SECURITY_DIR/truststoreONAPall.jks -srcstorepass changeit -destkeystore /opt/java/openjdk/lib/security/cacerts  -deststorepass changeit -noprompt
RUN mkdir $ODL_HOME/certs
RUN chown -R odl:odl /opt


ENV SDNC_SECUREPORT=8443
ENV SDNC_KEYPASS=changeit2
#${sdnc.keypass}
ENV SDNC_KEYSTORE=org.onap.sdnc.p12
RUN echo org.osgi.service.http.secure.enabled=true >> $ODL_HOME/etc/custom.properties
RUN echo org.osgi.service.http.port.secure=$SDNC_SECUREPORT >> $ODL_HOME/etc/custom.properties
RUN echo org.ops4j.pax.web.ssl.keystore=$SDNC_STORE_DIR/truststoreONAPall.jks >> $ODL_HOME/etc/custom.properties
RUN echo org.ops4j.pax.web.ssl.password=$SDNC_KEYPASS >> $ODL_HOME/etc/custom.properties
RUN echo org.ops4j.pax.web.ssl.keypassword=$SDNC_KEYPASS >> $ODL_HOME/etc/custom.properties

USER odl
WORKDIR /opt/opendaylight

CMD [ "/bin/bash" ]
ENTRYPOINT [ "/opt/onap/sdnc/bin/startODL.sh" ]
EXPOSE 8181 1090 1099

