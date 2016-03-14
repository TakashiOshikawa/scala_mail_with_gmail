package mail

/**
  * Created by oshikawatakashi on 2016/03/06.
  */
object Template {

  def listToString(ls: List[String]): List[String] = {
    ls map (_.replaceAll("""\\n""", "<br>"))
  }

}
