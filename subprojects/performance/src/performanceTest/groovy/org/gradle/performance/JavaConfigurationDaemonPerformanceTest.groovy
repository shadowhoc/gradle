/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.performance

import org.gradle.performance.categories.Experiment
import org.gradle.performance.categories.JavaPerformanceTest
import org.junit.experimental.categories.Category
import spock.lang.Unroll

import static org.gradle.performance.measure.DataAmount.mbytes
import static org.gradle.performance.measure.Duration.millis

@Category([JavaPerformanceTest])
class JavaConfigurationDaemonPerformanceTest extends AbstractCrossVersionPerformanceTest {

    @Category([Experiment])
    @Unroll("configure Java software model build - #testProject")
    def "configure Java software model build"() {
        given:
        runner.testId = "configure Java build $testProject (daemon)"
        runner.previousTestIds = ["configure new java project $testProject"]
        runner.testProject = testProject
        runner.tasksToRun = ['help']
        runner.targetVersions = ['2.11', 'last']
        runner.useDaemon = true
        runner.maxExecutionTimeRegression = maxExecutionTimeRegression
        runner.maxMemoryRegression = mbytes(25)
        runner.gradleOpts = ["-Xms1g", "-Xmx1g"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject               | maxExecutionTimeRegression
        "largeJavaSwModelProject" | millis(500)
        "bigNewJava"              | millis(500)
        "mediumNewJava"           | millis(500)
        "smallNewJava"            | millis(500)
    }

    @Unroll("configure Java build - #testProject")
    def "configure Java build"() {
        given:
        runner.testId = "configure Java build $testProject (daemon)"
        runner.previousTestIds = ["configure java project $testProject"]
        runner.testProject = testProject
        runner.tasksToRun = ['help']
        runner.targetVersions = targetVersions
        runner.useDaemon = true
        runner.maxExecutionTimeRegression = maxExecutionTimeRegression
        runner.maxMemoryRegression = mbytes(25)
        runner.gradleOpts = ["-Xms1g", "-Xmx1g"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject     | maxExecutionTimeRegression | targetVersions
        "bigOldJava"    | millis(500)                | ['2.11', 'last']
        "mediumOldJava" | millis(500)                | ['2.11', 'last']
        // TODO: Restore 'last' when sufficent performance gains are made.
        "smallOldJava"  | millis(500)                | ['3.1-20160818000032+0000']
    }
}
