package nightyrelief.model

import scalafx.beans.property.{BooleanProperty, StringProperty, IntegerProperty}
import nightyrelief.util.Database
import nightyrelief.util.DateUtil.*
import scalikejdbc.*
import scala.util.Try

class Recipient(_fullName: String,
                _icNumber: String,
                _email: String,
                _password: String) extends User(_fullName,_icNumber, _email, _password) with Database:
  var address: StringProperty = StringProperty("")
  var householdSize: IntegerProperty = IntegerProperty(1)
  var occupation: StringProperty = StringProperty("")
  var income: IntegerProperty = IntegerProperty(0)
  var disability: BooleanProperty = BooleanProperty(false)
  var supervisorName: StringProperty = StringProperty("")
  var supervisorContact: StringProperty = StringProperty("")
  var extraNotes: StringProperty = StringProperty("")

  def save(): Try[Int] = {
    if (!exists) {
      Try(DB.autoCommit { implicit session =>
        val generatedId = sql"""
          INSERT INTO recipient (fullName, icNumber, email, password, phoneNumber, dateOfBirth, address,
          householdSize, occupation, income, disability, supervisorName, supervisorContact, extraNotes)
          VALUES (${fullName.value}, ${icNumber.value}, ${email.value}, ${password.value}, ${phoneNumber.value},
          ${dateOfBirth.value.asString}, ${address.value}, ${householdSize.value},
          ${occupation.value}, ${income.value}, ${disability.value},
          ${supervisorName.value}, ${supervisorContact.value}, ${extraNotes.value})
        """.updateAndReturnGeneratedKey.apply()
        this.id.value = Some(generatedId.toInt)
        generatedId.toInt
      })
    } else
      Try(DB.autoCommit { implicit session =>
        sql"""
              UPDATE recipient
              SET
              fullName = ${fullName.value},
              icNumber = ${icNumber.value},
              email = ${email.value},
              password = ${password.value},
              phoneNumber = ${phoneNumber.value},
              dateOfBirth = ${dateOfBirth.value.asString},
              address = ${address.value},
              householdSize = ${householdSize.value},
              occupation = ${occupation.value},
              income = ${income.value},
              disability = ${disability.value},
              supervisorName = ${supervisorName.value},
              supervisorContact = ${supervisorContact.value},
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
              DELETE FROM recipient
              WHERE id = ${id.value.get}
           """.update.apply()
      })
    else
      throw new Exception("Recipient does not exist in database!")
    end if
  end delete

  def exists: Boolean = {
    id.value.isDefined && {
      DB.readOnly { implicit session =>
        sql"""
            SELECT * FROM recipient WHERE
            id = ${id.value.get}
         """.map(x => x.int("id")).single.apply()
      } match
        case Some(_) => true
        case None    => false
    }
  }
  end exists

end Recipient

object Recipient extends Database:
  def apply(
             _id: Int,
             _fullName: String,
             _icNumber: String,
             _email: String,
             _password: String,
             _phoneNumber: String,
             _dateOfBirth: String,
             _address: String,
             _householdSize: Int,
             _occupation: String,
             _income: Int,
             _disability: Boolean,
             _supervisorName: String,
             _supervisorContact: String,
             _extraNotes: String
           ): Recipient =
    new Recipient(_fullName, _icNumber, _email, _password):
      id.value = Some(_id)
      phoneNumber.value = _phoneNumber
      dateOfBirth.value = _dateOfBirth.parseLocalDate
      address.value = _address
      householdSize.value = _householdSize
      occupation.value = _occupation
      income.value = _income
      disability.value = _disability
      supervisorName.value = _supervisorName
      supervisorContact.value = _supervisorContact
      extraNotes.value = _extraNotes
  end apply

  def initializeTable() =
    DB.autoCommit { implicit session =>
      sql"""
            CREATE TABLE recipient (
              id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
              fullName varchar(255),
              icNumber varchar(16),
              email varchar(64),
              password varchar(16),
              phoneNumber varchar(16),
              dateOfBirth varchar(64),
              address varchar(1024),
              householdSize int,
              occupation varchar(255),
              income int,
              disability boolean,
              supervisorName varchar(255),
              supervisorContact varchar(16),
              extraNotes varchar(255)
            )
         """.execute.apply()
    }
  end initializeTable

  def getAllRecipients: List[Recipient] =
    DB.readOnly { implicit session =>
      sql"SELECT * FROM recipient".map { x =>
        Recipient(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"), x.string("address"),
          x.int("householdSize"), x.string("occupation"), x.int("income"), x.boolean("disability"),
          x.string("supervisorName"), x.string("supervisorContact"), x.string("extraNotes")
        )
      }.list.apply()
    }
  end getAllRecipients
  
  def getRecipientById(id: Int): Option[Recipient] =
    DB.readOnly { implicit session =>
      sql"SELECT * FROM recipient WHERE id = $id".map { x =>
        Recipient(
          x.int("id"),
          x.string("fullName"), x.string("icNumber"), x.string("email"), x.string("password"),
          x.string("phoneNumber"), x.string("dateOfBirth"), x.string("address"),
          x.int("householdSize"), x.string("occupation"), x.int("income"), x.boolean("disability"),
          x.string("supervisorName"), x.string("supervisorContact"), x.string("extraNotes")
        )
      }.single.apply()
    }
  end getRecipientById