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

rem Tool: Jacoco
call mvn -q -Drat.skip -Dcheckstyle.skip -Dmaven.test.failure.ignore=true -Djacoco.destFile=./coverage/jacoco.exec -Djacoco.dataFile=./coverage/jacoco.exec clean org.jacoco:jacoco-maven-plugin:prepare-agent install org.jacoco:jacoco-maven-plugin:report

rem Parse coverage data
cd %origin%\Parser
call mvn -q clean install
cd target
java -jar Parser-1.0-SNAPSHOT-jar-with-dependencies.jar /jackson-dataformat-xml/target/site/jacoco/jacoco.xml Jacoco

rem Tool: Clover
cd /Users/%USERNAME%/%repo_name%

call mvn -q clean compile

call mvn -q -Dmaven.test.failure.ignore=true org.openclover:clover-maven-plugin:instrument 
call mvn -q org.openclover:clover-maven-plugin:clover

rem No parser yet

rem Ending
cd /Users/%USERNAME%/
echo
echo Do you wish to remove %repo_name%?
rd /s "/Users/%USERNAME%/%repo_name%"
cd %origin%



