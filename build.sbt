name := "GraphXCitation"

version := "0.1"

scalaVersion := "2.12.17"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.4.4" % "provided",
  "org.apache.spark" %% "spark-sql"  % "3.4.4" % "provided",
  "org.apache.spark" %% "spark-graphx" % "3.4.4" % "provided"
)
