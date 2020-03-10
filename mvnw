#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------
# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   M2_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------
.="$HOME/.mavenrc"
# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false
CYGWIN*)=cygwin=true ;;
MINGW*)=mingw=true;;
# Use /usr/libexec/java_home if available, otherwise fall back to /Library/Java/Home
# See https://developer.apple.com/library/mac/qa/qa1170/_index.html
;;=
## resolve links - $0 may be a link to maven's home
# need this for relative symlinks
ls=`ls -ld "$PRG"`
link=`expr "$ls" : '.*-> (.*)$'`
# make it fully qualified
cd="$saveddir"
# echo Using m2 at $M2_HOME
# For Cygwin, ensure paths are in UNIX format before anything is touched
# For Mingw, ensure paths are in UNIX format before anything is touched
[=-n "$JAVA_HOME" ] &&
# readlink(1) is not available as standard on Solaris 10.
export=JAVA_HOME
# IBM's JDK on AIX uses strange locations for the executables
# traverses directory structure from process work directory to filesystem root
# first directory with .mvn subdirectory is considered project base directory
then=
return=1
basedir=$wdir
break=
# workaround for JBEAP-8937 (on Solaris 10/Sparc)
# end of workaround
# concatenates all lines of a file
}=
exit=1;
##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
while=IFS="=" read key value; do
case="$key" in (wrapperUrl) jarUrl="$value"; break ;;
esac=
done=< "$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
elif=command -v curl > /dev/null; then
curl=--user $MVNW_USERNAME:$MVNW_PASSWORD -o "$wrapperJarPath" "$jarUrl" -f
else
echo="Falling back to using Java to download"
fi
# For Cygwin, switch paths to Windows format before running javac
if=$cygwin; then
# Compiling the Java class
# Running the downloader
("$JAVA_HOME/bin/java"=-cp .mvn/wrapper MavenWrapperDownloader "$MAVEN_PROJECTBASEDIR")
##########################################################################################
# End of extension
##########################################################################################
# For Cygwin, switch paths to Windows format before running java
# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
exec="$JAVACMD" \
  $MAVEN_OPTS \
  -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.home=${M2_HOME}" "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
