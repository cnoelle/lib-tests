#!/bin/bash

#
# Start OGEMA with default configuration (Equinox OSGi and no security)
#

LAUNCHER="./system/felix-launcher.jar:./system/org.apache.felix.framework-6.0.1.jar"
CONFIG=${OGEMA_CONFIG:-config/config.xml}
JAVA=${JAVA_HOME:+${JAVA_HOME}/bin/}java
EXTENSIONS=ext$(find ext/ -iname "*jar" -printf :%p)
VMOPTS="-Dfile.encoding=UTF-8 -Dorg.osgi.framework.storage.fromlevel=40 -Dfelix.config.properties=file:./config/config.properties"
VMOPTS="$VMOPTS -cp $LAUNCHER:$EXTENSIONS"
CLEAN_ARGS="-Dorg.osgi.framework.storage.clean=onFirstInit"
SECURITY_ARGS="-Dorg.osgi.framework.security=osgi -Djava.security.policy=config/all.policy"

for var in "$@"
do
    echo "Next argument ${var}"
    case $var in
        -clean)
            echo "Case clean"
            VMOPTS="${VMOPTS} ${CLEAN_ARGS}"
            echo "OPT: ${VMOPTS}"
            ;;
        *)
            echo "Case default"
            VMOPTS="${VMOPTS} ${var}"
            ;;
    esac
done

$JAVA $VMOPTS org.apache.felix.main.Main $OPTIONS

