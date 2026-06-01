@echo off
setlocal

REM Try to find JAVA_HOME (JDK 17+)
set FOUND_JAVA=

REM Check IntelliJ IDEA bundled JDK
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javac.exe" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr"
    set FOUND_JAVA=1
    goto :run
)

REM Check common JDK locations
for %%d in (
    "C:\Program Files\Java\jdk-21"
    "C:\Program Files\Java\jdk-17"
    "C:\Program Files\Eclipse Adoptium\jdk-21*"
    "C:\Program Files\Eclipse Adoptium\jdk-17*"
) do (
    if exist "%%~d\bin\javac.exe" (
        set "JAVA_HOME=%%~d"
        set FOUND_JAVA=1
        goto :run
    )
)

if not defined FOUND_JAVA (
    echo [ERROR] JDK 17+ not found.
    echo Please install JDK 17 or higher from: https://adoptium.net
    pause
    exit /b 1
)

:run
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using Java: %JAVA_HOME%
echo.
echo Starting KanbaNice Desktop...
echo Make sure the backend is running on http://localhost:8080
echo.
call mvnw.cmd javafx:run
pause
