package nightyrelief.view

import nightyrelief.model.Recipient
import nightyrelief.util.DateUtil.*
import javafx.scene.control.{RadioButton, TextField, TextArea}
import scalafx.scene.control.Alert
import scalafx.stage.Stage
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.event.ActionEvent

@FXML
class RecipientEditDialogController {

  @FXML private var fullNameField: TextField = null
  @FXML private var icNumberField: TextField = null
  @FXML private var emailField: TextField = null
  @FXML private var passwordField: TextField = null
  @FXML private var phoneNumberField: TextField = null
  @FXML private var dateOfBirthField: TextField = null
  @FXML private var addressField: TextArea = null
  @FXML private var householdSizeField: TextField = null
  @FXML private var occupationField: TextField = null
  @FXML private var incomeField: TextField = null
  @FXML private var disabilityRadio: RadioButton = null
  @FXML private var supervisorNameField: TextField = null
  @FXML private var supervisorContactField: TextField = null
  @FXML private var extraNotesField: TextArea = null

  var dialogStage: Stage = null
  private var __recipient: Recipient = null
  var okClicked: Boolean = false

  def recipient: Recipient = __recipient
  def recipient_=(x: Recipient): Unit = {
    __recipient = x
    // Populate fields from recipient object
    if (__recipient != null) {
      fullNameField.text = __recipient.fullName.value
      icNumberField.text = __recipient.icNumber.value
      emailField.text = __recipient.email.value
      passwordField.text = __recipient.password.value
      phoneNumberField.text = __recipient.phoneNumber.value
      dateOfBirthField.text = __recipient.dateOfBirth.value.asString
      addressField.text = __recipient.address.value
      householdSizeField.text = __recipient.householdSize.value.toString
      occupationField.text = __recipient.occupation.value
      incomeField.text = __recipient.income.value.toString
      disabilityRadio.setSelected(__recipient.disability.value)
      supervisorNameField.text = __recipient.supervisorName.value
      supervisorContactField.text = __recipient.supervisorContact.value
      extraNotesField.text = __recipient.extraNotes.value
    }
  }

  @FXML
  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      __recipient.fullName <== fullNameField.text
      __recipient.icNumber <== icNumberField.text
      __recipient.email <== emailField.text
      __recipient.password <== passwordField.text
      __recipient.phoneNumber <== phoneNumberField.text
      __recipient.dateOfBirth.value = dateOfBirthField.text.value.parseLocalDate
      __recipient.address <== addressField.text
      __recipient.householdSize.value = householdSizeField.text.value.toInt
      __recipient.occupation <== occupationField.text
      __recipient.income.value = incomeField.text.value.toInt
      __recipient.disability.value = disabilityRadio.isSelected
      __recipient.supervisorName <== supervisorNameField.text
      __recipient.supervisorContact <== supervisorContactField.text
      __recipient.extraNotes <== extraNotesField.text

      okClicked = true
      dialogStage.close()
    }
  }

  @FXML
  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }

  def nullChecking(x: String): Boolean = x == null || x.isEmpty

  def isInputValid(): Boolean = {
    var errorMessage = ""

    val fullName = fullNameField.getText
    val email = emailField.getText
    val ic = icNumberField.getText
    val password = passwordField.getText
    val phone = phoneNumberField.getText
    val dob = dateOfBirthField.text.value
    val address = addressField.getText
    val occupation = occupationField.getText
    val income = incomeField.getText
    val supervisorContact = supervisorContactField.getText

    // Full name
    if (nullChecking(fullName))
      errorMessage += "No valid full name!\n"

    // Email
    if (nullChecking(email)) {
      errorMessage += "No valid email!\n"
    } else {
      val parts = email.split("@")
      if (parts.length != 2 || parts(0).isEmpty || parts(1).isEmpty || !parts(1).contains("."))
        errorMessage += "Not valid email! It must have '@' and '.'!\n"
    }

    // IC Number
    if (nullChecking(ic)) {
      errorMessage += "No valid IC number!\n"
    } else if (!ic.matches("""\d{6}-\d{2}-\d{4}""")) { //ChatGPT assisted regex
      errorMessage += "Use Format 000000-00-0000 for IC Number!\n"
    }

    // Password
    if (nullChecking(password)) {
      errorMessage += "No valid password!\n"
    } else {
      if (password.length < 8) {
        errorMessage += "Password must be at least 8 characters long!\n"
      } else if (!password.exists(_.isDigit) || !password.exists(_.isLetter)) {
        errorMessage += "Password must contain both letters and digits!\n"
      }
    }

    // Phone number
    if (nullChecking(phone)) {
      errorMessage += "No valid phone number!\n"
    } else if (!phone.matches("""^01[0-9]-(\d{7}|(?<=011-)\d{8})$""")) { //ChatGPT assisted regex
      errorMessage += "Phone number format: 01X-XXXXXXX or 011-XXXXXXXX\n"
    }

    // Date of Birth
    if (nullChecking(dob)) {
      errorMessage += "No valid birthday!\n"
    } else if (!dob.isValid) {
      errorMessage += "Use format dd.mm.yyyy for birthday!\n"
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

    // Income
    if (nullChecking(income)) {
      errorMessage += "No valid income!\n"
    } else {
      try {
        if (income.toDouble < 0)
          errorMessage += "Income must be a positive number!\n"
      } catch {
        case _: NumberFormatException =>
          errorMessage += "Income must be a valid number!\n"
      }
    }
    
    // Household Size
    if (nullChecking(householdSizeField.getText)) {
      errorMessage += "No valid household size!\n"
    } else {
      try {
        val householdSize = householdSizeField.getText.toInt
        if (householdSize < 1)
          errorMessage += "Household size must be at least 1!\n"
      } catch {
        case _: NumberFormatException =>
          errorMessage += "Household size must be a valid integer!\n"
      }
    }

    // Supervisor Contact
    if (!nullChecking(supervisorContact)) {
      if (!supervisorContact.matches("""^01[0-9]-(\d{7}|(?<=011-)\d{8})$"""))
        errorMessage += "Phone number format: 01X-XXXXXXX or 011-XXXXXXXX\n"
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