package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.{Aid, Donor, Recipient}
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import scalafx.Includes.*
import scalafx.beans.binding.Bindings
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

import scala.util.{Failure, Success}

class AdminAidController {
  @FXML private var aidTableView: TableView[Aid] = null
  @FXML private var userTypeTableColumn: TableColumn[Aid, String] = null
  @FXML private var aidStatusTableColumn: TableColumn[Aid, String] = null

  @FXML private var userTypeLabel: Label = null
  @FXML private var userIDLabel: Label = null
  @FXML private var aidStatusLabel: Label = null
  @FXML private var aidDateLabel: Label = null
  @FXML private var itemLabel: Label = null
  @FXML private var amountLabel: Label = null

  @FXML private var fullNameLabel: Label = null
  @FXML private var icNumberLabel: Label = null
  @FXML private var emailLabel: Label = null
  @FXML private var phoneNumberLabel: Label = null
  @FXML private var addressLabel: Label = null

  @FXML
  def initialize(): Unit = {
    aidTableView.items = nightyrelief.MainApp.aidData
    userTypeTableColumn.cellValueFactory = _.value.userType
    aidStatusTableColumn.cellValueFactory = _.value.aidStatus

    showAidDetails(None)
    aidTableView.selectionModel().selectedItem.onChange(
      (_, _, newAid) => showAidDetails(Option(newAid))
    )
  }

  private def showAidDetails(aidOption: Option[Aid]): Unit = {
    aidOption match {
      case Some(aid) =>
        // Bind aid details
        userTypeLabel.text <== aid.userType
        userIDLabel.text <== aid.userId.asString
        aidStatusLabel.text <== aid.aidStatus
        aidDateLabel.text <== aid.aidDate.asString
        itemLabel.text <== aid.item
        amountLabel.text <== Bindings.createStringBinding(() => f"RM ${aid.amount.value}%.2f", aid.amount)

        // Fetch and display user details based on userType
        if (aid.userType.value == "Recipient") {
          Recipient.getRecipientById(aid.userId.value) match {
            case Some(recipient) =>
              fullNameLabel.text = recipient.fullName.value
              icNumberLabel.text = recipient.icNumber.value
              emailLabel.text = recipient.email.value
              phoneNumberLabel.text = recipient.phoneNumber.value
              addressLabel.text = recipient.address.value
            case None => clearUserLabels()
          }
        } else if (aid.userType.value == "Farmer") {
          nightyrelief.model.Farmer.getFarmerById(aid.userId.value) match {
            case Some(farmer) =>
              fullNameLabel.text = farmer.fullName.value
              icNumberLabel.text = farmer.icNumber.value
              emailLabel.text = farmer.email.value
              phoneNumberLabel.text = farmer.phoneNumber.value
              addressLabel.text = farmer.farmLocation.value
            case None => clearUserLabels()
          }
        } else if (aid.userType.value == "Donor") {
          Donor.getDonorById(aid.userId.value) match {
            case Some(donor) =>
              fullNameLabel.text = donor.fullName.value
              icNumberLabel.text = donor.icNumber.value
              emailLabel.text = donor.email.value
              phoneNumberLabel.text = donor.phoneNumber.value
              addressLabel.text = donor.address.value
            case None => clearUserLabels()
          }
        } else {
          clearUserLabels()
        }

      case None =>
        // Clear all labels if no aid is selected
        userTypeLabel.text = ""
        userIDLabel.text = ""
        aidStatusLabel.text = ""
        aidDateLabel.text = ""
        itemLabel.text = ""
        amountLabel.text = ""
        clearUserLabels()
    }
  }

  private def clearUserLabels(): Unit = {
    fullNameLabel.text = ""
    icNumberLabel.text = ""
    emailLabel.text = ""
    phoneNumberLabel.text = ""
    addressLabel.text = ""
  }

  @FXML
  def handleNew(action: ActionEvent): Unit = {
    val aid = new Aid()
    val okClicked = MainApp.showAidEditDialog(aid)
    if (okClicked) {
      aid.save() match {
        case Success(_) =>
          MainApp.aidData += aid
          new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title = "Aid Record Created"
            headerText = "New Aid Record Created Successfully"
            contentText = s"A new aid record for user ID ${aid.userId.value} has been created."
          }.showAndWait()
        case Failure(exception) =>
          new Alert(AlertType.Error) {
            initOwner(MainApp.stage)
            title = "Database Error"
            headerText = "Failed to Create Aid Record"
            contentText = s"Could not save the aid record to the database.\nError: ${exception.getMessage}"
          }.showAndWait()
      }
    }
  }

  @FXML
  def handleEdit(action: ActionEvent): Unit = {
    val selectedAid = aidTableView.selectionModel().selectedItem.value
    if (selectedAid != null) {
      val okClicked = MainApp.showAidEditDialog(selectedAid)
      if (okClicked) {
        selectedAid.save() match {
          case Success(_) => showAidDetails(Some(selectedAid))
          case Failure(exception) =>
            new Alert(AlertType.Error) {
              initOwner(MainApp.stage)
              title = "Database Error"
              headerText = "Failed to Update Aid Record"
              contentText = s"Could not update the aid record in the database.\nError: ${exception.getMessage}"
            }.showAndWait()
        }
      }
    } else {
      new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Aid Record Selected"
        contentText = "Please select an aid record in the table to edit."
      }.showAndWait()
    }
  }

  @FXML
  def handleDelete(action: ActionEvent): Unit = {
    val selectedIndex = aidTableView.selectionModel().selectedIndex.value
    val selectedAid = aidTableView.selectionModel().selectedItem.value
    if (selectedIndex >= 0) {
      selectedAid.delete() match {
        case Success(_) =>
          aidTableView.items().remove(selectedIndex)
        case Failure(exception) =>
          new Alert(AlertType.Error) {
            initOwner(MainApp.stage)
            title = "Database Error"
            headerText = "Failed to Delete Aid Record"
            contentText = s"Could not delete the aid record from the database.\nError: ${exception.getMessage}"
          }.showAndWait()
      }
    } else {
      new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Aid Record Selected"
        contentText = "Please select an aid record in the table to delete."
      }.showAndWait()
    }
  }

  @FXML
  def handleLogout(): Unit = {
    MainApp.showWelcome()
  }

  @FXML
  def handleRecipient(): Unit = {
    MainApp.showAdminRecipient()
  }

  @FXML
  def handleFarmer(): Unit = {
    MainApp.showAdminFarmer()
  }

  @FXML
  def handleDonor(): Unit = {
    MainApp.showAdminDonor()
  }
}