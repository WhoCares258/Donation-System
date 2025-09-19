package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.{Aid, Donor}
import nightyrelief.util.DateUtil.asString
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{ScrollPane, Button as JFXButton, Label as JFXLabel}
import javafx.scene.layout.HBox as JFXHBox
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonBar, ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Stage

import scala.util.{Failure, Success, Try}

class DonorController {
  @FXML private var fullNameLabel: JFXLabel = _
  @FXML private var icNumberLabel: JFXLabel = _
  @FXML private var emailLabel: JFXLabel = _
  @FXML private var phoneNumberLabel: JFXLabel = _
  @FXML private var dateOfBirthLabel: JFXLabel = _
  @FXML private var addressLabel: JFXLabel = _
  @FXML private var jobTitleLabel: JFXLabel = _
  @FXML private var workplaceLabel: JFXLabel = _
  @FXML private var causeLabel: JFXLabel = _
  @FXML private var donationTypeLabel: JFXLabel = _

  @FXML private var firstDonorHistoryHBox: JFXHBox = _
  @FXML private var secondDonorHistoryHBox: JFXHBox = _
  @FXML private var noHistoryLabel: JFXLabel = _
  @FXML private var moreButton: JFXButton = _
  @FXML private var item1Label: JFXLabel = _
  @FXML private var item2Label: JFXLabel = _
  @FXML private var aidDate1Label: JFXLabel = _
  @FXML private var aidDate2Label: JFXLabel = _
  @FXML private var aidStatus1Label: JFXLabel = _
  @FXML private var aidStatus2Label: JFXLabel = _
  @FXML private var aid1Button: JFXButton = _
  @FXML private var aid2Button: JFXButton = _

  private var _donor: Option[Donor] = None

  def setDonor(donor: Donor): Unit = {
    _donor = Some(donor)
    _donor.foreach { d =>
      fullNameLabel.setText(d.fullName.value)
      icNumberLabel.setText(d.icNumber.value)
      emailLabel.setText(d.email.value)
      phoneNumberLabel.setText(d.phoneNumber.value)
      dateOfBirthLabel.setText(d.dateOfBirth.value.asString)
      addressLabel.setText(d.address.value)
      jobTitleLabel.setText(d.jobTitle.value)
      workplaceLabel.setText(d.workplace.value)
      causeLabel.setText(d.cause.value)
      donationTypeLabel.setText(d.donationType.value)

      val userAids = Aid.getAllUserAid("Donor", d.id.value.get)

      firstDonorHistoryHBox.setVisible(false)
      secondDonorHistoryHBox.setVisible(false)
      moreButton.setVisible(false)
      noHistoryLabel.setVisible(userAids.isEmpty)

      if (userAids.nonEmpty) {
        setAidUI(userAids.head, item1Label, aidDate1Label, aidStatus1Label, aid1Button)
        firstDonorHistoryHBox.setVisible(true)
      }

      if (userAids.length > 1) {
        setAidUI(userAids(1), item2Label, aidDate2Label, aidStatus2Label, aid2Button)
        secondDonorHistoryHBox.setVisible(true)
      }

      if (userAids.length > 2) {
        moreButton.setVisible(true)
        moreButton.onAction = _ => showDonorHistory(userAids)
      }
    }
  }

  private def setAidUI(aid: Aid, itemLabel: JFXLabel, dateLabel: JFXLabel, statusLabel: JFXLabel, button: JFXButton): Unit = {
    itemLabel.setText("RM" + aid.amount.value)
    dateLabel.setText(aid.aidDate.value.asString)
    statusLabel.setText(aid.aidStatus.value)

    button.setVisible(true)
    button.setDisable(true)

    aid.aidStatus.value match {
      case "Sent" =>
        button.setText("Confirming")
      case "Completed" =>
        button.setText("Thank You")
      case _ =>
        button.setText(aid.aidStatus.value)
    }
  }

