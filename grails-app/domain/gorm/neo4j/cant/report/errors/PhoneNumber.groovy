package gorm.neo4j.cant.report.errors

class PhoneNumber {
    String number

    static mappings = {
        labels "Phone", "PhoneNumber"
    }
}
