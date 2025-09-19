package nightyrelief.view

import nightyrelief.MainApp
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button

class WelcomeController():

  @FXML
  def handleRecipient(action: ActionEvent): Unit = {
    MainApp.showLogin("Recipient")
  }

  @FXML
  def handleFarmer(action: ActionEvent): Unit = {
    MainApp.showLogin("Farmer")
  }

  @FXML
  def handleDonor(action: ActionEvent): Unit = {
    MainApp.showLogin("Donor")
  }

  @FXML
  def handleAdmin(action: ActionEvent): Unit = {
    MainApp.showLogin("Admin")
  }