  def showDonorHistory(aids: List[Aid]): Unit = {
    val historyContainer = new VBox(10)
    historyContainer.padding = Insets(20)
    historyContainer.alignment = Pos.TopCenter
    historyContainer.getStyleClass.add("background")

    val titleLabel = new Label("Recipient Aid History")
    titleLabel.getStyleClass.add("label-title")

    historyContainer.children.add(titleLabel)

    if (aids.isEmpty) {
      val noHistoryLabel = new Label("No aid history available.")
      noHistoryLabel.styleClass.add("label-description")
      historyContainer.getChildren.add(noHistoryLabel)
    } else {
      aids.foreach { aid =>
        val itemLabel = new Label(aid.item.value)
        itemLabel.prefWidth = 260
        itemLabel.getStyleClass.add("label-description")

        val dateLabel = new Label(aid.aidDate.value.asString)
        dateLabel.prefWidth = 240
        dateLabel.getStyleClass.add("label-description")

        val statusLabel = new Label(aid.aidStatus.value)
        statusLabel.prefWidth = 173
        statusLabel.getStyleClass.add("label-description")

        val actionButton = new scalafx.scene.control.Button()
        actionButton.prefWidth = 218
        actionButton.disable = true
        aid.aidStatus.value match
          case "Sent" =>
            actionButton.text = "Confirming"
          case "Completed" =>
            actionButton.text = "Thank You"
          case _ =>
            actionButton.text = aid.aidStatus.value

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

    new Stage() {
      initOwner(MainApp.stage)
      title = "Donor Aid History"
      scene = historyScene
    }.show()
  }

  @FXML
  private def handleDonate(action: ActionEvent): Unit = {
    _donor.foreach { donor =>
      val dialog = new Dialog[(String, String)]() {
        initOwner(MainApp.stage)
        title = "New Donation"
        headerText = "Enter your donation details below."
      }

      val donateButtonType = new ButtonType("Donate", ButtonBar.ButtonData.OKDone)
      dialog.dialogPane().getButtonTypes.addAll(donateButtonType, ButtonType.Cancel)

      val bankName = "NightyRelief Bank"
      val accountNumber = s"1234-56-${100000 + scala.util.Random.nextInt(900000)}"
      val bankDetailsLabel = new Label(
        s"Please transfer to:\nBank: $bankName\nAccount Number: $accountNumber"
      ) {
        styleClass.add("label-description")
      }

      val amountField = new TextField {
        promptText = "Amount (RM)"
      }
      val receiptField = new TextField {
        promptText = "Receipt Number"
      }

      val grid = new GridPane {
        hgap = 10
        vgap = 10
        padding = Insets(20, 10, 10, 10)
        add(bankDetailsLabel, 0, 0, 2, 1)
        add(new Label("Cash (RM):  "), 0, 1)
        add(amountField, 1, 1)
        add(new Label("Reference:  "), 0, 2)
        add(receiptField, 1, 2)
      }

      dialog.dialogPane().setContent(grid)

      val donateButton = dialog.dialogPane().lookupButton(donateButtonType)
      donateButton.disableProperty().bind(
        amountField.text.isEmpty || receiptField.text.isEmpty
      )

      dialog.resultConverter = (dialogButton: ButtonType) =>
        if (dialogButton == donateButtonType)
          (amountField.text.value, receiptField.text.value)
        else
          null

      dialog.showAndWait() match {
        case Some((amountStr: String, receiptNumber: String)) =>
          Try(amountStr.toDouble) match {
            case Success(amountValue) =>
              val newAid = new Aid("Donor", donor.id.value.get)
              newAid.item.value = receiptNumber
              newAid.amount.value = amountValue
              newAid.aidStatus.value = "Completed"

              newAid.save() match {
                case Success(_) =>
                  new Alert(AlertType.Information) {
                    initOwner(MainApp.stage)
                    title = "Donation"
                    headerText = "Donation Recorded"
                    contentText = "Your donation has been successfully recorded. Thank you!"
                  }.showAndWait()
                  setDonor(donor) // refresh UI
                case Failure(exception) =>
                  new Alert(AlertType.Warning) {
                    initOwner(MainApp.stage)
                    title = "Failed to Save"
                    headerText = "Database Error"
                    contentText = s"Database error: failed to record donation.\nError message: ${exception.getMessage}"
                  }.showAndWait()
              }

            case Failure(_) =>
              new Alert(AlertType.Error) {
                initOwner(MainApp.stage)
                title = "Invalid Input"
                headerText = "Invalid Amount"
                contentText = "Please enter a valid number for the amount."
              }.showAndWait()
          }

        case None =>
        case Some(_) => ??? // User cancelled the dialog
      }
    }
  }

  @FXML
  private def handleLogout(action: ActionEvent): Unit = {
    MainApp.showWelcome()
  }

  @FXML
  def handleEdit(action: ActionEvent): Unit = {
    _donor.foreach { donor =>
      val okClicked = MainApp.showDonorEditDialog(donor)
      if (okClicked) {
        donor.save() match {
          case Success(_) =>
            setDonor(donor)
          case Failure(exception) =>
            new Alert(AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database error: failed to update donor.\n" +
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