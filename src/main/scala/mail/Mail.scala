package mail

/**
  * Created by oshikawatakashi on 2016/03/06.
  */

import java.util.Date
import scala.util.Try
import javax.mail._
import javax.mail.internet._

case class MailSender(to: Seq[InternetAddress], from:String, subject:String, body: String) {
  class PlainAuthenticator(user:String, password:String) extends Authenticator {
    override def getPasswordAuthentication() = {
      new PasswordAuthentication(user, password)
    }
  }
  def send = Try{
    val props = System.getProperties

    // Googleのセキュリティ設定で認証が通らない場合
    // https://www.google.com/settings/security/lesssecureapps
    // の安全性の低いアプリのアクセスをオンにする
    val user = "-----------@gmail.com"
    val pass = "yourpassword"
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "465")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    props.put("mail.smtp.socketFactory.fallback", "false")
    props.put("mail.smtp.user", user)
    props.put("mail.smtp.password", pass)
    val session = Session.getInstance(props, new PlainAuthenticator(user, pass))
    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(from, "from-name", "ISO-2022-JP"))
    msg.setRecipients(Message.RecipientType.TO, to.mkString(","))
    msg.setSubject(subject)
    msg.setContent(body, "text/html; charset=iso-2022-jp")
    msg.setHeader("X-Mailer", "playframework mailer coded in scala")
    msg.setSentDate(new Date())
    Transport.send(msg)
  }
}
