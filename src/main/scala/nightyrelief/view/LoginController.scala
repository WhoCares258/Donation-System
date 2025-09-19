package nightyrelief.view

import nightyrelief.MainApp
import nightyrelief.model.{Donor, Farmer, Recipient}
import scalafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Hyperlink, Label, PasswordField, TextField}
import javafx.scene.layout.AnchorPane
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import scala.language.reflectiveCalls

class LoginController:

  // Stage property
  var stage: Option[Stage] = None
  // Return property
  var loginSuccess = false
  var role: String = null

  // FXML references
  @FXML private var signupLink: Hyperlink = _
  @FXML private var forgotPasswordLink: Hyperlink = _
  @FXML private var signupLabel: Label = _
  @FXML private var rootPane: AnchorPane = _
  @FXML private var emailField: TextField = _
  @FXML private var passwordField: PasswordField = _

  def setAdminLogin(): Unit =
    if signupLink != null then signupLink.setVisible(false)
    if forgotPasswordLink != null then forgotPasswordLink.setVisible(false)
    if signupLabel != null then signupLabel.setVisible(false)
    if rootPane != null then rootPane.setPrefHeight(rootPane.getPrefHeight - 30)

  /** Email validation with '@' and '.' check */
  private def validateEmail(email: String): Either[String, Unit] =
    if email == null || email.trim.isEmpty then
      Left("No valid email!\n")
    else
      val parts = email.split("@")
      if parts.length != 2 || parts(0).isEmpty || parts(1).isEmpty || !parts(1).contains(".") then
        Left("Email must have '@' and '.'!\n")
      else Right(())

  /** Password validation: length >= 8 and at least 1 number */
  private def validatePassword(password: String): Either[String, Unit] =
    if password.length < 8 then
      Left("Password must be at least 8 characters long.")
    else if !password.exists(_.isDigit) then
      Left("Password must contain at least one number.")
    else Right(())

  /** Generic authentication for all user roles */
  private def authenticateUser[T <: { def email: StringProperty; def password: StringProperty }](
                                                                                                  users: ObservableBuffer[T],
                                                                                                  email: String,
                                                                                                  password: String,
                                                                                                  onSuccess: T => Unit
                                                                                                ): Either[String, Unit] =
    users.find(_.email.value == email) match
      case Some(user) if user.password.value == password =>
        onSuccess(user)
        Right(())
      case Some(_) => Left("Incorrect password.")
      case None    => Left("Email address not found.")

  @FXML
  def handleLogin(action: ActionEvent): Unit =
    val email = emailField.getText.trim
    val password = passwordField.getText.trim

    val loginResult = role match
      case "Admin" =>
        if email == "admin" && password == "password1" then
          MainApp.showAdminRecipient()
          Right(())
        else
          Left("Invalid admin username or password.")
      case "Recipient" | "Farmer" | "Donor" =>
        for
          _ <- validateEmail(email)
          _ <- validatePassword(password)
          _ <- role match
            case "Recipient" => authenticateUser(MainApp.recipientData, email, password, MainApp.showRecipient)
            case "Farmer"    => authenticateUser(MainApp.farmerData, email, password, MainApp.showFarmer)
            case "Donor"     => authenticateUser(MainApp.donorData, email, password, MainApp.showDonor)
            case _           => Left("Invalid role specified.") // Should not be reached
        yield ()

      case _ => Left("Invalid role specified.")

    loginResult match
      case Right(_) =>
        loginSuccess = true
        stage.foreach(_.close())
      case Left(error) =>
        new Alert(AlertType.Error) {
          initOwner(stage.orNull)
          title = "Login Failed"
          headerText = "Invalid Input"
          contentText = error
        }.showAndWait()

  @FXML
  def handleSignUp(action: ActionEvent): Unit = {
    val dummyEvent = new ActionEvent()
    role match
      case "Recipient" =>
        AdminRecipientController().handleNew(dummyEvent)
      case "Farmer" =>
        AdminFarmerController().handleNew(dummyEvent)
      case "Donor" =>
        AdminDonorController().handleNew(dummyEvent)
      case _ =>
        new Alert(AlertType.Warning) {
          initOwner(stage.orNull)
          title = "Signup Not Available"
          headerText = "Signup Unavailable"
          contentText = s"Signup is not available for $role users."
        }.showAndWait()
  }

  @FXML
  def handleForgotPassword(action: ActionEvent): Unit =
    val email = emailField.getText.trim
    validateEmail(email) match
      case Right(_) =>
        new Alert(AlertType.Information) {
          initOwner(stage.orNull)
          title = "Forgot Password"
          headerText = "Password Recovery"
          contentText = "If the email address you entered is associated with an account, you will receive an email with instructions on how to reset your password."
        }.showAndWait()
      case Left(error) =>
        new Alert(AlertType.Error) {
          initOwner(stage.orNull)
          title = "Invalid Email"
          headerText = "Invalid Email Address"
          contentText = error
        }.showAndWait()