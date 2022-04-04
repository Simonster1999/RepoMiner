@echo off
set /p url="Enter your git repository link here: "

set origin=%CD%

if "%url:~0,5%" == "https" (set user_repo=%url:~19%) else (set user_repo=%url:~15%)

for /f "tokens=2 delims=/" %%a in ("%user_repo%") do (
  set temp=%%a
)

for /f "tokens=1 delims=." %%a in ("%temp%") do (
  set repo_name=%%a
)

cd /Users/%USERNAME%

call git clone %url%
cd %repo_name%

call mvn -Dmaven.test.failure.ignore=true -Djacoco.destFile=./coverage/jacoco.exec -Djacoco.dataFile=./coverage/jacoco.exec clean org.jacoco:jacoco-maven-plugin:prepare-agent install org.jacoco:jacoco-maven-plugin:report

cd %origin%\XmlParser\out\production\XmlParser

java Parser

cd %origin%