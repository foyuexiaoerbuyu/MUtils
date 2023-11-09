@echo off
ren H:\Program\Mytools.jar Mytools_"%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%".jar
copy   G:\MWeb\webUtils\Mytools\out\artifacts\MyTools_jar\Mytools.jar H:\Program\  /y