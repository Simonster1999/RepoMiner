@echo off
set /p url="Enter your git repository link here: "

set origin=%CD%

rem Determine if link is https or ssh
if "%url:~0,5%" == "https" (set user_repo=%url:~19%) else (set user_repo=%url:~15%)

rem Extract repository name from link
for /f "tokens=2 delims=/" %%a in ("%user_repo%") do (
  set temp=%%a
)

for /f "tokens=1 delims=." %%a in ("%temp%") do (
  set repo_name=%%a
)

cd /Users/%USERNAME%

rem Clone and navigate to repository
call git clone %url%
cd %repo_name%

set skip=-Drat.skip -Dcheckstyle.skip -Dmaven.test.failure.ignore=true

rem Tool: Jacoco
set jacoco=org.jacoco:jacoco-maven-plugin:
call mvn -q %skip% -Djacoco.destFile=./coverage/jacoco.exec -Djacoco.dataFile=./coverage/jacoco.exec clean %jacoco%prepare-agent install %jacoco%report

rem Parse coverage data
cd %origin%\Parser
call mvn -q clean install
cd target

rem Args: Xmlpath, Tool
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /%repo_name%/target/site/jacoco/jacoco.xml Jacoco

rem Tool: Clover
cd /Users/%USERNAME%/%repo_name%

call mvn -q clean compile

set clover=org.openclover:clover-maven-plugin:
call mvn -q %skip% %clover%setup test %clover%aggregate %clover%clover

rem Parse coverage data
cd %origin%/Parser/target

rem Args: Xmlpath, Tool
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /%repo_name%/target/site/clover/clover.xml Clover

rem Ending
cd %origin%\Parser\target
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar 
cd %origin%
echo
echo Do you wish to remove %repo_name%?
rd /s "/Users/%USERNAME%/%repo_name%"




