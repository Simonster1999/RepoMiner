@echo off
set /p url="Enter your git repository link here: "

rem Save starting directory
set origin=%CD%

rem Determine if link is https or ssh, extract "user/repository.git"
if "%url:~0,5%" == "https" (set user_repo=%url:~19%) else (set user_repo=%url:~15%)

rem Extract repository name from link
for /f "tokens=2 delims=/" %%a in ("%user_repo%") do (
  set temp=%%a
)

rem Remove last 4 characters (.git)
set repo_name=%temp:~0,-4%

rem Navigate to user. All repositires will be cloned here
cd /Users/%USERNAME%

rem Clone and navigate to repository
call git clone %url%
cd %repo_name%

rem Arguments to let projects build successfully
set skip=-Drat.skip -Dcheckstyle.skip -Dmaven.javadoc.skip=true -Dgpg.skip

rem --------------- Code coverage ---------------
echo --------------- Code coverage ---------------

rem --------------- Tool: Jacoco ---------------
set jacoco=org.jacoco:jacoco-maven-plugin:
call mvn -q %skip% -Djacoco.destFile=./coverage/jacoco.exec -Djacoco.dataFile=./coverage/jacoco.exec clean %jacoco%prepare-agent install %jacoco%report

echo Running Jacoco... This can take a while

rem Parse coverage data
cd %origin%\Parser
call mvn -q clean install
cd target

rem Args: Xmlpath, Tool
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /%repo_name%/target/site/jacoco/jacoco.xml Jacoco

rem --------------- After Jacoco ---------------
rem Skip Jacoco when running other tools
set skip=-Drat.skip -Dcheckstyle.skip -Dmaven.javadoc.skip=true -Dgpg.skip -Djacoco.skip=true

rem --------------- Tool: Clover ---------------
cd /Users/%USERNAME%/%repo_name%

echo Running Clover... This can take a while

call mvn -q clean compile

set clover=org.openclover:clover-maven-plugin:
call mvn -q %skip% %clover%setup test %clover%aggregate %clover%clover

rem Parse coverage data
cd %origin%/Parser/target

rem Args: Xmlpath, Tool
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /%repo_name%/target/site/clover/clover.xml Clover

rem --------------- Tool: Jmockit ---------------
cd /Users/%USERNAME%/%repo_name%

echo Running Jmockit... This can take a while

call mvn -q clean compile

rem Get tool dependency, use tool in surefire plugin
call mvn -q dependency:get -Dartifact=org.jmockit:jmockit:1.49
call mvn -q %skip% clean test -Dmaven.test.failure.ignore=true -DargLine="-javaagent:\"${settings.localRepository}\"/org/jmockit/jmockit/1.49/jmockit-1.49.jar -Dcoverage-output=serial -Djmockit-coverage-metrics=all"

rem Parse coverage data
cd %origin%/Parser/target

rem Args: Xmlpath, Tool
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /%repo_name%/target/coverage.ser Jmockit

rem --------------- Mutation testing ---------------
echo --------------- Mutation testing ---------------

rem --------------- Tool: PITest ---------------
set var=F
echo.
set /p ans="Do you want to run PITest? It can take a long time. [Y/N]: "
if "%ans%" == "Y" set var=T
if "%ans%" == "y" set var=T
if "%var%" == "F" goto litDar

cd /Users/%USERNAME%/%repo_name%

call mvn -q test-compile org.pitest:pitest-maven:mutationCoverage %skip%

rem Call parser..

rem --------------- Tool: LittleDarwin ---------------
:litDar
set var=F
echo.
set /p ans="Do you want to run LittleDarwin? It can take a long time. [Y/N]: "
if "%ans%" == "Y" set var=T
if "%ans%" == "y" set var=T
if "%var%" == "F" goto end

cd %origin%

rem Check if LittleDarwin.exe is already downloaded from previous runs
if not exist LittleDarwin.exe (
  echo Downloading LittleDarwin 0.10.6
  curl -L -o LittleDarwin.exe https://github.com/aliparsai/LittleDarwin/raw/master/binaries/0.10.6/LittleDarwin.exe
)

set skipComma=-Drat.skip,-Dcheckstyle.skip,-Dmaven.javadoc.skip=true,-Dgpg.skip,-Djacoco.skip=true
call LittleDarwin.exe -m -b -p /Users/%USERNAME%/%repo_name%/src/main -t /Users/%USERNAME%/%repo_name% --timeout=100 --initial-build-command mvn,clean,compile,%skipComma% -c mvn,clean,test

rem Call parser..

rem --------------- Ending ---------------
:end

cd %origin%\Parser\target

rem Print summary by giving no args
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar 
cd %origin%
echo.
echo Do you wish to remove %repo_name%?
rd /s "/Users/%USERNAME%/%repo_name%"




