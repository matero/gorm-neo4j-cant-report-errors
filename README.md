# gorm-neo4j-cant-report-errors
Sample project to report an error on gorm-neo4j

When a validation on a dynamic properties is not successful, it fails to build the
error message as it can not access de property using the spring java-bean utilities.

This simple project tries to show this, and force the validation manually as the
validation infraesatructure is not currently working on development branch (see
https://github.com/grails/gorm-neo4j/issues/75).

Check the integration test PersonIntegrationSpec to see how static properties work
as expected, but dynamic ones fails to build error messages.
