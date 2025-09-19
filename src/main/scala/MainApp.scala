package nightyrelief

import nightyrelief.model.{Aid, Donor, Farmer, Recipient}
import nightyrelief.util.Database
import nightyrelief.view.{AboutController, AidEditDialogController, ContactController, DonorController, DonorEditDialogController, FarmerController, FarmerEditDialogController, ImpactController, LoginController, RecipientController, RecipientEditDialogController, RootLayoutController}
import javafx.fxml.FXMLLoader
import javafx.scene.layout.{AnchorPane, BorderPane}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.stage.Modality.ApplicationModal
import scalafx.stage.Stage

import java.net.URL

object MainApp extends JFXApp3:
  Database.setupDB()
  var rootPane: Option[BorderPane] = None
  var rootLayoutController: Option[RootLayoutController] = None
  var cssResource: URL = getClass.getResource("view/style.css")

  var recipientData = new ObservableBuffer[Recipient]()
  var farmerData = new ObservableBuffer[Farmer]()
  var donorData = new ObservableBuffer[Donor]()
  var aidData = new ObservableBuffer[Aid]()

  recipientData ++= Recipient.getAllRecipients
  farmerData ++= Farmer.getAllFarmers
  donorData ++= Donor.getAllDonors
  aidData ++= Aid.getAllAids

  override def start(): Unit = {
    val rootLayoutResource: URL = getClass.getResource("/nightyrelief/view/RootLayout.fxml")
    val loader = new FXMLLoader(rootLayoutResource)
    val rootLayout = loader.load[BorderPane]()
    rootPane = Option(rootLayout)

    rootLayoutController = Some(loader.getController[RootLayoutController]())

    stage = new PrimaryStage():
      title = "Nighty Relief"
      icons += new Image(getClass.getResourceAsStream("/images/earth.png"))
      scene = new Scene():
        root = rootLayout
        stylesheets = Seq(cssResource.toExternalForm)
    showWelcome()
  }

  /** Generic FXML loader that swaps the center pane and optionally configures the controller */
  def showView[T](fxmlPath: String)(configure: T => Unit = (_: T) => ()): Unit = {
    val resource: URL = getClass.getResource(fxmlPath)
    val loader = new FXMLLoader(resource)
    val pane = loader.load[AnchorPane]()
    rootPane.foreach(_.setCenter(pane))

    val controller = loader.getController[T]()

    // Automatically tell RootLayoutController about the active controller
    rootLayoutController.foreach(_.setCurrentController(controller.asInstanceOf[AnyRef]))

    // Allow caller to do any extra configuration
    configure(controller)
  }


  def showWelcome(): Unit =
      showView[Object]("/nightyrelief/view/Welcome.fxml")()

  def showRecipient(recipient: Recipient): Unit =
    showView[RecipientController]("/nightyrelief/view/Recipient.fxml")(_.setRecipient(recipient))

  def showFarmer(farmer: Farmer): Unit =
    showView[FarmerController]("/nightyrelief/view/Farmer.fxml")(_.setFarmer(farmer))

  def showDonor(donor: Donor): Unit =
    showView[DonorController]("/nightyrelief/view/Donor.fxml")(_.setDonor(donor))

  def showAdminRecipient(): Unit =
    showView[Object]("/nightyrelief/view/AdminRecipient.fxml")()

  def showAdminFarmer(): Unit =
    showView[Object]("/nightyrelief/view/AdminFarmer.fxml")()

  def showAdminDonor(): Unit =
    showView[Object]("/nightyrelief/view/AdminDonor.fxml")()

  def showAdminAid(): Unit =
    showView[Object]("/nightyrelief/view/AdminAid.fxml")()
  
  def showLogin(role: String): Boolean = {
    val loginResource: URL = getClass.getResource("/nightyrelief/view/Login.fxml")
    val loader = new FXMLLoader(loginResource)
    loader.load()
    val pane = loader.getRoot[AnchorPane]()
    val myWindow = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = s"$role Login"
      icons += new Image(getClass.getResourceAsStream("/images/earth.png"))
      scene = new Scene():
        root = pane
        stylesheets = Seq(cssResource.toExternalForm)
    val ctrl = loader.getController[LoginController]()
    ctrl.stage = Option(myWindow)
    ctrl.role = role // Pass the role to the controller

    if (role == "Admin") {
      ctrl.setAdminLogin()
    }
    myWindow.showAndWait()
    ctrl.loginSuccess
  }

  def showRecipientEditDialog(recipient: Recipient): Boolean = {
    val dialogResource: URL = getClass.getResource("/nightyrelief/view/RecipientEditDialog.fxml")
    val loader = new FXMLLoader(dialogResource)
    loader.load()
    val dialogPane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Recipient"
      scene = new Scene():
        root = dialogPane
        stylesheets = Seq(cssResource.toExternalForm)

    val controller = loader.getController[RecipientEditDialogController]()
    controller.dialogStage = dialogStage
    controller.recipient = recipient

    dialogStage.showAndWait()
    controller.okClicked
  }

  def showFarmerEditDialog(farmer: Farmer): Boolean =
    val dialogResource: URL = getClass.getResource("/nightyrelief/view/FarmerEditDialog.fxml")
    val loader = new FXMLLoader(dialogResource)
    loader.load()
    val dialogPane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Farmer"
      scene = new Scene():
        root = dialogPane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[FarmerEditDialogController]()
    controller.dialogStage = dialogStage
    controller.farmer = farmer

    dialogStage.showAndWait()
    controller.okClicked

  def showDonorEditDialog(donor: Donor): Boolean =
    val dialogResource: URL = getClass.getResource("/nightyrelief/view/DonorEditDialog.fxml")
    val loader = new FXMLLoader(dialogResource)
    loader.load()
    val dialogPane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Donor"
      scene = new Scene():
        root = dialogPane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[DonorEditDialogController]()
    controller.dialogStage = dialogStage
    controller.donor = donor

    dialogStage.showAndWait()
    controller.okClicked

  def showAidEditDialog(aid: Aid): Boolean =
    val dialogResource: URL = getClass.getResource("/nightyrelief/view/AidEditDialog.fxml")
    val loader = new FXMLLoader(dialogResource)
    loader.load()
    val dialogPane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Edit Aid"
      scene = new Scene():
        root = dialogPane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[AidEditDialogController]()
    controller.dialogStage = dialogStage
    controller.aid = aid

    dialogStage.showAndWait()
    controller.okClicked
    
  def showAbout(): Unit =
    val dialogResource: URL = getClass.getResource("/nightyrelief/view/About.fxml")
    val loader = new FXMLLoader(dialogResource)
    loader.load()
    val dialogPane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "About NightyRelief"
      scene = new Scene():
        root = dialogPane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[AboutController]()
    controller.dialogStage = dialogStage
    dialogStage.showAndWait()
    
  def showImpact(): Unit =
    val impactResource: URL = getClass.getResource("/nightyrelief/view/Impact.fxml")
    val loader = new FXMLLoader(impactResource)
    loader.load()
    val pane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Impact Overview"
      scene = new Scene():
        root = pane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[ImpactController]()
    controller.dialogStage = dialogStage
    dialogStage.showAndWait()

  def showContact(): Unit =
    val contactResource: URL = getClass.getResource("/nightyrelief/view/Contact.fxml")
    val loader = new FXMLLoader(contactResource)
    loader.load()
    val pane = loader.getRoot[AnchorPane]()
    val dialogStage = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "Contact Us"
      scene = new Scene():
        root = pane
        stylesheets = Seq(cssResource.toExternalForm)
    val controller = loader.getController[ContactController]()
    controller.dialogStage = dialogStage
    dialogStage.showAndWait()
  
end MainApp