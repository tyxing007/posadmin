echo ֹͣTomcat
net stop Tomcat7

pause

rd "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\admin\" /s/q

D:

echo �����ļ�
copy admin.war "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\"
echo �ļ������ɹ�

pause

echo ����Tomcat
net start Tomcat7

echo �������

pause