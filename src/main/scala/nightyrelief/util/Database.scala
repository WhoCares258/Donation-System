package nightyrelief.util

import scalikejdbc.*
import nightyrelief.model.{Farmer, Recipient}

trait Database:
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val derbyUrl = "jdbc:derby:myDB;create=true"

  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(derbyUrl, "me", "mine")

  given AutoSession = AutoSession

object Database extends Database:
  def setupDB(): Unit =
    if (DB.getTable("RECIPIENT").isEmpty) {
      Recipient.initializeTable()
    }
    if (DB.getTable("FARMER").isEmpty) {
      Farmer.initializeTable()
    }
    if (DB.getTable("DONOR").isEmpty) {
      nightyrelief.model.Donor.initializeTable()
    }
    if (DB.getTable("AID").isEmpty) {
      nightyrelief.model.Aid.initializeTable()
    }