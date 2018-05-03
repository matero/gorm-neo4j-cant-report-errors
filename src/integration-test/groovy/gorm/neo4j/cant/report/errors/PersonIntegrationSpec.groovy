package gorm.neo4j.cant.report.errors

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import grails.validation.ValidationErrors
import org.grails.datastore.gorm.validation.constraints.BlankConstraint
import org.grails.datastore.gorm.validation.constraints.NullableConstraint
import org.springframework.beans.NotReadablePropertyException
import spock.lang.Specification

@Integration
@Rollback
class PersonIntegrationSpec extends Specification {

    def messageSource

    void "normal properties are accessible"() {
        when: "a created model instance"
            def sam = new Person(name: "Sam")
        then: "their static properties can be accessed"
            sam.name == "Sam"
        and: "some dynamic properties also can be accessed"
            sam['email'] == null
    }

    void "simple dynamic properties are accessible"() {
        given: "a created model instance"
            def sam = new Person()
        when: "a dynamic property is setted"
            sam['email'] = "sam.sagaz@email.com"
        then: "it should be setted"
            sam['email'] == "sam.sagaz@email.com"
    }

    void "dynamic relationships are accessible"() {
        given: "a created model instance"
            def sam = new Person()
            def phoneNumber = new PhoneNumber(phone:"54 9 342 5144111").save()
        when: "a dynamic relation is setted"
            sam['phone'] = phoneNumber
        then: "it should be setted"
            sam['phone'] == phoneNumber
    }

    void "it is able to access static properties"() {
        given: "a created model instance"
            def sam = new Person()
        and:
            def contraint = new BlankConstraint(Person, "name", false, messageSource)
        and:
            def errors = new ValidationErrors(sam)

        when:
            contraint.validateWithVetoing(sam, "Sam", errors)
        then:
            !errors.hasErrors()

        when:
            contraint.validateWithVetoing(sam, "  ", errors)
        then:
            errors.hasErrors()
    }

    void "it is not able to work dynamic properties"() {
        given: "a created model instance"
            def sam = new Person()
            sam.email = "sam@email.com"
        and:
            def emailCantBeBlank = new BlankConstraint(Person, "email", false, messageSource)
        and:
            def errors = new ValidationErrors(sam)

        when:
            emailCantBeBlank.validateWithVetoing(sam, "sam@email.com", errors)
        then:
            !errors.hasErrors()

        when: "validates an illegal value that needs to build its message"
            emailCantBeBlank.validateWithVetoing(sam, " ", errors)
        then: "it fails"
            NotReadablePropertyException e = thrown()
            e.message == "Invalid property 'email' of bean class [gorm.neo4j.cant.report.errors.Person]: Bean property 'email' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?"
    }

    void "it is not able to work dynamic relationships"() {
        given: "a created model instance"
            def sam = new Person()
        and:
            def phoneIsRequired = new NullableConstraint(Person, "phone", false, messageSource)
        and:
            def errors = new ValidationErrors(sam)

        when: "validates legal values"
            phoneIsRequired.validateWithVetoing(sam, new PhoneNumber(number:"03 03 456"), errors)
        then: "it runs ok, because no msg must be build"
            !errors.hasErrors()

        when: "validates an illegal value that needs to build its message"
            phoneIsRequired.validateWithVetoing(sam, null, errors)
        then: "it fails"
            NotReadablePropertyException e = thrown()
            e.message == "Invalid property 'phone' of bean class [gorm.neo4j.cant.report.errors.Person]: Bean property 'phone' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?"
    }
}
