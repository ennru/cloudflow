/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloudflow.operator
package action

import scala.collection.immutable._
import skuber._
import cloudflow.operator.action.runner.{ AkkaRunner, FlinkRunner, Runner, SparkRunner }

/**
 * Creates a sequence of resource actions for preparing the namespace where the application is
 * installed
 */
object PrepareNamespaceActions {
  def apply(app: CloudflowApplication.CR, namespace: String, labels: CloudflowLabels, ownerReferences: List[OwnerReference])(
      implicit ctx: DeploymentContext
  ): Seq[Action[ObjectResource]] = {
    val runners = extractRunners(app)
    runners.flatMap { runner =>
      runner.prepareNamespaceActions(app, namespace, labels, ownerReferences)
    }
  }

  private def extractRunners(app: CloudflowApplication.CR): Vector[Runner[_]] =
    app.spec.streamlets.map(streamlet => streamlet.descriptor.runtime.name).distinct.map {
      case AkkaRunner.runtime  => AkkaRunner
      case FlinkRunner.runtime => FlinkRunner
      case SparkRunner.runtime => SparkRunner
    }
}