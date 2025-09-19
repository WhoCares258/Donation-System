package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.Farmer
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
class AdminFarmerController {
  @FXML
  private var farmerTableView: TableView[Farmer] = null
  @FXML
  private var farmNameTableColumn: TableColumn[Farmer, String] = null

  // Labels for displaying farmer details
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
  private var farmNameLabel: Label = null
  @FXML
  private var farmLocationLabel: Label = null
  @FXML
  private var farmSizeLabel: Label = null
  @FXML
  private var farmEstablishedDateLabel: Label = null
  @FXML
  private var employeeCountLabel: Label = null
  @FXML
  private var farmDetailsLabel: Label = null

  @FXML
  def initialize(): Unit = {
    farmerTableView.items = MainApp.farmerData
    farmNameTableColumn.cellValueFactory = _.value.fullName

    showFarmerDetails(None)
    farmerTableView.selectionModel().selectedItem.onChange(
      (_, _, newFarmer) => showFarmerDetails(Option(newFarmer))
    )
  }

  private def showFarmerDetails(farmer: Option[Farmer]): Unit = {
    farmer match {
      case Some(farmer) =>
        fullNameLabel.text <== farmer.fullName
        icNumberLabel.text <== farmer.icNumber
        emailLabel.text <== farmer.email
        passwordLabel.text <== farmer.password
        phoneNumberLabel.text <== farmer.phoneNumber
        dateOfBirthLabel.text <== Bindings.createStringBinding(
          () => farmer.dateOfBirth.value.asString,
          farmer.dateOfBirth
        )
        farmNameLabel.text <== farmer.farmName
        farmLocationLabel.text <== farmer.farmLocation
        farmSizeLabel.text <== farmer.farmSize.asString()
        farmEstablishedDateLabel.text <== Bindings.createStringBinding(
          () => farmer.farmEstablishedDate.value.asString,
          farmer.farmEstablishedDate
        )
        employeeCountLabel.text <== farmer.numberOfEmployees.asString()
        farmDetailsLabel.text <== farmer.farmDetails
      case None =>
        fullNameLabel.text.unbind()
        icNumberLabel.text.unbind()
        emailLabel.text.unbind()
        passwordLabel.text.unbind()
        phoneNumberLabel.text.unbind()
        dateOfBirthLabel.text.unbind()
        farmNameLabel.text.unbind()
        farmLocationLabel.text.unbind()
        farmSizeLabel.text.unbind()
        farmEstablishedDateLabel.text.unbind()
        employeeCountLabel.text.unbind()
        farmDetailsLabel.text.unbind()

        fullNameLabel.text = ""
        icNumberLabel.text = ""
        emailLabel.text = ""
        passwordLabel.text = ""
        phoneNumberLabel.text = ""
        dateOfBirthLabel.text = ""
        farmNameLabel.text = ""
        farmLocationLabel.text = ""
        farmSizeLabel.text = ""
        farmEstablishedDateLabel.text = ""
        employeeCountLabel.text = ""
        farmDetailsLabel.text = ""
    }
  }

  def handleLogout(): Unit =
    MainApp.showWelcome()

  @FXML
  def handleDelete(action: ActionEvent): Unit =
    val selectedIndex = farmerTableView.selectionModel().selectedIndex.value
    val selectedFarmer = farmerTableView.selectionModel().selectedItem.value
    if (selectedIndex >= 0) then
      selectedFarmer.delete() match
        case Success(x) => farmerTableView.items().remove(selectedIndex)
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Delete Farmer"
            headerText = "Database Error"
            contentText = "Database error: failed to delete farmer.\n" +
              s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Farmer Selected"
        contentText = "Please select a farmer in the table."
      noSelectionAlert.showAndWait()

  @FXML
  def handleNew(action: ActionEvent): Unit =
    val farmer= new Farmer(null, null, null, null)
    val okClicked = MainApp.showFarmerEditDialog(farmer)
    if (okClicked) {
      farmer.save() match {
        case Success(x) => 
          MainApp.farmerData += farmer
          val alert = new Alert(AlertType.Information):
            initOwner(MainApp.stage)
            title = "Farmer Created"
            headerText = "New Farmer Created"
            contentText = s"Farmer ${farmer.fullName.value} has been created successfully."
          alert.showAndWait()
        case Failure(exception) =>
          val dbAlert = new Alert(AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database error: failed to create farmer.\n" +
              s"Error message: ${exception.getMessage}"
          dbAlert.showAndWait()
      }
    }
    else
      val alert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Farmer Created"
        headerText = "Farmer Creation Cancelled"
        contentText = "No farmer was created. Please try again."
      alert.showAndWait()

  @FXML
  def handleEdit(action: ActionEvent): Unit =
    val selectedFarmer = farmerTableView.selectionModel().selectedItem.value
    if (selectedFarmer != null) then
      val okClicked = MainApp.showFarmerEditDialog(selectedFarmer)
      if okClicked then
        selectedFarmer.save() match
          case Success(x) => showFarmerDetails(Some(selectedFarmer))
          case Failure(exception) =>
            val dbAlert = new Alert(AlertType.Warning):
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update farmer.\n" +
                s"Error message: ${exception.getMessage}"
            dbAlert.showAndWait()
    else
      val noSelectionAlert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Farmer Selected"
        contentText = "Please select a Farmer in the table."
      noSelectionAlert.showAndWait()
      
  @FXML    
  def handleAidHistory(action: ActionEvent): Unit =
    val selectedFarmer = farmerTableView.selectionModel().selectedItem.value

    if (selectedFarmer == null) {
      val noSelectionAlert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Farmer Selected"
        contentText = "Please select a farmer in the table."
      }
      noSelectionAlert.showAndWait()
    } else {
      FarmerController().showFarmerHistory(
        Aid.getAllUserAid(
          "Farmer",
          selectedFarmer.id.value.get
        )
      )
    }
  
  @FXML
  def handleRecipient(): Unit =
    MainApp.showAdminRecipient()
    
  @FXML  
  def handleDonor(): Unit =
    MainApp.showAdminDonor()
    
  @FXML  
  def handleAid(): Unit =
    MainApp.showAdminAid()
}