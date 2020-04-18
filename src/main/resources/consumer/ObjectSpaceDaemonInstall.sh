#!/bin/bash
function commandIsSuccess(){
	if [ ! $? -eq 0 ]; then
		echo "$1 异常!"
		exit 1
	fi
}
cd /
#1.建立运行环境
echo "------------------------自动化运维平台  运行环境构建中...------------------------"
if [ -d "/usr/local/bin/ObjectSpaceDaemon/" ];then
rm -rf /usr/local/bin/ObjectSpaceDaemon/
fi

commandIsSuccess "删除旧版本"

echo "正在构建进程环境..."
mkdir /usr/local/bin/ObjectSpaceDaemon/
commandIsSuccess "构建进程环境"


if [ ! -d "/var/log/ObjectSpaceDaemon/" ];then
echo "正在构建日志环境..."
mkdir /var/log/ObjectSpaceDaemon/
fi
commandIsSuccess "构建日志环境"

echo "------------------------自动化运维平台  运行环境构建完毕------------------------"
echo ""
#2.下载jar包、jar包启动脚本、卸载脚本
echo "------------------------自动化运维平台  正在安装依赖组件...---------------------"
echo ""
echo "守护进程安装中..."
wget -P /usr/local/bin/ObjectSpaceDaemon/ download.objectspace.cn/ObjectSpaceDaemon/ObjectSpaceDaemon.jar
commandIsSuccess "安装守护进程"

echo "启动程序安装中..."
wget -P /usr/local/bin/ObjectSpaceDaemon/ download.objectspace.cn/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh
commandIsSuccess "安装启动程序..."

echo "卸载程序安装中..."
wget -P /usr/local/bin/ObjectSpaceDaemon/ download.objectspace.cn/ObjectSpaceDaemon/ObjectSpaceDaemonUninstall.sh
commandIsSuccess "安装卸载程序"
echo ""
echo "------------------------自动化运维平台  依赖组件安装完毕------------------------"

#3.运行jar包
echo "------------------------自动化运维平台  守护进程启动中...-----------------------"
echo ""
echo "启动进程..."
sh /usr/local/bin/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh init $1
commandIsSuccess "启动进程"
echo "进程启动完毕!"
#4.设置jar包为开机启动
echo "设置系统启动项..."
echo ""
chmod +x /usr/local/bin/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh
commandIsSuccess "修改启动程序权限"
sudo cp /usr/local/bin/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh /etc/rc.d/init.d/
commandIsSuccess "迁移启动程序"
chkconfig --add /etc/rc.d/init.d/ObjectSpaceDaemon/ObjectSpaceDaemonStartUp.sh
commandIsSuccess "加入系统启动项"
chkconfig ObjectSpaceDaemonStartUp.sh on
commandIsSuccess "开启守护模式"
echo ""
echo "系统启动项设置完毕!"
echo ""

echo "------------------------自动化运维平台  守护进程安装完毕------------------------"
exit 0
