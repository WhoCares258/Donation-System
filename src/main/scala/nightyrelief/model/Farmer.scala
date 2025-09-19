package nightyrelief.model

import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, StringProperty}
import java.time.LocalDate
import nightyrelief.util.Database
import nightyrelief.util.DateUtil.*
import scalikejdbc.*

import scala.util.Try

class Farmer(_fullName: String,
             _icNumber: String,
             _email: String,
             _password: String) extends User(_fullName,_icNumber, _email, _password) with Database:
  var farmName: StringProperty = StringProperty("")
  var farmLocation: StringProperty = StringProperty("")
  var farmSize: IntegerProperty = IntegerProperty(1)
  var farmEstablishedDate: ObjectProperty[LocalDate] = ObjectProperty[LocalDate](LocalDate.now())
  var numberOfEmployees: IntegerProperty = IntegerProperty(1)
  var farmDetails: StringProperty = StringProperty("")

  def save(): Try[Int] = {
    if (!exists) {
      Try(DB.autoCommit { implicit session =>
        val generatedId = sql"""
          INSERT INTO farmer (fullName, icNumber, email, password, phoneNumber, dateOfBirth,
          farmName, farmLocation, farmSize, farmEstablishedDate, numberOfEmployees, farmDetails)
          VALUES (${fullName.value}, ${icNumber.value}, ${email.value}, ${password.value}, ${phoneNumber.value},
          ${dateOfBirth.value.asString}, ${farmName.value}, ${farmLocation.value},${farmSize.value},
          ${farmEstablishedDate.value.asString}, ${numberOfEmployees.value}, ${farmDetails.value})
        """.updateAndReturnGeneratedKey.apply()
        this.id.value = Some(generatedId.toInt)
        generatedId.toInt
      })
    } else
      Try(DB.autoCommit { implicit session =>
        sql"""
              UPDATE farmer
              SET
              fullName = ${fullName.value},
              icNumber = ${icNumber.value},
              email = ${email.value},
              password = ${password.value},
              phoneNumber = ${phoneNumber.value},
              dateOfBirth = ${dateOfBirth.value.asString},
              farmName = ${farmName.value},
              farmLocation = ${farmLocation.value},
              farmSize = ${farmSize.value},
              farmEstablishedDate = ${farmEstablishedDate.value.asString},
              numberOfEmployees = ${numberOfEmployees.value},
              farmDetails = ${farmDetails.value}
              WHERE id = ${id.value.get}
           """.update.apply()
      })
    end if
  }
  end save

  def delete(): Try[Int] =
    if (exists) then
      Try(DB.autoCommit { implicit session =>
        sql"""
              DELETE FROM farmer
              WHERE id = ${id.value.get}
           """.update.apply()
      })
    else
      throw new Exception("Farmer does not exist in database!")
    end if
  end delete

  def exists: Boolean = {
    id.value.isDefined && {
      DB.readOnly { implicit session =>
        sql"""
            SELECT * FROM farmer WHERE
            id = ${id.value.get}
         """.map(x => x.int("id")).single.apply()
      } match
        case Some(_) => true
        case None    => false
    }
  }
  end exists

end Farmer

object Farmer extends Database:
  def apply(
             _id: Int,
             _fullName: String,
             _icNumber: String,
             _email: String,
             _password: String,
             _phoneNumber: String,
             _dateOfBirth: String,
             _farmName: String,
             _farmLocation: String,
             _farmSize: Int,
             _farmEstablishedDate: String,
             _numberOfEmployees: Int,
             _farmDetails: String
           ): Farmer =
    new Farmer(_fullName, _icNumber, _email, _password):
      id.value = Some(_id)
      phoneNumber.value = _phoneNumber
      dateOfBirth.value = _dateOfBirth.parseLocalDate
      farmName.value = _farmName
      farmLocation.value = _farmLocation
      farmSize.value = _farmSize
      farmEstablishedDate.value = _farmEstablishedDate.parseLocalDate
      numberOfEmployees.value = _numberOfEmployees
      farmDetails.value = _farmDetails
  end apply
  def initializeTable() =
    DB.autoCommit { implicit session =>
      sql"""
            CREATE TABLE farmer (
              id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
              fullName varchar(255),
              icNumber varchar(16),
              email varchar(64),
              password varchar(16),
              phoneNumber varchar(16),
              dateOfBirth varchar(64),
              farmName varchar(255),
              farmLocation varchar(255),
              farmSize int,
              farmEstablishedDate varchar(64),
              numberOfEmployees int,
              farmDetails varchar(255)
            )
         """.execute.apply()
    }
  end initializeTable

  def getAllFarmers: List[Farmer] =
    DB.readOnly { implicit session =>
      sql"SELECT * FROM farmer".map { x =>
        Farmer(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"),
          x.string("farmName"), x.string("farmLocation"), x.int("farmSize"),
          x.string("farmEstablishedDate"), x.int("numberOfEmployees"), x.string("farmDetails")
        )
      }.list.apply()
    }
    
  def getFarmerById(farmerId: Int): Option[Farmer] =
    DB.readOnly { implicit session =>
      sql"""
        SELECT * FROM farmer WHERE id = ${farmerId}
      """.map { x =>
        Farmer(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"),
          x.string("farmName"), x.string("farmLocation"), x.int("farmSize"),
          x.string("farmEstablishedDate"), x.int("numberOfEmployees"), x.string("farmDetails")
        )
      }.single.apply()
    }