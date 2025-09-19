package nightyrelief.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate
import scala.util.Try

abstract class User(_fullName: String,
                    _icNumber: String,
                    _email: String,
                    _password: String) {
  def this() = this(null, null, null, null)
  var id: ObjectProperty[Option[Int]] = ObjectProperty[Option[Int]](None)
  var fullName: StringProperty = StringProperty(_fullName)
  var icNumber: StringProperty = StringProperty(_icNumber)
  var email: StringProperty = StringProperty(_email)
  var password: StringProperty = StringProperty(_password)
  var phoneNumber: StringProperty = StringProperty("")
  var dateOfBirth: ObjectProperty[LocalDate] = ObjectProperty[LocalDate](LocalDate.now())

  def save(): Try[Int]
  def delete(): Try[Int]
  def exists: Boolean
}