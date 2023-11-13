rem 复制时间戳到剪切板
rem set /p="%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%"<nul | clip
rem rename MLibs-release.aar MLibs-release"%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%".aar
if exist MLibs-release.aar rename MLibs-release.aar MLibs-release"%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%".aar