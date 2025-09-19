package nightyrelief.model

import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, StringProperty}
import java.time.LocalDate
import nightyrelief.util.Database
import nightyrelief.util.DateUtil.*
import scalikejdbc.*

import scala.util.Try

class Donor(_fullName: String,
            _icNumber: String,
            _email: String,
            _password: String) extends User(_fullName,_icNumber, _email, _password) with Database:
  var address: StringProperty = StringProperty("")
  var jobTitle: StringProperty = StringProperty("")
  var workplace: StringProperty = StringProperty("")
  var cause: StringProperty = StringProperty("")
  var donationType: StringProperty = StringProperty("")
  var extraNotes: StringProperty = StringProperty("")

  def save(): Try[Int] = {
    if (!exists) {
      Try(DB.autoCommit { implicit session =>
        val generatedId = sql"""
          INSERT INTO donor (fullName, icNumber, email, password, phoneNumber, dateOfBirth,
          address, jobTitle, workplace, cause, donationType, extraNotes)
          VALUES (${fullName.value}, ${icNumber.value}, ${email.value}, ${password.value}, ${phoneNumber.value},
          ${dateOfBirth.value.asString}, ${address.value}, ${jobTitle.value}, ${workplace.value},
          ${cause.value}, ${donationType.value}, ${extraNotes.value})
        """.updateAndReturnGeneratedKey.apply()
        this.id.value = Some(generatedId.toInt)
        generatedId.toInt
      })
    } else
      Try(DB.autoCommit { implicit session =>
        sql"""
              UPDATE donor
              SET
              fullName = ${fullName.value},
              icNumber = ${icNumber.value},
              email = ${email.value},
              password = ${password.value},
              phoneNumber = ${phoneNumber.value},
              dateOfBirth = ${dateOfBirth.value.asString},
              address = ${address.value},
              jobTitle = ${jobTitle.value},
              workplace = ${workplace.value},
              cause = ${cause.value},
              donationType = ${donationType.value},
              extraNotes = ${extraNotes.value}
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
              DELETE FROM donor
              WHERE id = ${id.value.get}
           """.update.apply()
      })
    else
      throw new Exception("Donor does not exist in database!")
    end if
  end delete

  def exists: Boolean = {
    id.value.isDefined && {
      DB.readOnly { implicit session =>
        sql"""
            SELECT * FROM donor WHERE
            id = ${id.value.get}
         """.map(x => x.int("id")).single.apply()
      } match
        case Some(_) => true
        case None    => false
    }
  }
  end exists

end Donor

object Donor extends Database:
  def apply(
             _id: Int,
             _fullName: String,
             _icNumber: String,
             _email: String,
             _password: String,
             _phoneNumber: String,
             _dateOfBirth: String,
             _address: String,
             _jobTitle: String,
             _workplace: String,
             _cause: String,
             _donationType: String,
             _extraNotes: String
           ): Donor =
    new Donor(_fullName, _icNumber, _email, _password):
      id.value = Some(_id)
      phoneNumber.value = _phoneNumber
      dateOfBirth.value = _dateOfBirth.parseLocalDate
      address.value = _address
      jobTitle.value = _jobTitle
      workplace.value = _workplace
      cause.value = _cause
      donationType.value = _donationType
      extraNotes.value = _extraNotes
  end apply
  def initializeTable() =
    DB.autoCommit { implicit session =>
      sql"""
            CREATE TABLE donor (
              id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
              fullName varchar(255),
              icNumber varchar(16),
              email varchar(64),
              password varchar(16),
              phoneNumber varchar(16),
              dateOfBirth varchar(64),
              address varchar(255),
              jobTitle varchar(64),
              workplace varchar(64),
              cause varchar(64),
              donationType varchar(64),
              extraNotes varchar(255)
            )
         """.execute.apply()
    }
  end initializeTable

  def getAllDonors: List[Donor] =
    DB.readOnly { implicit session =>
      sql"SELECT * FROM donor".map { x =>
        Donor(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"), x.string("address"),
          x.string("jobTitle"), x.string("workplace"), x.string("cause"),
          x.string("donationType"), x.string("extraNotes")
        )
      }.list.apply()
    }
    
  def getDonorById(donorId: Int): Option[Donor] =
    DB.readOnly { implicit session =>
      sql"SELECT * FROM donor WHERE id = ${donorId}".map { x =>
        Donor(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"), x.string("address"),
          x.string("jobTitle"), x.string("workplace"), x.string("cause"),
          x.string("donationType"), x.string("extraNotes")
        )
      }.single.apply()
    }