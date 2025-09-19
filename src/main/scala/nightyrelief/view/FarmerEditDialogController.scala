package nightyrelief.view

import nightyrelief.model.Farmer
import nightyrelief.util.DateUtil.*
import javafx.scene.control.{TextField, TextArea}
import scalafx.scene.control.Alert
import scalafx.stage.Stage
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.event.ActionEvent

@FXML
class FarmerEditDialogController {

  @FXML private var fullNameField: TextField = null
  @FXML private var icNumberField: TextField = null
  @FXML private var emailField: TextField = null
  @FXML private var passwordField: TextField = null
  @FXML private var phoneNumberField: TextField = null
  @FXML private var dateOfBirthField: TextField = null
  @FXML private var farmNameField: TextField = null
  @FXML private var farmLocationField: TextArea = null
  @FXML private var farmSizeField: TextField = null
  @FXML private var farmEstablishedDateField: TextField = null
  @FXML private var employeeCountField: TextField = null
  @FXML private var farmDetailsField: TextArea = null

  var dialogStage: Stage = null
  private var __farmer: Farmer = null
  var okClicked: Boolean = false

  def farmer: Farmer = __farmer
  def farmer_=(x: Farmer): Unit = {
    __farmer = x
    // Populate fields from farmer object
    if (__farmer != null) {
      fullNameField.text = __farmer.fullName.value
      icNumberField.text = __farmer.icNumber.value
      emailField.text = __farmer.email.value
      passwordField.text = __farmer.password.value
      phoneNumberField.text = __farmer.phoneNumber.value
      dateOfBirthField.text = __farmer.dateOfBirth.value.asString
      farmNameField.text = __farmer.farmName.value
      farmLocationField.text = __farmer.farmLocation.value
      farmSizeField.text = __farmer.farmSize.value.toString
      farmEstablishedDateField.text = __farmer.farmEstablishedDate.value.asString
      employeeCountField.text = __farmer.numberOfEmployees.value.toString
      farmDetailsField.text = __farmer.farmDetails.value
    }
  }

  @FXML
  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      __farmer.fullName <== fullNameField.text
      __farmer.icNumber <== icNumberField.text
      __farmer.email <== emailField.text
      __farmer.password <== passwordField.text
      __farmer.phoneNumber <== phoneNumberField.text
      __farmer.dateOfBirth.value = dateOfBirthField.text.value.parseLocalDate
      __farmer.farmName <== farmNameField.text
      __farmer.farmLocation <== farmLocationField.text
      __farmer.farmSize.value = farmSizeField.text.value.toDouble
      __farmer.farmEstablishedDate.value = farmEstablishedDateField.text.value.parseLocalDate
      __farmer.numberOfEmployees.value = employeeCountField.text.value.toInt
      __farmer.farmDetails <== farmDetailsField.text

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
    val farmName = farmNameField.getText
    val farmLocation = farmLocationField.getText
    val farmSize = farmSizeField.getText
    val farmEstablishedDate = farmEstablishedDateField.text.value
    val employeeCount = employeeCountField.text.value

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

    if (nullChecking(farmName))
      errorMessage += "No valid farm name!\n"

    if (nullChecking(farmLocation))
      errorMessage += "No valid farm location!\n"

    if (nullChecking(farmSize)) {
      errorMessage += "No valid farm size!\n"
    } else {
      try {
        if (farmSize.toDouble <= 0)
          errorMessage += "Farm size must be a positive number\n"
      } catch {
        case _: NumberFormatException =>
          errorMessage += "Farm size must be a valid number\n"
      }
    }

    try {
      val count = employeeCountField.getText.toInt
      if (count <= 0) {
        errorMessage += "Employee count must be a positive integer\n"
      }
    } catch {
      case _: NumberFormatException =>
        errorMessage += "Employee count must be a valid integer\n"
    }
    
    if (nullChecking(farmEstablishedDate)) {
      errorMessage += "No valid farm established date!\n"
    } else if (!farmEstablishedDate.isValid) {
      errorMessage += "Use format dd.mm.yyyy for date!\n"
    }


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