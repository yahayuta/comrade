@echo off
REM Compile ComradeFX and MapData with JavaFX 24.0.1
set JAVAFX_LIB=C:\javafx-sdk-24.0.1\lib
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml,javafx.media ComradeFX.java MapData.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b %errorlevel%
)
echo Compilation successful.
