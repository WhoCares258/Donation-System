package nightyrelief.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.HBox
import scalafx.Includes.*
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.stage.Stage
import scalafx.util.StringConverter

class AboutController {

  @FXML private var chartHBox: HBox = _

  var dialogStage: Stage = null

  @FXML
  def initialize(): Unit = {
    // Data extracted from https://data.gov.my/data-catalogue/hh_poverty
    val povertyData = Seq(
      1995d -> 19.5d,
      1997d -> 19.7d,
      1999d -> 19.0d,
      2002d -> 20.0d,
      2004d -> 19.9d,
      2007d -> 17.4d,
      2009d -> 19.3d,
      2012d -> 19.2d,
      2014d -> 15.6d,
      2016d -> 15.9d,
      2019d -> 16.9d,
      2020d -> 16.2d,
      2022d -> 16.6d,
    )

    val series = new XYChart.Series[Number, Number] {
      name = "Poverty Rate"
      data() ++= povertyData.map {
        case (x, y) => XYChart.Data[Number, Number](x, y)
      }
    }

    val xAxis = NumberAxis("Year", 1994, 2024, 3)
    xAxis.tickLabelFormatter = new StringConverter[Number] {
      override def toString(t: Number): String = f"${t.intValue}%d"
      override def fromString(s: String): Number = s.toInt
    }

    val yAxis = NumberAxis("Poverty Rate (%)", 12, 23, 1)

    val povertyChart = new LineChart(xAxis, yAxis) {
      title = "Poverty Rate in Malaysia"
      legendVisible = false
      data() += series
    }

    chartHBox.children += povertyChart
  }
}
