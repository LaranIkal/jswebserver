
Title JSWEBSERVER

REM to run this process in background, run jswebserver.vbs
SET JSWEBSRVRPORT=9595

REM ####################################
REM ####################################
REM #
REM # The next lines are to show that 
REM # you can configure connections in
REM # the environment and then use them 
REM # in your JavaScript server web scripts.
REM # this way you avoid reading from 
REM # the file system everytime. 
REM #
REM ####################################
REM ####################################

REM ####################################
REM # DB Params - SQLITE
REM ####################################
REM export SQLITEDB=webapps/mywebpage/data/mysqlitedb.sqlite

REM ####################################
REM # DB Params - APACHE DERBY
REM ####################################
REM export DERBYDBSERVER=localhost
REM export DERBYDBPORT=1527
REM export DERBYDBNAME=myderbydb
REM #export DERBYDBUSER=dbuser
REM #export DERBYDBPASS=dbpassword

java -cp jswebserver-1.0.jar;jarlib/* org.jswebserver.ServerLauncher

