package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.{Aid, Farmer}
import nightyrelief.util.DateUtil.asString
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, ScrollPane}
import javafx.scene.layout.{HBox, VBox}
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

import scala.util.{Failure, Success}

class FarmerController {
  @FXML private var fullNameLabel: Label = null
  @FXML private var icNumberLabel: Label = null
  @FXML private var emailLabel: Label = null
  @FXML private var phoneNumberLabel: Label = null
  @FXML private var farmNameLabel: Label = null
  @FXML private var farmLocationLabel: Label = null
  @FXML private var farmSizeLabel: Label = null
  @FXML private var farmEstablishedDateLabel: Label = null
  @FXML private var numberOfEmployeesLabel: Label = null

  // --- Added FXML fields for Aid History ---
  @FXML private var item1Label: Label = null
  @FXML private var item2Label: Label = null
  @FXML private var item3Label: Label = null
  @FXML private var aidDate1Label: Label = null
  @FXML private var aidDate2Label: Label = null
  @FXML private var aidStatus1Label: Label = null
  @FXML private var aidStatus2Label: Label = null
  @FXML private var aid1Button: Button = null
  @FXML private var aid2Button: Button = null
  @FXML private var moreButton: Button = _
  @FXML private var firstFarmerHistoryHBox: HBox = _
  @FXML private var secondFarmerHistoryHBox: HBox = _
  @FXML private var noHistoryLabel: Label = _
  @FXML private var newRequestButton: Button = _
  // -----------------------------------------

  private var _farmer: Option[Farmer] = None

  def setFarmer(farmer: Farmer): Unit = {
    _farmer = Some(farmer)
    _farmer.foreach { f =>
      fullNameLabel.setText(f.fullName.value)
      icNumberLabel.setText(f.icNumber.value)
      emailLabel.setText(f.email.value)
      phoneNumberLabel.setText(f.phoneNumber.value)
      farmNameLabel.setText(f.farmName.value)
      farmLocationLabel.setText(f.farmLocation.value)
      farmSizeLabel.setText(f.farmSize.value.toString)
      farmEstablishedDateLabel.setText(f.farmEstablishedDate.value.asString)
      numberOfEmployeesLabel.setText(f.numberOfEmployees.value.toString)

      // --- Added logic for Aid History ---
      val userAids = Aid.getAllUserAid("Farmer", f.id.value.get)

      // Disable new request button if the latest request is still pending
      if (userAids.nonEmpty && userAids.head.aidStatus.value == "Requested") {
        newRequestButton.setDisable(true)
      } else {
        newRequestButton.setDisable(false)
      }

      // Hide all by default
      firstFarmerHistoryHBox.setVisible(false)
      secondFarmerHistoryHBox.setVisible(false)
      moreButton.setVisible(false)
      noHistoryLabel.setVisible(userAids.isEmpty)

      if (userAids.nonEmpty) {
        setAidUI(userAids.head, item1Label, aidDate1Label, aidStatus1Label, aid1Button)
        firstFarmerHistoryHBox.setVisible(true)
      }

      if (userAids.length > 1) {
        setAidUI(userAids(1), item2Label, aidDate2Label, aidStatus2Label, aid2Button)
        secondFarmerHistoryHBox.setVisible(true)
      }

      // Show More button if there are more than 3 aids
      if (userAids.length > 3) {
        moreButton.setVisible(true)
        moreButton.onAction = _ => showFarmerHistory(userAids) // Using 0 as placeholder for householdSize
      }
    }
  }

  private def setAidUI(aid: Aid, itemLabel: Label, dateLabel: Label, statusLabel: Label, button: Button): Unit = {
    itemLabel.text = aid.item.value
    dateLabel.text = aid.aidDate.value.asString
    statusLabel.text = aid.aidStatus.value

    aid.aidStatus.value match {
      case "Delivered" =>
        button.text = "Received"
        button.visible = true
        button.disable = false
        button.onAction = _ => {
          aid.aidStatus.value = "Received"
          aid.save()
          setFarmer(_farmer.get)
        }

      case "Received" =>
        button.text = "View Info"
        button.visible = true
        button.disable = false
        button.onAction = _ => showAidInfo(aid)

      case "Sent" =>
        button.text = "Checking"
        button.visible = true
        button.disable = true

      case "Completed" =>
        button.text = "Thank You"
        button.visible = true
        button.disable = true

      case "" =>
        button.visible = false

      case _ =>
        button.text = aid.aidStatus.value
        button.visible = true
        button.disable = true
    }
  }

