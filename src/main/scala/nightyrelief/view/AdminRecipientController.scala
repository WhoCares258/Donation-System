package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.Recipient
import nightyrelief.util.DateUtil.asString
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes.*
import javafx.event.ActionEvent
import scalafx.beans.binding.Bindings
import scala.util.{Failure, Success}
import nightyrelief.model.Aid

@FXML
class AdminRecipientController {
  @FXML
  private var recipientTableView: TableView[Recipient] = null
  @FXML
  private var fullNameTableColumn: TableColumn[Recipient, String] = null

  // Labels for displaying recipient details
  @FXML
  private var fullNameLabel: Label = null
  @FXML
  private var icNumberLabel: Label = null
  @FXML
  private var emailLabel: Label = null
  @FXML
  private var passwordLabel: Label = null
  @FXML
  private var phoneNumberLabel: Label = null
  @FXML
  private var dateOfBirthLabel: Label = null
  @FXML
  private var addressLabel: Label = null
  @FXML
  private var householdSizeLabel: Label = null
  @FXML
  private var occupationLabel: Label = null
  @FXML
  private var incomeLabel: Label = null
  @FXML
  private var disabilityLabel: Label = null
  @FXML
  private var supervisorNameLabel: Label = null
  @FXML
  private var supervisorContactLabel: Label = null
  @FXML
  private var extraNotesLabel: Label = null

  @FXML
  def initialize(): Unit = {
    recipientTableView.items = MainApp.recipientData
    fullNameTableColumn.cellValueFactory = _.value.fullName

    showRecipientDetails(None)
    recipientTableView.selectionModel().selectedItem.onChange(
      (_, _, newRecipient) => showRecipientDetails(Option(newRecipient))
    )
  }

  def showRecipientDetails(recipient: Option[Recipient]): Unit = {
    recipient match {
      case Some(recipient) =>
        fullNameLabel.text <== recipient.fullName
        icNumberLabel.text <== recipient.icNumber
        emailLabel.text <== recipient.email
        passwordLabel.text <== recipient.password
        phoneNumberLabel.text <== recipient.phoneNumber
        dateOfBirthLabel.text <== Bindings.createStringBinding(
          () => recipient.dateOfBirth.value.asString,
          recipient.dateOfBirth
        )
        addressLabel.text <== recipient.address
        householdSizeLabel.text <== recipient.householdSize.delegate.asString()
        occupationLabel.text <== recipient.occupation
        incomeLabel.text <== recipient.income.asString()
        disabilityLabel.text <== recipient.disability.delegate.asString()
        supervisorNameLabel.text <== recipient.supervisorName
        supervisorContactLabel.text <== recipient.supervisorContact
        extraNotesLabel.text <== recipient.extraNotes

      case None =>
        fullNameLabel.text.unbind()
        icNumberLabel.text.unbind()
        emailLabel.text.unbind()
        passwordLabel.text.unbind()
        phoneNumberLabel.text.unbind()
        dateOfBirthLabel.text.unbind()
        addressLabel.text.unbind()
        householdSizeLabel.text.unbind()
        occupationLabel.text.unbind()
        incomeLabel.text.unbind()
        disabilityLabel.text.unbind()
        supervisorNameLabel.text.unbind()
        supervisorContactLabel.text.unbind()
        extraNotesLabel.text.unbind()

        fullNameLabel.text = ""
        icNumberLabel.text = ""
        emailLabel.text = ""
        passwordLabel.text = ""
        phoneNumberLabel.text = ""
        dateOfBirthLabel.text = ""
        addressLabel.text = ""
        householdSizeLabel.text = ""
        occupationLabel.text = ""
        incomeLabel.text = ""
        disabilityLabel.text = ""
        supervisorNameLabel.text = ""
        supervisorContactLabel.text = ""
        extraNotesLabel.text = ""
    }
  }

  def handleLogout(): Unit =
    MainApp.showWelcome()

  @FXML
  def handleDelete(action: ActionEvent): Unit =
    val selectedIndex = recipientTableView.selectionModel().selectedIndex.value
    val selectedRecipient = recipientTableView.selectionModel().selectedItem.value
    if (selectedIndex >= 0) then
      selectedRecipient.delete() match
        case Success(x) => recipientTableView.items().remove(selectedIndex)
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Delete Recipient"
            headerText = "Database Error"
            contentText = "Database error: failed to delete recipient.\n" +
                          s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Recipient Selected"
        contentText = "Please select a recipient in the table."
      noSelectionAlert.showAndWait()

  @FXML
  def handleNew(action: ActionEvent): Unit =
    val recipient= new Recipient(null, null, null, null)
    val okClicked = MainApp.showRecipientEditDialog(recipient)
    if (okClicked) {
      recipient.save() match {
        case Success(x) => 
          MainApp.recipientData += recipient
          val alert = new Alert(AlertType.Confirmation):
            initOwner(MainApp.stage)
            title = "Recipient Created"
            headerText = "New Recipient Created"
            contentText = s"Recipient ${recipient.fullName.value} has been created successfully."
          alert.showAndWait()  
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database error: failed to create recipient.\n" +
                          s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
      }
    }
    else
      val alert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Recipient Created"
        headerText = "Recipient Creation Cancelled"
        contentText = "No recipient was created. Please try again."
      alert.showAndWait()

  @FXML
  def handleEdit(action: ActionEvent): Unit =
    val selectedRecipient = recipientTableView.selectionModel().selectedItem.value
    if (selectedRecipient != null) then
      val okClicked = MainApp.showRecipientEditDialog(selectedRecipient)
      if okClicked then
        selectedRecipient.save() match
          case Success(x) => showRecipientDetails(Some(selectedRecipient))
          case Failure(exception) =>
            val dbAlert = new Alert(AlertType.Warning):
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update recipient.\n" +
                            s"Error message: ${exception.getMessage}"
            dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Recipient Selected"
        contentText = "Please select a recipient in the table."
      noSelectionAlert.showAndWait()

  @FXML
  def handleAidHistory(action: ActionEvent): Unit = {
    val selectedRecipient = recipientTableView.selectionModel().selectedItem.value

    if (selectedRecipient == null) {
      val noSelectionAlert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Recipient Selected"
        contentText = "Please select a recipient in the table."
      }
      noSelectionAlert.showAndWait()
    } else {
      RecipientController().showRecipientHistory(
        Aid.getAllUserAid(
          "Recipient",
          selectedRecipient.id.value.get
        ),
        selectedRecipient.householdSize.value
      )
    }

  }

  @FXML
  def handleFarmer(action: ActionEvent): Unit =
    MainApp.showAdminFarmer()
    
  @FXML  
  def handleDonor(action: ActionEvent): Unit =
    MainApp.showAdminDonor()
  
  @FXML
  def handleAid(action: ActionEvent): Unit =
    MainApp.showAdminAid()

}