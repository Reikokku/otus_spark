import scala.io.Source
import scala.util.Using
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import scala.reflect.io.File

object ReadJson extends App{

  val filePath = File(args(0))

  case class Name (official: String)
  case class Country (name: Name, capital: List[String], region: String, area: Double)

  val readCodec: JsonValueCodec[List[Country]] = JsonCodecMaker.make

  case class Output(name: String, capital: String, area: Double)

  val writeCodec: JsonValueCodec[List[Output]] = JsonCodecMaker.make

  val json =
    Using(Source.fromURL("https://raw.githubusercontent.com/mledoze/countries/master/countries.json")) {
    resource => resource.mkString
  }.getOrElse("")

  def countryToOutput(country: Country): Output =
    Output(name = country.name.official, capital = country.capital.head, area = country.area)

  val top10 = readFromArray(json.getBytes("UTF-8"))(readCodec)
    .filter(_.region == "Africa")
    .sortBy(_.area)
    .reverse
    .take(10)
    .map(countryToOutput)

  val resJsonArray = writeToString(top10)(writeCodec)

  filePath.writeAll(resJsonArray)
}



