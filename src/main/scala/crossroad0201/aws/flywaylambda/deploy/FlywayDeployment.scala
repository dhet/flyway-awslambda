package crossroad0201.aws.flywaylambda.deploy

import java.nio.file.Path
import java.util.{Properties => JProperties}

import com.amazonaws.services.lambda.runtime.Context
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.Configuration

case class FlywayDeployment(
  sourceBucket: String,
  sourcePrefix: String,
  sqlFiles: Seq[Path],
  flywayConfig: Configuration
)

object FlywayDeployment {

  def apply(sourceBucket: String, sourcePrefix:String, maybeConfig: Option[JProperties], location: String, sqlFiles: Seq[Path])(implicit context: Context): FlywayDeployment = {
    val logger = context.getLogger

    val config = (maybeConfig match {
      case Some(conf) => Flyway.configure().configuration(conf)
      case None =>
        logger.log("No Flyway configuration found. Configuring via FLYWAY_* environment variables.")
        Flyway.configure().envVars()
    }).locations(location)

    logger.log(
      s"""--- Flyway configuration ------------------------------------
         |flyway.url      = ${config.getDataSource.getConnection.getMetaData.getURL}
         |flyway.user     = ${config.getDataSource.getConnection.getMetaData.getUserName}
         |flyway.password = ****
         |
         |SQL locations   = ${config.getLocations.mkString(", ")}
         |SQL files       = ${sqlFiles.mkString(", ")}
         |-------------------------------------------------------------
              """.stripMargin)

    FlywayDeployment(
      sourceBucket,
      sourcePrefix,
      sqlFiles,
      config
    )
  }
}
