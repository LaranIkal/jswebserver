#!/bin/ksh

######### Setting jswebserver and database environment #########...START

####################################
# jswebserver params
####################################
export JSWEBSRVRPORT=9393
#export JSWEBSRVRPORT=7575
export JSWEBHOME=`pwd`

####################################
####################################
#
# The next lines are to show that 
# you can configure connections in
# the environment and then use them 
# in your JavaScript server web scripts.
# this way you avoid reading from 
# the file system everytime. 
#
####################################
####################################

####################################
# DB Params - SQLITE
####################################
export SQLITEDB=webapps/mywebpage/data/mysqlitedb.sqlite

####################################
# DB Params - APACHE DERBY
####################################
export DERBYDBSERVER=localhost
export DERBYDBPORT=1527
export DERBYDBNAME=myderbydb
#export DERBYDBUSER=dbuser
#export DERBYDBPASS=dbpassword


####################################
# DB Params - ORACLE
####################################
#export ORADBSERVER=localhost
#export ORADBPORT=1521
#export ORADBNAME=myoradb
#export ORADBUSER=dbuser
#export ORADBPASS=dbpassword


####################################
# DB Params - POSTGRESQL
####################################
#export PSQLDBSERVER=localhost
#export PSQLDBPORT=5432
#export PSQLDBNAME=mypgdb
#export PSQLDBUSER=dbuser
#export PSQLDBPASS=dbpassword

######### Setting jswebserver and database environment #########...END


if [ $# -ne 1 ]
then
	echo "Usage:"
	echo "		jswebserver.ksh <start|stop>"
	exit 1
fi

ACTION=$1

if [[ "$JSWEBSRVRPORT" = "" ]]; then
  printf "\nWeb Server connection info not setup!, setup the environment by editing jswebserver.ksh before starting.\n\n"
  exit 1
fi


if [[ "$ACTION" = "start" ]]; then
  PROCESSID=`ps -ef | grep jswebserver | grep 1.0 | awk '{print $2}'`
  if [[ "$PROCESSID" = "" ]]; then
	nohup java -cp jswebserver-1.0.jar:jarlib/* org.jswebserver.ServerLauncher >/dev/null 2>&1 &
	WEBPAGE=`hostname -i`:$JSWEBSRVRPORT
	printf "\njswebserver started, open the web page ${WEBPAGE} in your browser to confirm.\n\n"
  else
	printf "\njswebserver is already running, ProcessID ${PROCESSID}.\n\n"
	exit 1
  fi
  exit 0
fi


if [[ "$ACTION" = "stop" ]]; then
  printf "\nGet Process ID."
  PROCESSID=`ps -ef | grep jswebserver | grep 1.0 | awk '{print $2}'`
  if [[ "$PROCESSID" = "" ]]; then
	printf "\njswebserver is not currently running!\n\n"
	exit 1
  else
	printf "\nKilling ProcessID: ${PROCESSID}\n\n"
	kill $PROCESSID
  fi
  exit 0
fi

echo "Invalid option $1"
echo "Usage:"
echo "		jswebserver.ksh <start|stop>"
exit 1



