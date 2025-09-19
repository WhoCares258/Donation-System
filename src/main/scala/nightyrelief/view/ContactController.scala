package nightyrelief.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{TextArea, TextField}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

class ContactController {

  var dialogStage: Stage = _

  @FXML
  private var emailField: TextField = _
  @FXML
  private var messageField: TextArea = _

  @FXML
  def handleSend(action: ActionEvent): Unit = {
    val email = emailField.getText
    val message = messageField.getText
    var errorMessage = ""

    if (email == null || email.trim.isEmpty) {
      errorMessage += "No valid email!\n"
    } else {
      val parts = email.split("@")
      if (parts.length != 2 || parts(0).isEmpty || parts(1).isEmpty || !parts(1).contains(".")) {
        errorMessage += "Not a valid email! It must have '@' and '.'!\n"
      }
    }

    if (message == null || message.trim.isEmpty) {
      errorMessage += "Message field cannot be empty!\n"
    }

    if (errorMessage.isEmpty) {
      dialogStage.close()
      new Alert(AlertType.Confirmation) {
        initOwner(dialogStage)
        title = "Success"
        contentText = "Your message has been sent successfully! We will get back to you soon."
        headerText = "Thank you for contacting us!"
      }.showAndWait()
    } else {
      new Alert(AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
    }
  }
}