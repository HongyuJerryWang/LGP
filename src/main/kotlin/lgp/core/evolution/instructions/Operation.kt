package lgp.core.evolution.instructions

import lgp.core.evolution.registers.Arguments
import lgp.core.modules.Module

/**
 * An operation has an [Arity] and some function that it can perform on [Arguments] given to it.
 *
 * Operations are specified as [Module]s, meaning that custom operations can be implemented and use
 * when generating instructions for an individual in the population.
 *
 * Operations should have some representation with them, so that the operation that they convey can
 * be expressed in a textual form.
 *
 * @param T Type of arguments that the operation operates on.
 * @param arity How many arguments the operations function takes.
 * @param func A function mapping arguments to some value.
 */
abstract class Operation<T>(val arity: Arity, val func: (Arguments<T>) -> T ) : Module {
    /**
     * A way to express an operation in a textual format.
     */
    abstract val representation: String

    /**
     * Executes an operation in some way. Generally this would simply involve applying
     * the function the operation represents to the arguments given.
     *
     * @param arguments A set of arguments to the function.
     * @return A value of type T mapped from the arguments.
     */
    abstract fun execute(arguments: Arguments<T> ): T
}