@echo off
set CP=./system/felix-launcher.jar;./system/org.apache.felix.framework-6.0.1.jar

setlocal enabledelayedexpansion
for %%f in (ext\*) do ( 
	@set "CP=!CP!;%%f"
)

set VM_ARGS=-Dfile.encoding=UTF-8 -Dorg.osgi.framework.storage.fromlevel=40 -Dfelix.config.properties=file:./config/config.properties
set SECURITY_ARGS=-Dorg.osgi.framework.security=osgi -Djava.security.policy=config/all.policy
set CLEAN_ARGS=-Dorg.osgi.framework.storage.clean=onFirstInit

:TOP
IF $%1$ == $$ GOTO RUN
IF %1 == -security (
set VM_ARGS=%VM_ARGS% %SECURITY_ARGS%
SHIFT
GOTO TOP
)
IF %1 == -clean (
set VM_ARGS=%VM_ARGS% %CLEAN_ARGS%
SHIFT
GOTO TOP
)

:RUN
set MAIN_CLASS=org.apache.felix.main.Main

set JAVA=java

@echo on
%JAVA% %VM_ARGS% -cp %CP% %MAIN_CLASS%
@echo off
endlocal