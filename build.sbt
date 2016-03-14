lazy val root = (project in file(".")).
  settings(
    name := "daily_report",
    version := "1.0",
    scalaVersion := "2.11.7",
    libraryDependencies ++= dervy
  )

lazy val dervy = {
  Seq(
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "com.typesafe.play"   %   "play-json_2.11" % "2.4.2",
    "javax.mail" % "mail" % "1.4"
  )
}