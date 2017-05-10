package lgp.examples

import lgp.core.environment.*
import lgp.core.environment.config.JsonConfigLoader
import lgp.core.environment.constants.GenericConstantLoader
import lgp.core.environment.dataset.CsvDatasetLoader
import lgp.core.environment.operations.DefaultOperationLoader
import lgp.core.evolution.Runners
import lgp.core.evolution.fitness.FitnessFunctions
import lgp.core.evolution.population.*
import lgp.lib.BaseInstructionGenerator
import lgp.lib.BaseProgram
import lgp.lib.BaseProgramGenerator
import lgp.lib.BaseProgramSimplifier
import java.util.*

class Iris {
    companion object Main {
        // Locations of configuration and data set files.
        private val configFilename =  this::class.java.classLoader.getResource("iris_env.json").file
        private val datasetFilename =  this::class.java.classLoader.getResource("iris.csv").file

        @JvmStatic fun main(args: Array<String>) {
            val configLoader = JsonConfigLoader(
                    filename = configFilename
            )

            val config = configLoader.load()

            val nominalValues = setOf("Iris-setosa", "Iris-versicolor", "Iris-virginica")

            val datasetLoader = CsvDatasetLoader(
                    filename = datasetFilename,
                    // Allow parsing of the nominal attributes
                    parseFunction = { v: String ->
                        when (v) {
                            // Simply map the nominal value to its index (i.e. class ∈ {0, 1, 2})
                            in nominalValues -> nominalValues.indexOf(v).toDouble()
                            else -> v.toDouble()
                        }
                    },
                    // Nominal attribute values
                    labels = nominalValues.toList()
            )

            // Set up a loader for loading the operations we want to use (specified in the configuration file)
            val operationLoader = DefaultOperationLoader<Double>(
                    operationNames = config.operations
            )

            // Set up a loader for the constant values (specified in the configuration file)
            val constantLoader = GenericConstantLoader(
                    constants = config.constants,
                    parseFunction = String::toDouble
            )

            // Fill calculation registers with the value 1.0
            val defaultValueProvider = DefaultValueProviders.constantValueProvider(1.0)

            val ce = FitnessFunctions.thresholdCE(threshold = 0.5)

            // Create a new environment with these components.
            val environment = Environment(
                    configLoader,
                    constantLoader,
                    datasetLoader,
                    operationLoader,
                    defaultValueProvider,
                    fitnessFunction = ce
            )

            // Set up registered modules
            val container = ModuleContainer(
                    modules = mutableMapOf(
                            CoreModuleType.InstructionGenerator to {
                                BaseInstructionGenerator(environment)
                            },
                            CoreModuleType.ProgramGenerator to {
                                BaseProgramGenerator(environment, sentinelTrueValue = 1.0)
                            },
                            CoreModuleType.SelectionOperator to {
                                TournamentSelection(environment, tournamentSize = 4)
                            },
                            CoreModuleType.RecombinationOperator to {
                                LinearCrossover(
                                        environment,
                                        maximumSegmentLength = 6,
                                        maximumCrossoverDistance = 5,
                                        maximumSegmentLengthDifference = 3
                                )
                            },
                            CoreModuleType.MacroMutationOperator to {
                                MacroMutationOperator(
                                        environment,
                                        insertionRate = 0.67,
                                        deletionRate = 0.33
                                )
                            },
                            CoreModuleType.MicroMutationOperator to {
                                MicroMutationOperator(
                                        environment,
                                        registerMutationRate = 0.5,
                                        operatorMutationRate = 0.0,
                                        constantMutationFunc = { v ->
                                            // Add random gaussian noise to constant with standard deviation of 1
                                            // from the current value.
                                            v + (Random().nextGaussian() * 1)
                                        }
                                )
                            }
                    )
            )

            environment.registerModules(container)

            // Find the best individual with these parameters.
            val model = Models.SteadyState(environment)

            val runner = Runners.DistributedRunner(environment, model, runs = 5)
            val result = runner.run()
            val simplifier = BaseProgramSimplifier<Double>()

            result.evaluations.forEachIndexed { run, res ->
                println("Run ${run + 1} (best fitness = ${res.best.fitness})")
                //res.best.effectiveInstructions.forEach(::println)
                println(simplifier.simplify(res.best as BaseProgram<Double>))
                println("\nStats (last run only):\n")

                for ((k, v) in res.statistics.last().data) {
                    println("$k = $v")
                }
                println("")
            }
        }
    }
}