  def showAidInfo(aid: Aid): Unit = {
    new Alert(AlertType.Information) {
      initOwner(MainApp.stage)
      title = "Aid Information"
      headerText = s"${aid.item.value} Aid Details"
      contentText =
        s"Cash: RM 5000\n" +
          s"Fertilizer: 5 kg\n" +
          s"Pesticide: 2 L\n" +
          s"Seeds: 10 kg\n" +
          s"Tools: 2 sets\n" +
          s"Aid Status: ${aid.aidStatus.value}\n" +
          s"Aid Date: ${aid.aidDate.value.asString}\n"
    }.showAndWait()
  }

  def showFarmerHistory(aids: List[Aid]): Unit = {
    val historyContainer = new VBox(10)
    historyContainer.padding = Insets(20)
    historyContainer.alignment = Pos.TopCenter
    historyContainer.getStyleClass.add("background")

    val titleLabel = new Label("Farmer Aid History")
    titleLabel.getStyleClass.add("label-title")

    historyContainer.getChildren.add(titleLabel)

    if (aids.isEmpty) {
      val noHistoryLabel = new Label("No aid history available.")
      noHistoryLabel.getStyleClass.add("label-description")
      historyContainer.getChildren.add(noHistoryLabel)
    } else {
      aids.foreach { aid =>
        val itemLabel = new Label(aid.item.value)
        itemLabel.setPrefWidth(260)
        itemLabel.getStyleClass.add("label-description")

        val dateLabel = new Label(aid.aidDate.value.asString)
        dateLabel.setPrefWidth(240)
        dateLabel.getStyleClass.add("label-description")

        val statusLabel = new Label(aid.aidStatus.value)
        statusLabel.setPrefWidth(173)
        statusLabel.getStyleClass.add("label-description")

        val actionButton = new Button()
        actionButton.setPrefWidth(218)
        aid.aidStatus.value match {
          case "Delivered" =>
            actionButton.text = "Received"
            actionButton.onAction = _ => {
              aid.aidStatus.value = "Received"
              aid.save()
              showFarmerHistory(aids)
            }
          case "Received" =>
            actionButton.text = "View Info"
            actionButton.onAction = _ => showAidInfo(aid)
          case "Sent" =>
            actionButton.text = "Checking"
            actionButton.disable = true
          case "Completed" =>
            actionButton.text = "Thank You"
            actionButton.disable = true
          case _ =>
            actionButton.text = aid.aidStatus.value
            actionButton.disable = true
        }

        val row = new HBox(10, itemLabel, dateLabel, statusLabel, actionButton)
        row.alignment = Pos.CenterLeft
        row.getStyleClass.add("hbox-history")

        historyContainer.getChildren.add(row)
      }
    }

    val scrollPane = new ScrollPane
    scrollPane.content = historyContainer
    scrollPane.setFitToWidth(true)
    scrollPane.setFitToHeight(true)

    val historyScene = new Scene(scrollPane, 800, 600)
    historyScene.stylesheets = Seq(MainApp.cssResource.toExternalForm)

    val stage = new Stage() {
      initOwner(MainApp.stage)
      title = "Farmer Aid History"
      scene = historyScene
    }
    stage.show()
  }

  @FXML
  private def handleNewRequest(action: ActionEvent): Unit = {
    _farmer.foreach { farmer =>
      // Create a new Aid request
      val newAid = new Aid("Farmer", farmer.id.value.getOrElse(0)) // Assuming id is Option[Int])
      newAid.item.value = "Grant"
      newAid.amount.value = 5000
      newAid.aidStatus.value = "Requested" // Set an initial status

      newAid.save() match {
        case Success(_) =>
          newRequestButton.setDisable(true)
          new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title = "New Aid Request"
            headerText = "Aid Request Created"
            contentText = "Your aid request has been successfully created."
          }.showAndWait()
          setFarmer(farmer) // Refresh the UI
        case Failure(exception) =>
          new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database error: failed to create aid request.\n" +
              s"Error message: ${exception.getMessage}"
          }.showAndWait()
      }
    }
  }

  @FXML
  private def handleDonate(action: ActionEvent): Unit = {
    _farmer.foreach { farmer =>
      // Logic to handle donation
      new Alert(AlertType.Information) {
        initOwner(MainApp.stage)
        title = "Donate"
        contentText = "Our team will contact you soon to arrange the donation process."
      }.showAndWait()
    }
  }

  @FXML
  private def handleLogout(action: ActionEvent): Unit = {
    MainApp.showWelcome()
  }

  @FXML
  def handleEdit(action: ActionEvent): Unit = {
    _farmer.foreach { farmer =>
      val okClicked = MainApp.showFarmerEditDialog(farmer)
      if (okClicked) {
        farmer.save() match {
          case Success(_) =>
            setFarmer(farmer)
          case Failure(exception) =>
            new Alert(AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update farmer.\n" +
                s"Error message: ${exception.getMessage}"
            }.showAndWait()
        }
      }
    }
  }

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
}