package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.Recipient
import nightyrelief.model.Aid
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

class RecipientController {
  @FXML private var fullNameLabel: Label = null
  @FXML private var icNumberLabel: Label = null
  @FXML private var emailLabel: Label = null
  @FXML private var phoneNumberLabel: Label = null
  @FXML private var dateOfBirthLabel: Label = null
  @FXML private var addressLabel: Label = null
  @FXML private var householdSizeLabel: Label = null
  @FXML private var occupationLabel: Label = null
  @FXML private var incomeLabel: Label = null
  @FXML private var disabilityLabel: Label = null
  @FXML private var supervisorNameLabel: Label = null
  @FXML private var supervisorContactLabel: Label = null
  @FXML private var item1Label: Label = null
  @FXML private var item2Label: Label = null
  @FXML private var item3Label: Label = null
  @FXML private var aidDate1Label: Label = null
  @FXML private var aidDate2Label: Label = null
  @FXML private var aidDate3Label: Label = null
  @FXML private var aidStatus1Label: Label = null
  @FXML private var aidStatus2Label: Label = null
  @FXML private var aidStatus3Label: Label = null
  @FXML private var aid1Button: Button = null
  @FXML private var aid2Button: Button = null
  @FXML private var aid3Button: Button = null
  @FXML private var moreButton: Button = _
  @FXML private var firstRecipientHistoryHBox: HBox = _
  @FXML private var secondRecipientHistoryHBox: HBox = _
  @FXML private var thirdRecipientHistoryHBox: HBox = _
  @FXML private var noHistoryLabel: Label = _

  private var _recipient: Option[Recipient] = None

  def setRecipient(recipient: Recipient): Unit = {
    _recipient = Some(recipient)
    _recipient.foreach { r =>
      fullNameLabel.setText(r.fullName.value)
      icNumberLabel.setText(r.icNumber.value)
      emailLabel.setText(r.email.value)
      phoneNumberLabel.setText(r.phoneNumber.value)
      dateOfBirthLabel.setText(r.dateOfBirth.value.asString)
      addressLabel.setText(r.address.value)
      householdSizeLabel.setText(r.householdSize.value.toString)
      occupationLabel.setText(r.occupation.value)
      incomeLabel.setText(r.income.value.toString)
      disabilityLabel.setText(if (r.disability.value) "Yes" else "No")
      supervisorNameLabel.setText(r.supervisorName.value)
      supervisorContactLabel.setText(r.supervisorContact.value)

      val userAids = Aid.getAllUserAid("Recipient", r.id.value.get)

      // Hide all by default
      firstRecipientHistoryHBox.setVisible(false)
      secondRecipientHistoryHBox.setVisible(false)
      thirdRecipientHistoryHBox.setVisible(false)
      moreButton.setVisible(false)
      noHistoryLabel.setVisible(userAids.isEmpty)

      if (userAids.nonEmpty) {
        setAidUI(userAids.head, item1Label, aidDate1Label, aidStatus1Label, aid1Button, r.householdSize.value)
        firstRecipientHistoryHBox.setVisible(true)
      }

      if (userAids.length > 1) {
        setAidUI(userAids(1), item2Label, aidDate2Label, aidStatus2Label, aid2Button, r.householdSize.value)
        secondRecipientHistoryHBox.setVisible(true)
      }

      if (userAids.length > 2) {
        setAidUI(userAids(2), item3Label, aidDate3Label, aidStatus3Label, aid3Button, r.householdSize.value)
        thirdRecipientHistoryHBox.setVisible(true)
      }

      // Show More button if there are more than 3 aids
      if (userAids.length > 3) {
        thirdRecipientHistoryHBox.setVisible(false)
        moreButton.setVisible(true)
        moreButton.onAction = _ => showRecipientHistory(userAids, r.householdSize.value)
      }
    }
  }

  private def setAidUI(aid: Aid, itemLabel: Label, dateLabel: Label, statusLabel: Label, button: Button, householdSize: Int): Unit = {
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
          setRecipient(_recipient.get)
        }

      case "Received" =>
        button.text = "View Info"
        button.visible = true
        button.disable = false
        button.onAction = _ => showAidInfo(aid, householdSize)

      case "" =>
        button.visible = false

      case _ =>
        button.text = aid.aidStatus.value
        button.visible = true
        button.disable = true
    }
  }

  def showAidInfo(aid: Aid, householdSize: Int): Unit = {
    new Alert(AlertType.Information) {
      initOwner(MainApp.stage)
      title = "Aid Information"
      headerText = s"${aid.item.value} Aid Details"
      contentText =
        s"Cash: RM ${1000 + 200 * householdSize}\n" +
          s"Rice: ${5 + householdSize} kg\n" +
          s"Flour: ${3 + householdSize} kg\n" +
          s"Oil: ${2 + householdSize} L\n" +
          s"Sugar: ${1 + householdSize} kg\n" +
          s"Meat (kg): ${2 + householdSize}\n" +
          s"Aid Status: ${aid.aidStatus.value}\n" +
          s"Aid Date: ${aid.aidDate.value.asString}\n"
    }.showAndWait()
  }

  def showRecipientHistory(aids: List[Aid], householdSize: Int): Unit = {
    val historyContainer = new VBox(10)
    historyContainer.padding = Insets(20)
    historyContainer.alignment = Pos.TopCenter
    historyContainer.getStyleClass.add("background")

    val titleLabel = new Label("Recipient Aid History")
    titleLabel.getStyleClass.add("label-title")

    historyContainer.children.add(titleLabel)

    if (aids.isEmpty) {
      val noHistoryLabel = new Label("No aid history available.")
      noHistoryLabel.getStyleClass.add("label-description")
      historyContainer.children.add(noHistoryLabel)
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
              showRecipientHistory(aids, householdSize) // refresh
            }
          case "Received" =>
            actionButton.text = "View Info"
            actionButton.onAction = _ => showAidInfo(aid, householdSize)
          case _ =>
            actionButton.text = aid.aidStatus.value
            actionButton.disable = true
        }

        val row = new HBox(10, itemLabel, dateLabel, statusLabel, actionButton)
        row.alignment = Pos.CenterLeft
        row.getStyleClass.add("hbox-history")

        historyContainer.children.add(row)
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
      title = "Recipient History"
      this.scene = historyScene
    }
    stage.show()
  }

  @FXML
  private def handleLogout(action: ActionEvent): Unit = {
    MainApp.showWelcome()
  }

  @FXML
  def handleEdit(action: ActionEvent): Unit = {
    _recipient.foreach { recipient =>
      val okClicked = MainApp.showRecipientEditDialog(recipient)
      if (okClicked) {
        recipient.save() match {
          case Success(_) =>
            setRecipient(recipient)
          case Failure(exception) =>
            new Alert(AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update recipient.\n" +
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