setlocal
set module=%1

echo "Adding module " %module%

call mvn archetype:create -DarchetypeGroupId=org.freehep -DarchetypeArtifactId=freehep-project-archetype -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=org.freehep -DartifactId=%module%
del /s DUMMY
rmdir %module%\src\main\java\org\freehep
rmdir %module%\src\main\java\org
rmdir %module%\src\main\resources\org\freehep
rmdir %module%\src\main\resources\org
rmdir %module%\src\test\java\org\freehep
rmdir %module%\src\test\java\org
rmdir %module%\src\test\resources\org\freehep
rmdir %module%\src\test\resources\org

svn propget svn:ignore . > svnignores
svn add %module%
svn propset svn:ignore --file svnignores %module%
del svnignores

REM svn propset svn:keywords --file svnkeywords.txt %module%\pom.xml
REM svn propset svn:eol-style native %module%\pom.xml

REM svn propset svn:keywords --file svnkeywords.txt %module%\src\site\site.xml
REM svn propset svn:eol-style native %module%\src\site\site.xml
