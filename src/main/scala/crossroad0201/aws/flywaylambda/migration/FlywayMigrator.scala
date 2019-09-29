package crossroad0201.aws.flywaylambda.migration

import crossroad0201.aws.flywaylambda.deploy.FlywayDeployment
import org.flywaydb.core.Flyway

import scala.util.{Failure, Success, Try}

trait FlywayMigrator {

  def migrate(deployment: FlywayDeployment) = {
    val flyway = new Flyway(deployment.flywayConfig)

    val appliedCount = Try {
      flyway.migrate
    }

    val migrationInfos = Try {
      flyway.info.all
    }

    val url = deployment.flywayConfig.getDataSource.getConnection.getMetaData.getURL
    (appliedCount, migrationInfos) match {
      case (Success(c), Success(is)) => MigrationResult.success(url, c, is.map(MigrationInfo(_)))
      case (Success(c), Failure(e)) => MigrationResult.failure(url, e, Seq())
      case (Failure(e), Success(is)) => MigrationResult.failure(url, e, is.map(MigrationInfo(_)))
      case (Failure(e1), Failure(e2)) => MigrationResult.failure(url, e1, Seq())
    }
  }

}
