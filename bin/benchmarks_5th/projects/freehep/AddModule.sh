#!/bin/sh
module=$1

echo "Adding module " $module

mvn archetype:create -DarchetypeGroupId=org.freehep -DarchetypeArtifactId=freehep-project-archetype -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=org.freehep -DartifactId=$module
find $module -print | grep DUMMY | xargs rm
rmdir $module/src/main/java/org/freehep
rmdir $module/src/main/java/org
rmdir $module/src/main/resources/org/freehep
rmdir $module/src/main/resources/org
rmdir $module/src/test/java/org/freehep
rmdir $module/src/test/java/org
rmdir $module/src/test/resources/org/freehep
rmdir $module/src/test/resources/org

svn propget svn:ignore . > svnignores
svn add $module
svn propset svn:ignore --file svnignores $module
rm svnignores
