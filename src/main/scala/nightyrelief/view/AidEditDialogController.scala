package nightyrelief.view

import nightyrelief.model.Aid
import javafx.fxml.FXML
import javafx.scene.control.TextField
import scalafx.stage.Stage
import scalafx.Includes.*
import nightyrelief.util.DateUtil.*
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class AidEditDialogController {

  @FXML private var userTypeField: TextField = null
  @FXML private var userIDField: TextField = null
  @FXML private var aidStatusField: TextField = null
  @FXML private var aidDateField: TextField = null
  @FXML private var itemField: TextField = null
  @FXML private var amountField: TextField = null

  var dialogStage: Stage = null
  private var __aid: Aid = null
  var okClicked: Boolean = false

  def aid: Aid = __aid
  def aid_=(x: Aid): Unit = {
    __aid = x
    // Populate fields from aid object
    if (__aid != null) {
      userTypeField.text = __aid.userType.value
      userIDField.text = __aid.userId.value.toString
      aidStatusField.text = __aid.aidStatus.value
      aidDateField.text = __aid.aidDate.value.asString
      itemField.text = __aid.item.value
      amountField.text = __aid.amount.value.toString
    }
  }

  @FXML
  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      __aid.userType <== userTypeField.text
      __aid.userId.value = userIDField.text.value.toInt
      __aid.aidStatus <== aidStatusField.text
      __aid.aidDate.value = aidDateField.text.value.parseLocalDate
      __aid.item <== itemField.text
      __aid.amount.value = amountField.text.value.toInt

      okClicked = true
      dialogStage.close()
    }
  }

  @FXML
  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }

  def nullChecking(x: String): Boolean = {
    x == null || x.trim.isEmpty
  }

  def isInputValid(): Boolean = {
    var errorMessage = ""

    if (nullChecking(userTypeField.getText)) {
      errorMessage += "User Type cannot be empty!\n"
    }
    if (nullChecking(userIDField.getText)) {
      errorMessage += "User ID cannot be empty!\n"
    } else {
      try {
        userIDField.getText.toInt
      } catch {
        case _: NumberFormatException =>
          errorMessage += "User ID must be a number!\n"
      }
    }
    if (nullChecking(aidStatusField.getText)) {
      errorMessage += "Aid Status cannot be empty!\n"
    }
    if (nullChecking(aidDateField.getText)) {
      errorMessage += "Aid Date cannot be empty!\n"
    } else if (!aidDateField.getText.isValid) {
      errorMessage += "Use format dd.mm.yyyy for Date!\n"
    }
    if (nullChecking(itemField.getText)) {
      errorMessage += "Item cannot be empty!\n"
    }
    if (nullChecking(amountField.getText)) {
      errorMessage += "Aid Amount cannot be empty!\n"
    } else {
      try {
        if (amountField.getText.toInt < 0) {
          errorMessage += "Aid Amount must be a positive number!\n"
        }
      } catch {
        case _: NumberFormatException =>
          errorMessage += "Aid Amount must be a valid number!\n"
      }
    }

    if (errorMessage.isEmpty) {
      true
    } else {
      new Alert(AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
      false
    }
  }
}