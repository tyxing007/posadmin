echo 停止Tomcat
net stop Tomcat7

pause

rd "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\admin\" /s/q

D:

echo 拷贝文件
copy admin.war "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\"
echo 文件拷贝成功

pause

echo 启动Tomcat
net start Tomcat7

echo 部署完成

pause