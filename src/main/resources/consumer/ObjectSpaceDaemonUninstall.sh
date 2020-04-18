#!/bin/bash
cd /
echo "------------------------自动化运维平台  卸载程序启动...------------------------"
echo ""
if [ -d "/usr/local/bin/ObjectSpaceDaemon/" ];then
echo "卸载守护进程以及其它相关文件..."
echo ""
rm -rf /usr/local/bin/ObjectSpaceDaemon/
fi

#2.删除日志
if [ -d "/var/log/ObjectSpaceDaemon/" ];then
echo "删除日志..."
echo ""
rm -rf /var/log/ObjectSpaceDaemon/
fi

#3.删除自动启动
echo "取消系统启动项..."
echo ""
chkconfig test.sh off
chkconfig --del /etc/rc.d/init.d/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh
if [ -f "/etc/rc.d/init.d/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh" ];then
rm -rf /etc/rc.d/init.d/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh
fi
echo "------------------------自动化运维平台  卸载完毕------------------------"

