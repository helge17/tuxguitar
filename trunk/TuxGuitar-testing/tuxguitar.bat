cd "platform-win32"

SET "JAVA=java"

SET "JAVA_LIBRARY_PATH=%JAVA_LIBRARY_PATH%;lib\"

SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;..\platform-all\tuxguitar.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;..\platform-all\lib\itext.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;..\platform-all\lib\gervill.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;..\platform-all\share\"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-dist.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\swt.jar"

SET "TG_CLASSPATH=%TG_CLASSPATH%;share\plugins"

SET "TG_MAIN_CLASS=org.herac.tuxguitar.app.TGMain"

%JAVA% -cp %JAVA_CLASSPATH% -Djava.library.path=%JAVA_LIBRARY_PATH% -Dtuxguitar.class.path=%TG_CLASSPATH% %TG_MAIN_CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9 %10
