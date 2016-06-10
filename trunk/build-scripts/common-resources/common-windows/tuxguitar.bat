cd "."

SET "TG_HOME=."
SET "TG_MAIN_CLASS=org.herac.tuxguitar.app.TGMainSingleton"

SET "JAVA=java"

SET "JAVA_LIBRARY_PATH=%JAVA_LIBRARY_PATH%;lib\"

SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-ui-toolkit.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-ui-toolkit-swt.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-lib.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-editor-utils.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-gm-utils.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\tuxguitar-awt-graphics.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\swt.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\gervill.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\itext-pdf.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;lib\itext-xmlworker.jar"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;share\"
SET "JAVA_CLASSPATH=%JAVA_CLASSPATH%;dist\"

%JAVA% -cp %JAVA_CLASSPATH% -Dtuxguitar.home.path=%TG_HOME% -Djava.library.path=%JAVA_LIBRARY_PATH% %TG_MAIN_CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9 %10
