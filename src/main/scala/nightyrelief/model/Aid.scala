package nightyrelief.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import nightyrelief.util.Database
import nightyrelief.util.DateUtil.*
import scalikejdbc.*

import scala.language.postfixOps
import scala.util.Try

class Aid(var _userType: String, var _userId: Int):
  def this() = this("", 0)
  var aidId: ObjectProperty[Option[Int]] = ObjectProperty[Option[Int]](None)
  var userType: StringProperty = StringProperty(_userType)
  var userId: ObjectProperty[Int] = ObjectProperty[Int](_userId)
  var item: StringProperty = StringProperty("")
  var amount: IntegerProperty = IntegerProperty(0)
  var aidDate: ObjectProperty[java.time.LocalDate] = ObjectProperty[java.time.LocalDate](java.time.LocalDate.now())
  var aidStatus: StringProperty = StringProperty("Pending")

  def save(): Try[Int] = {
    if (!exists) {
      Try(DB.autoCommit { implicit session =>
        val generatedId =
          sql"""
            INSERT INTO aid (userType, userId, item, amount, aidDate, aidStatus)
            VALUES (${userType.value}, ${userId.value}, ${item.value}, ${amount.value},
             ${aidDate.value.asString}, ${aidStatus.value})
          """.updateAndReturnGeneratedKey.apply()
        this.aidId.value = Some(generatedId.toInt)
        generatedId.toInt
      })
    } else
      Try(DB.autoCommit { implicit session =>
        sql"""
                UPDATE aid
                SET
                userType = ${userType.value},
                userId = ${userId.value},
                item = ${item.value},
                amount = ${amount.value},
                aidDate = ${aidDate.value.asString},
                aidStatus = ${aidStatus.value}
                WHERE aidId = ${aidId.value.get}
             """.update.apply()
      })
    end if
  }
  end save

  def delete(): Try[Int] =
    if (exists) then
      Try(DB.autoCommit { implicit session =>
        sql"""
                DELETE FROM aid
                WHERE aidId = ${aidId.value.get}
             """.update.apply()
      })
    else
      throw new Exception("Aid does not exist in database!")
    end if
  end delete

  def exists: Boolean = {
    aidId.value.isDefined && {
      DB.readOnly { implicit session =>
        sql"""
        SELECT aidId FROM aid WHERE aidId = ${aidId.value.get}
      """.map(_.int("aidId")).single.apply()
      }.isDefined
    }
  }
  end exists

object Aid extends Database:
  def apply(
    _aidId: Int, _userType: String, _userId: Int, _item: String, _amount: Int,
    _aidDate: String, _aidStatus: String
  ): Aid =
    new Aid(_userType, _userId):
      aidId.value = Some(_aidId)
      item.value = _item
      amount.value = _amount
      aidDate.value = _aidDate.parseLocalDate
      aidStatus.value = _aidStatus

  def initializeTable() =
    DB.autoCommit { implicit session =>
      sql"""
            CREATE TABLE aid (
              aidID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
              userType varchar(50) NOT NULL,
              userId int NOT NULL,
              item varchar(255) NOT NULL,
              amount int NOT NULL,
              aidDate date NOT NULL,
              aidStatus varchar(50) NOT NULL
            )
         """.execute.apply()
    }
  end initializeTable

  def getAllAids: List[Aid] =
    DB.readOnly { implicit session =>
      sql"""
      SELECT * 
      FROM aid
      ORDER BY CASE aidStatus
        WHEN 'Requested' THEN 1
        WHEN 'Pending'   THEN 2
        WHEN 'Approved'  THEN 3
        WHEN 'Sent'      THEN 4
        WHEN 'Delivered' THEN 5
        WHEN 'Received'  THEN 6
        WHEN 'Completed' THEN 7
        ELSE 8
      END, aidDate DESC
    """.map { x =>
        Aid(
          x.int("aidID"),
          x.string("userType"),
          x.int("userId"),
          x.string("item"),
          x.int("amount"),
          x.string("aidDate"),
          x.string("aidStatus")
        )
      }.list.apply()
    }

  def getAllUserAid(userType: String, userId: Int): List[Aid] =
    DB.readOnly { implicit session =>
      sql"""
        SELECT * FROM aid WHERE userType = ${userType} AND userId = ${userId}
        ORDER BY CASE aidStatus
          WHEN 'Requested' THEN 1
          WHEN 'Pending'   THEN 2
          WHEN 'Approved'  THEN 3
          WHEN 'Sent'      THEN 4
          WHEN 'Delivered' THEN 5
          WHEN 'Received'  THEN 6
          WHEN 'Completed' THEN 7
          ELSE 8
        END, aidDate DESC
      """.map { x =>
        Aid(
          x.int("aidID"),
          x.string("userType"),
          x.int("userId"),
          x.string("item"),
          x.int("amount"),
          x.string("aidDate"),
          x.string("aidStatus")
        )
      }.list.apply()
    }
  end getAllUserAid

  def getAidCountByMonthAndYear(month: Int, year: Int): (Int, Int, Int) = {
    DB.readOnly { implicit session =>
      sql"""
      SELECT
        COALESCE(SUM(CASE WHEN userType = 'Recipient' THEN 1 ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN userType = 'Farmer' THEN 1 ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN userType = 'Donor' THEN 1 ELSE 0 END), 0)
      FROM aid
      WHERE MONTH(aidDate) = ${month} AND YEAR(aidDate) = ${year}
    """.map(rs => (rs.int(1), rs.int(2), rs.int(3))).single.apply().getOrElse((0, 0, 0))
    }
  }
