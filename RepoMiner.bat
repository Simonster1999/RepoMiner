@echo off
set /p url="Enter your git repository link here: "
set name_part=%url%
if "%url:~0,5%" == "https" (set name_part=%url:~19%)
for /f "tokens=2 delims=/" %%a in ("%name_part%") do (
  set AFTER_SLASH=%%a
)

for /f "tokens=1 delims=." %%a in ("%AFTER_SLASH%") do (
  set BEFORE_PERIOD=%%a
)

git clone %url%
cd %BEFORE_PERIOD%

mvn -Djacoco.destFile=./coverage/jacoco.exec -Djacoco.dataFile=./coverage/jacoco.exec clean org.jacoco:jacoco-maven-plugin:prepare-agent install org.jacoco:jacoco-maven-plugin:report