package nightyrelief.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.HBox
import scalafx.scene.chart.{CategoryAxis, NumberAxis, PieChart, StackedBarChart, XYChart}
import scalafx.stage.Stage
import nightyrelief.MainApp
import scalafx.Includes.*
import nightyrelief.model.Aid
import scalafx.collections.ObservableBuffer

import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ImpactController {

  @FXML private var chartHBox: HBox = _

  var dialogStage: Stage = null

  @FXML
  def initialize(): Unit = {

    val recipientNum = MainApp.recipientData.length
    val farmerNum = MainApp.farmerData.length
    val donorNum = MainApp.donorData.length

    val recipientCount = PieChart.Data(s"$recipientNum Recipients", recipientNum)
    val farmerCount = PieChart.Data(s"$farmerNum Farmers", farmerNum)
    val donorCount = PieChart.Data(s"$donorNum Donors", donorNum)

    val userChart = new PieChart {
      data = Seq(recipientCount, farmerCount, donorCount)
      title = "User Distribution"
    }

    // get current month and year
    var currentMonth = YearMonth.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    val third = Aid.getAidCountByMonthAndYear(currentMonth.getMonthValue, currentMonth.getYear)
    val thirdMonthYear = currentMonth.format(formatter)

    currentMonth = currentMonth.minusMonths(1)
    val second = Aid.getAidCountByMonthAndYear(currentMonth.getMonthValue, currentMonth.getYear)
    val secondMonthYear = currentMonth.format(formatter)

    currentMonth = currentMonth.minusMonths(1)
    val first = Aid.getAidCountByMonthAndYear(currentMonth.getMonthValue, currentMonth.getYear)
    val firstMonthYear = currentMonth.format(formatter)

    val monthYear = Seq(firstMonthYear.toString, secondMonthYear.toString, thirdMonthYear.toString)
    val xAxis = CategoryAxis(monthYear)

    val yAxis = NumberAxis("Number of Aids")

    def xyData(ys: Seq[Number]) = ObservableBuffer.from(monthYear zip ys map (xy => XYChart.Data(xy._1, xy._2)))

    val series1 = new XYChart.Series[String, Number] {
      name = "Recipients"
      data = xyData(Seq(first._1, second._1, third._1))
    }
    val series2 = new XYChart.Series[String, Number] {
      name = "Farmers"
      data = xyData(Seq(first._2, second._2, third._2))
    }
    val series3 = new XYChart.Series[String, Number] {
      name = "Donors"
      data = xyData(Seq(first._3, second._3, third._3))
    }
    
    val userAidChart = new StackedBarChart(xAxis, yAxis) {
      title = "Last 3 Months Aid Distribution"
      data() ++= Seq(series1, series2, series3)
      categoryGap = 25.0d
    }
    
    chartHBox.children += userChart
    chartHBox.children += userAidChart
  }
}