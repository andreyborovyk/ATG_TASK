@echo off
setlocal

:startscript
  ::
  :: Check that DYNAMO_HOME and DAS_ROOT are set
  ::
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript

:dynamoenv
  if "%1"=="-h" goto :usage
  if "%2"=="" goto :nofiles

  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :endofscript

  echo -------------------
  echo JAVA_ARGS:  %JAVA_ARGS% 
  echo -------------------
  echo CLASSPATH:  %CLASSPATH%
  echo -------------------
  echo PATH:       %PATH%

  %JAVA_VM% %JAVA_ARGS% atg.xml.Combiner %*
  goto :endofscript
  
:nofiles
  echo xmlCombine: No arguments were supplied
  goto :usage
  
:usage
  echo usage: xmlCombine.bat {input file 1...n} -o {output file}
  goto :endofscript

:endofscript

endlocal
