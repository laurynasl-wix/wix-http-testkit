import depends._

lazy val publishSettings = Seq(
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if ( version.value.trim.endsWith( "SNAPSHOT" ) )
      Some( "snapshots" at nexus + "content/repositories/snapshots" )
    else
      Some( "releases" at nexus + "service/local/staging/deploy/maven2" )
  },
  publishMavenStyle := true,
  pomExtra in ThisBuild :=
    <scm>
      <url>git@github.com:wix/wix-http-testkit.git</url>
      <connection>scm:git:git@github.com:wix/wix-http-testkit.git</connection>
    </scm>
      <developers>
        <developer>
          <id>noama</id>
          <name>Noam Almog</name>
          <email>noamal@gmail.com</email>
          <organization>Wix</organization>
        </developer>
      </developers>
)



lazy val compileOptions = Seq(
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.11.12", "2.12.4"/*, "2.13-M2"*/),
  //  sbtVersion in Global := "1.0.0",
  //  scalaCompilerBridgeSource := {
  //    val sv = appConfiguration.value.provider.id.version
  //    ("org.scala-sbt" % "compiler-interface" % sv % "component").sources
  //  },
  //  scalaOrganization in ThisBuild := "org.typelevel",
  //  javaRuntimeVersion := System.getProperty( "java.vm.specification.version" ).toDouble,
  //  crossScalaVersions := ( javaRuntimeVersion.value match {
  //    case v if v >= 1.8 => Seq( "2.11.8", "2.12.1" )
  //    case _             => Seq( "2.11.8" )
  //  } ),
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xfatal-warnings"
  )
)

lazy val noPublish = Seq( publish := {}, publishLocal := {}, publishArtifact := false )

lazy val baseSettings =
  publishSettings ++
    //    releaseSettings ++
    compileOptions ++
    Seq(
      organization := "com.wix",
      homepage := Some( url( "https://github.com/wix-private/http-testkit" ) ),
      licenses := Seq( "MIT" -> url( "https://opensource.org/licenses/MIT" ) )
    )

lazy val httpTestkitTestCommons =
  (project in file("http-testkit-test-commons"))
    .settings(Seq(
      name := "http-testkit-test-commons",
      description := "Commonly used test utilities"
    ) ++ baseSettings ++ noPublish)

lazy val httpTestkitCore =
  (project in file("http-testkit-core"))
    .settings(Seq(
      name := "http-testkit-core",
      libraryDependencies ++= joda ++ specs2Test(scalaVersion.value) :+ akkaHttp(scalaVersion.value) :+ scalaXml :+ reflections :+ jsr305 :+ slf4jApi,
      description := "Commonly used util code also client and server interfaces"
    ) ++ baseSettings)
    .dependsOn(httpTestkitTestCommons % Test)

lazy val httpTestkitClient =
  (project in file("http-testkit-client"))
    .settings(Seq(
      name := "http-testkit-client",
      libraryDependencies ++= specs2(scalaVersion.value) ,
      description := "All code related to REST client, blocking and non-blocking"
    ) ++ baseSettings)
    .dependsOn(httpTestkitCore, httpTestkitSpecs2, httpTestkitTestCommons % Test, httpTestkitMarshallerJackson % Test)

lazy val httpTestkitServer =
  (project in file("http-testkit-server"))
    .settings(Seq(
      name := "http-testkit-server",
      description := "Server implementations - stub and mock"
    ) ++ baseSettings)
    .dependsOn(httpTestkitCore)

lazy val httpTestkitSpecs2 =
  (project in file("http-testkit-specs2"))
    .settings(Seq(
      name := "http-testkit-specs2",
      libraryDependencies ++= specs2(scalaVersion.value),
      description := "Specs2 Matcher suites - Request and Response."
    ) ++ baseSettings)
    .dependsOn(httpTestkitCore, httpTestkitTestCommons % Test, httpTestkitMarshallerJackson % Test)

lazy val httpTestkitMarshallerJackson =
  (project in file("http-testkit-marshaller-jackson"))
    .settings(Seq(
      name := "http-testkit-marshaller-jackson",
      libraryDependencies ++= jackson(scalaVersion.value) ++ specs2(scalaVersion.value),
      description := "Marshaller implementation - jackson"
    ) ++ baseSettings)
    .dependsOn(httpTestkitCore, httpTestkitTestCommons % Test)

lazy val httpTestkit =
  (project in file("http-testkit"))
    .settings(Seq(
      name := "Http Testkit",
      description := "Main module, contain factories but no implementation."
    ) ++ baseSettings)
    .dependsOn(httpTestkitClient, httpTestkitServer, httpTestkitSpecs2)

lazy val httpTestkitContractTests =
  (project in file("http-testkit-contract-tests"))
    .settings(Seq(
      name := "http-testkit-contract-tests",
      libraryDependencies ++= specs2Test(scalaVersion.value),
      description := "Contract tests for both client and server"
    ) ++ baseSettings ++ noPublish)
    .dependsOn(httpTestkit, httpTestkitMarshallerJackson, httpTestkitTestCommons % Test)

lazy val httpTestkitContractTestsCustomMarshaller =
  (project in file("http-testkit-contract-tests-custom-marshaller"))
    .settings(Seq(
      name := "http-testkit-contract-tests-custom-marshaller",
      libraryDependencies ++= specs2Test(scalaVersion.value),
      description := "Contract tests for both client and server"
    ) ++ baseSettings ++ noPublish)
    .dependsOn(httpTestkit, httpTestkitTestCommons % Test)

lazy val httpTestkitContractTestsNoCustomMarshaller =
  (project in file("http-testkit-contract-tests-no-custom-marshaller"))
    .settings(Seq(
      name := "http-testkit-contract-tests-no-custom-marshaller",
      libraryDependencies ++= specs2Test(scalaVersion.value),
      description := "Contract tests for both client and server"
    ) ++ baseSettings ++ noPublish)
    .dependsOn(httpTestkit, httpTestkitTestCommons % Test)

//lazy val httpTestkitExamples =
//  Project(
//    id = "examples",
//    base = file( "examples" ),
//    settings = Seq(
//      name := "examples",
//      libraryDependencies ++= specs2.map(_ % "test") ,
//      description :=
//        "Some crap i need to describe the library"
//    ) ++ baseSettings
//  ).dependsOn(wixHttpTestkit)


lazy val root =
  (project in file("."))
    .settings(Seq(name:= "Wix Http Testkit Modules") ++ baseSettings ++ noPublish)
    .aggregate(httpTestkitTestCommons,
               httpTestkitCore, httpTestkitClient, httpTestkitServer, httpTestkitSpecs2, httpTestkit, httpTestkitMarshallerJackson,
               httpTestkitContractTests, httpTestkitContractTestsCustomMarshaller, httpTestkitContractTestsNoCustomMarshaller)
