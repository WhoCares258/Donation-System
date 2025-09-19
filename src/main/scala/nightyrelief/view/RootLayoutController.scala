package nightyrelief.view

import javafx.fxml.FXML
import javafx.event.ActionEvent
import nightyrelief.MainApp
import java.lang.reflect.Method

@FXML
class RootLayoutController():
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)

  @FXML
  private def handleAbout(action: ActionEvent): Unit = {
    MainApp.showAbout()
  }

  @FXML
  private def handleImpact(action: ActionEvent): Unit = {
    MainApp.showImpact()
  }

  @FXML
  private def handleContact(action: ActionEvent): Unit = {
    MainApp.showContact()
  }

  private var currentController: AnyRef = _

  def setCurrentController(controller: AnyRef): Unit = {
    currentController = controller
  }

  @FXML
  def handleLogout(action: ActionEvent): Unit =
    MainApp.showWelcome()

  @FXML
  def handleNew(action: ActionEvent): Unit = {
    invokeIfExists("handleNew", action)
  }

  @FXML
  def handleEdit(action: ActionEvent): Unit = {
    invokeIfExists("handleEdit", action)
  }

  @FXML
  def handleDelete(action: ActionEvent): Unit = {
    invokeIfExists("handleDelete", action)
  }

  /** Utility to check if current controller has a method and invoke it */
  private def invokeIfExists(methodName: String, action: ActionEvent): Unit = {
    if (currentController != null) {
      val methodOpt = currentController.getClass.getMethods.find { m =>
        m.getName == methodName &&
          m.getParameterCount == 1 &&
          classOf[ActionEvent].isAssignableFrom(m.getParameterTypes.head)
      }
      methodOpt.foreach(_.invoke(currentController, action))
    }
  }

