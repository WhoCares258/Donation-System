package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.Donor
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
class AdminDonorController {
  @FXML
  private var donorTableView: TableView[Donor] = null
  @FXML
  private var fullNameTableColumn: TableColumn[Donor, String] = null

  // Labels for displaying donor details
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
  private var jobTitleLabel: Label = null
  @FXML
  private var workplaceLabel: Label = null
  @FXML
  private var causeLabel: Label = null
  @FXML
  private var donationTypeLabel: Label = null
  @FXML
  private var extraNotesLabel: Label = null
  
  @FXML
  def initialize(): Unit = {
    donorTableView.items = MainApp.donorData
    fullNameTableColumn.cellValueFactory = _.value.fullName

    showDonorDetails(None)
    donorTableView.selectionModel().selectedItem.onChange(
      (_, _, newDonor) => showDonorDetails(Option(newDonor))
    )
  }

  private def showDonorDetails(donor: Option[Donor]): Unit = {
    donor match {
      case Some(donor) =>
        fullNameLabel.text <== donor.fullName
        icNumberLabel.text <== donor.icNumber
        emailLabel.text <== donor.email
        passwordLabel.text <== donor.password
        phoneNumberLabel.text <== donor.phoneNumber
        dateOfBirthLabel.text <== Bindings.createStringBinding(
          () => donor.dateOfBirth.value.asString,
          donor.dateOfBirth
        )
        addressLabel.text <== donor.address
        jobTitleLabel.text <== donor.jobTitle
        workplaceLabel.text <== donor.workplace
        causeLabel.text <== donor.cause
        donationTypeLabel.text <== donor.donationType
        extraNotesLabel.text <== donor.extraNotes
      case None =>
        fullNameLabel.text.unbind()
        icNumberLabel.text.unbind()
        emailLabel.text.unbind()
        passwordLabel.text.unbind()
        phoneNumberLabel.text.unbind()
        dateOfBirthLabel.text.unbind()
        addressLabel.text.unbind()
        jobTitleLabel.text.unbind()
        workplaceLabel.text.unbind()
        causeLabel.text.unbind()
        donationTypeLabel.text.unbind()
        extraNotesLabel.text.unbind()

        fullNameLabel.text = ""
        icNumberLabel.text = ""
        emailLabel.text = ""
        passwordLabel.text = ""
        phoneNumberLabel.text = ""
        dateOfBirthLabel.text = ""
        addressLabel.text = ""
        jobTitleLabel.text = ""
        workplaceLabel.text = ""
        causeLabel.text = ""
        donationTypeLabel.text = ""
        extraNotesLabel.text = ""
    }
  }

  def handleLogout(): Unit =
    MainApp.showWelcome()

  @FXML
  def handleDelete(action: ActionEvent): Unit =
    val selectedIndex = donorTableView.selectionModel().selectedIndex.value
    val selectedDonor = donorTableView.selectionModel().selectedItem.value
    if (selectedIndex >= 0) then
      selectedDonor.delete() match
        case Success(x) => donorTableView.items().remove(selectedIndex)
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Delete Donor"
            headerText = "Database Error"
            contentText = "Database error: failed to delete donor.\n" +
              s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Donor Selected"
        contentText = "Please select a donor in the table."
      noSelectionAlert.showAndWait()

  @FXML
  def handleNew(action: ActionEvent): Unit =
    val donor= new Donor(null, null, null, null)
    val okClicked = MainApp.showDonorEditDialog(donor)
    if (okClicked) {
      donor.save() match {
        case Success(x) => 
          MainApp.donorData += donor
          val alert = new Alert(AlertType.Information):
            initOwner(MainApp.stage)
            title = "Donor Created"
            headerText = "New Donor Created"
            contentText = s"Donor ${donor.fullName.value} has been created successfully."
          alert.showAndWait()
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database error: failed to create donor.\n" +
              s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
      }
    }
    else
      val alert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Donor Created"
        headerText = "Donor Creation Cancelled"
        contentText = "No donor was created. Please try again."
      alert.showAndWait()

  @FXML
  def handleEdit(action: ActionEvent): Unit =
    val selectedDonor = donorTableView.selectionModel().selectedItem.value
    if (selectedDonor != null) then
      val okClicked = MainApp.showDonorEditDialog(selectedDonor)
      if okClicked then
        selectedDonor.save() match
          case Success(x) => showDonorDetails(Some(selectedDonor))
          case Failure(exception) =>
            val dbAlert = new Alert(AlertType.Warning):
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update donor.\n" +
                s"Error message: ${exception.getMessage}"
            dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Donor Selected"
        contentText = "Please select a Donor in the table."
      noSelectionAlert.showAndWait()
      
  @FXML    
  def handleAidHistory(): Unit =
    val selectedDonor = donorTableView.selectionModel().selectedItem.value

    if (selectedDonor == null) {
      val noSelectionAlert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Donor Selected"
        contentText = "Please select a donor in the table."
      }
      noSelectionAlert.showAndWait()
    } else {
      DonorController().showDonorHistory(
        Aid.getAllUserAid(
          "Donor",
          selectedDonor.id.value.get
        )
      )
    }
    
  @FXML
  def handleRecipient(): Unit =
    MainApp.showAdminRecipient()

  @FXML
  def handleFarmer(): Unit =
    MainApp.showAdminFarmer()
    
  @FXML  
  def handleAid(): Unit =
    MainApp.showAdminAid()
}