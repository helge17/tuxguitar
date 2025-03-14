@ECHO OFF

SET "TG_START=%cd%"

cd %~dp0

SET "TG_HOME=."
SET "TG_MAIN_CLASS=app.tuxguitar.app.TGMainSingleton"

SET "JAVA=jre\bin\java"

SET "JAVA_LIBRARY_PATH=%JAVA_LIBRARY_PATH%;lib\"

SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\*"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;share\"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;dist\"

%JAVA% -cp %JAVA_CLASSPATH% -Dtuxguitar.home.path=%TG_HOME% -Djava.library.path=%JAVA_LIBRARY_PATH% %TG_MAIN_CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9 %10

cd %TG_START%
