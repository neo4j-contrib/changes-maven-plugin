# Maven Changes Plugin

## Extract change log section

Reads human-written CHANGES.txt, and pulls out change log entries for the
current project version.

Example: https://github.com/neo4j/maven-changes-plugin/tree/master/src/functionaltest/testprojects/singleChangelogTest

## Deplying & combining change logs

Deploys the relevant changelog entries for the current project version.
Optionally, pulls in deployed changelogs from other projects, and adds
them to this projects changelog.

Example: https://github.com/neo4j/maven-changes-plugin/tree/master/src/functionaltest/testprojects/attachChangelogTest
