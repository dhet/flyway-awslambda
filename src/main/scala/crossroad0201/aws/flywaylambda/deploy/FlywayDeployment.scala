package crossroad0201.aws.flywaylambda.deploy

import java.nio.file.Path
import java.util.{Properties => JProperties}

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.Configuration

case class FlywayDeployment(
  sourceBucket: String,
  sourcePrefix: String,
  sqlFiles: Seq[Path],
  flywayConfig: Configuration
)

object FlywayDeployment {
  def apply(sourceBucket: String, sourcePrefix:String, conf: JProperties, location: String, sqlFiles: Seq[Path]): FlywayDeployment = {
    FlywayDeployment(
      sourceBucket,
      sourcePrefix,
      sqlFiles,
      Flyway.configure()
        .configuration(conf)
        .locations(location)
    )
  }
}
