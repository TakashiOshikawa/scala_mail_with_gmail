import java.util.Calendar
import javax.mail.internet.InternetAddress

import dispatch._, Defaults._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import mail.{MailSender, Template}

// TODO 祝日送らないようにする設定
object Main {
  def main(args: Array[String]) = {

    val request_url: String = "message_api_url"
    val api_token: String = "api_token"

    lazy val optReportJsons: Option[JsValue] = this.fetchDailyReportJsonString(request_url, api_token)

    lazy val a_day_ago: Int = 60*60*24
    lazy val daily_time = Calendar.getInstance().getTime().getTime/1000-(a_day_ago)

    lazy val daily_reports = optReportJsons match {
      case Some(v) => {
        this.dailyMessages(this.dailyReportDatas(v), daily_time)
      }
      case None    => List()
    }

    // メール送信
    val body = Template.listToString(daily_reports)
    val m = MailSender(InternetAddress.parse("mail-address1@gmail.com, mail-address2@gmail.com"), "from@mail.address.com", new DateTime().toString("yyyyMMdd")+"タイトル", Template.listToString(body).foldLeft("メッセージ<br><br>お疲れ様です。<br><br>%s".format(this.generateTodaysDateMessage))(_+"<br><br><br>"+_).replace("\"", "")+("<br>") )
    val res = m.send
  }

  // ChatWork APIから日報JSON取得 デフォルト100件
  def fetchDailyReportJsonString(request_url: String, api_token: String): Option[JsValue] = {
    val req = url(request_url).addHeader("X-ChatWorkToken", api_token)
    val res = Http(req)
    val res_byte = new String(res().getResponseBodyAsBytes())

    res_byte match {
      case s: String if s.isEmpty => None
      case s: String              => Some( Json.parse(res_byte) )
      case _                      => None
    }
  }

  // JsValueで取得した日報データをListのJsValueにして返す
  def dailyReportDatas(json_reports: JsValue): List[JsValue] = json_reports.as[List[JsValue]]

  // 指定時以後の日報取得
  // リプライと削除したメッセージは除く [rp という文字を含まない [delete
  def dailyMessages(messages_json: List[JsValue], day_start_timestamp: Long): List[String] = {
    messages_json
      .filter( x => (x \\ "send_time")(0).as[Int] > day_start_timestamp )
      .filter( x => (x \\ "body")(0).as[String].contains("[rp") == false )
      .filter( x => (x \\ "body")(0).as[String].contains("[delete") == false )
      .map( x => (x \\ "body")(0) )
      .map(_.toString())
  }

  def generateTodaysDateMessage(): String = {
    val today = new DateTime().toString(DateTimeFormat.fullDate())
    "%s分の日報です。<br>".format(today)
  }
}
