REM echo +---------------------------------------------------+
REM echo Mise à jour de la méta cannelle
REM call .\sources_cannelle_2020\meta_sources\Meta\build.bat
echo +---------------------------------------------------+
set Path=%Path%;..\..\updateProjects\SlikSvn\bin
echo Path=%Path%
echo JAVA_HOME=%JAVA_HOME%
echo +---------------------------------------------------+

..\..\updateProjects\ant\apache-ant-1.8.4\bin\ant.bat -buildfile update_Cannelle_plugins_2020.xml

echo +---------------------------------------------------+
