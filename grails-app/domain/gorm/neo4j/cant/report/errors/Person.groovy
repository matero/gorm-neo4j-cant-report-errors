package gorm.neo4j.cant.report.errors

class Person {
    String name

    static mapping = {
        labels "Person", "People"
        dynamicAssociations true
    }
}
