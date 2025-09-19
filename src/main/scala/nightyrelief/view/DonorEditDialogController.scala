package nightyrelief.view

import nightyrelief.model.Donor
import nightyrelief.util.DateUtil.*
import javafx.scene.control.{TextField, TextArea}
import scalafx.scene.control.Alert
import scalafx.stage.Stage
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.event.ActionEvent

@FXML
class DonorEditDialogController {

  @FXML private var fullNameField: TextField = null
  @FXML private var icNumberField: TextField = null
  @FXML private var emailField: TextField = null
  @FXML private var passwordField: TextField = null
  @FXML private var phoneNumberField: TextField = null
  @FXML private var dateOfBirthField: TextField = null
  @FXML private var addressField: TextArea = null
  @FXML private var jobTitleField: TextField = null
  @FXML private var workplaceField: TextField = null
  @FXML private var causeField: TextField = null
  @FXML private var donationTypeField: TextField = null
  @FXML private var extraNotesField: TextArea = null

  var dialogStage: Stage = null
  private var __donor: Donor = null
  var okClicked: Boolean = false

  def donor: Donor = __donor
  def donor_=(x: Donor): Unit = {
    __donor = x
    // Populate fields from donor object
    if (__donor != null) {
      fullNameField.text = __donor.fullName.value
      icNumberField.text = __donor.icNumber.value
      emailField.text = __donor.email.value
      passwordField.text = __donor.password.value
      phoneNumberField.text = __donor.phoneNumber.value
      dateOfBirthField.text = __donor.dateOfBirth.value.asString
      addressField.text = __donor.address.value
      jobTitleField.text = __donor.jobTitle.value
      workplaceField.text = __donor.workplace.value
      causeField.text = __donor.cause.value
      donationTypeField.text = __donor.donationType.value
      extraNotesField.text = __donor.extraNotes.value
    }
  }

  @FXML
  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      __donor.fullName <== fullNameField.text
      __donor.icNumber <== icNumberField.text
      __donor.email <== emailField.text
      __donor.password <== passwordField.text
      __donor.phoneNumber <== phoneNumberField.text
      __donor.dateOfBirth.value = dateOfBirthField.text.value.parseLocalDate
      __donor.address <== addressField.text 
      __donor.jobTitle <== jobTitleField.text
      __donor.workplace <== workplaceField.text
      __donor.cause <== causeField.text
      __donor.donationType <== donationTypeField.text
      __donor.extraNotes <== extraNotesField.text 

      okClicked = true
      dialogStage.close()
    }
  }

  @FXML
  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }

  private def nullChecking(x: String): Boolean = x == null || x.isEmpty

  private def isInputValid(): Boolean = {
    var errorMessage = ""

    val fullName = fullNameField.getText
    val email = emailField.getText
    val ic = icNumberField.getText
    val password = passwordField.getText
    val phone = phoneNumberField.getText
    val dob = dateOfBirthField.text.value
    val address = addressField.getText

    // Full name
    if (nullChecking(fullName))
      errorMessage += "No valid full name!\n"

    // Email
    if (nullChecking(email)) {
      errorMessage += "No valid email!\n"
    } else {
      val parts = email.split("@")
      if (parts.length != 2 || parts(0).isEmpty || parts(1).isEmpty || !parts(1).contains("."))
        errorMessage += "Not valid email! It should contain '@' and '.'\n"
    }

    // IC Number
    if (nullChecking(ic)) {
      errorMessage += "No valid IC number!\n"
    } else if (!ic.matches("""\d{6}-\d{2}-\d{4}""")) { //ChatGPT assisted regex
      errorMessage += "Not a valid IC Number! Use Format 000000-00-0000\n"
    }

    // Password
    if (nullChecking(password)) {
      errorMessage += "No valid password!\n"
    } else if (password.length < 8 || !password.exists(_.isDigit) || !password.exists(_.isLetter)) {
      errorMessage += "Password must be at least 8 characters long and contain both letters and digits\n"
    }

    // Phone number
    if (nullChecking(phone)) {
      errorMessage += "No valid phone number!\n"
    } else if (!phone.matches("""^01[0-9]-(\d{7}|(?<=011-)\d{8})$""")) { //ChatGPT assisted regex
      errorMessage += "Invalid mobile number! Use format 01X-XXXXXXX or 011-XXXXXXXX\n"
    }

    // Date of Birth
    if (nullChecking(dob)) {
      errorMessage += "No valid birthday!\n"
    } else if (!dob.isValid) {
      errorMessage += "Use the format dd.mm.yyyy for birthday!\n"
    } else {
      val today = java.time.LocalDate.now
      val birthDate = dob.parseLocalDate // assuming dob can be converted
      val age = java.time.Period.between(birthDate, today).getYears
      if (age < 18) {
        errorMessage += "You must be at least 18 years old!\n"
      }
    }

    // Address
    if (nullChecking(address))
      errorMessage += "No valid address!\n"
      

    if (errorMessage.isEmpty) {
      true
    } else {
      val alert = new Alert(Alert.AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }
      alert.showAndWait()
      false
    }
  }
}