#!/bin/bash
#chkconfig: 2345 80 90    
#description:ObjectSpaceDaemonStarter
### BEGIN INIT INFO
# Provides:           NoCortY
# Required-Start:     
# Required-Stop:      
# Default-Start:      2 3 4 5
# Default-Stop:       0 1 6
# Short-Description:  ObjectSpaceDaemon
# Description:        ObjectSapceDaemonStartUp Shell
#
### END INIT INFO
RETVAL=0
USERID=$2
start(){
DATE=$(date "+%Y-%m-%d")
source /etc/profile
nohup java -jar /usr/local/bin/ObjectSpaceDaemon/ObjectSpaceDaemon.jar > /var/log/ObjectSpaceDaemon/ObjectSpaceDaemon-$DATE.log 2>&1 &
}

init(){
if [ ! $USERID ]; then
 echo "请正确输入命令"
 exit 1
fi
DATE=$(date "+%Y-%m-%d")
source /etc/profile
nohup java -jar /usr/local/bin/ObjectSpaceDaemon/ObjectSpaceDaemon.jar $USERID > /var/log/ObjectSpaceDaemon/ObjectSpaceDaemon-$DATE.log 2>&1 &
}

stop(){
	ps -ef|grep ObjectSpaceDaemon.jar|grep -v grep|awk '{print $2}'|xargs kill -9
}
cd /
case $1 in 
start)
start
;;
stop)
stop
;;
init)
init
;;
esac
exit $RETVAL

