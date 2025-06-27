@echo off
REM Run ComradeFX with JavaFX 24.0.1
set JAVAFX_LIB=C:\javafx-sdk-24.0.1\lib
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml ComradeFX
