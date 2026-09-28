#!/bin/bash

######### Setting jswebserver and database environment #########...START

####################################
# jswebserver params
####################################
export JSWEBSRVRPORT=9595
#export JSWEBSRVRPORT=7575
export JSWEBHOME="$(cd "$(dirname "$(readlink -f "$0")")" && pwd)"

# Tracks the running server's PID directly, instead of grepping "ps -ef" for a version string.
# That grep-based approach breaks any time the jar's version changes (or the start/stop patterns
# drift apart, as they had), and can even match the grep command's own argument line.
PIDFILE="$JSWEBHOME/jswebserver.pid"

if [ $# -ne 1 ]
then
	echo "Usage:"
	echo "		jswebserver.sh <start|stop>"
	exit 1
fi

ACTION=$1

if [[ "$JSWEBSRVRPORT" = "" ]]; then
  printf "\nWeb Server connection info not setup!, setup the environment by editing jswebserver.sh before starting.\n\n"
  exit 1
fi

# Prints the PID and returns success only if PIDFILE exists AND that PID is actually alive.
# A leftover PIDFILE from a crash or a "kill -9" is cleaned up automatically.
is_running() {
  [[ -f "$PIDFILE" ]] || return 1
  local pid=$(cat "$PIDFILE")
  if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then echo "$pid"; return 0; fi
  rm -f "$PIDFILE"
  return 1
}

WEBPAGE=`hostname -i`:$JSWEBSRVRPORT
if [[ "$ACTION" = "start" ]]; then
  PROCESSID=$(is_running)
  if [[ -z "$PROCESSID" ]]; then
	nohup java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -cp jswebserver-1.1.jar:jarlib/* org.jswebserver.ServerLauncher >/dev/null 2>&1 &
	echo $! > "$PIDFILE" # $! is the PID of the background process just launched above
	printf "\njswebserver started, ProcessID $(cat "$PIDFILE"), open the web page http://${WEBPAGE} in your browser to confirm.\n\n"
  else
	printf "\njswebserver is already running, ProcessID ${PROCESSID}, open the web page http://${WEBPAGE} in your browser to confirm.\n\n"
	exit 1
  fi
  exit 0
fi


if [[ "$ACTION" = "stop" ]]; then
  printf "\nGet Process ID."
  PROCESSID=$(is_running)
  if [[ -z "$PROCESSID" ]]; then
	printf "\njswebserver is not currently running!\n\n"
	exit 1
  else
	printf "\nKilling ProcessID: ${PROCESSID}\n\n"
	kill "$PROCESSID"
	rm -f "$PIDFILE"
  fi
  exit 0
fi

echo "Invalid option $1"
echo "Usage:"
echo "		jswebserver.sh <start|stop>"
exit 1
