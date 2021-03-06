package lgp.core.program.registers

/**
 * A simple collection of [Argument]s.
 *
 * The sole purpose of this wrapper class is to make the syntax
 * of functions for operations a bit cleaner. For example, we can write:
 *
 * ```
 * // A simple cosine function definition
 * val cos: (Arguments<Double>) -> Double = { args -> Math.cos(args.get(0)) }
 * ```
 *
 * Instead of:
 *
 * ```
 * // A verbose cosine function definition
 * val cos: (List<Argument<Double>>) -> Double = { args -> Math.cos(args.get(0)) }
 * ```
 *
 * @param T The type of the arguments.
 * @property arguments A collection of [Argument]s to wrap.
 */
class Arguments<T>(val arguments: List<Argument<T>>) {

    /**
     * How many arguments there are.
     */
    fun size(): Int {
        return this.arguments.size
    }

    /**
     * Gets the value of an argument at the position given by [index].
     *
     * @param index The index of an argument value to fetch.
     * @throws IndexOutOfBoundsException When the index is not in the bounds of the argument collection.
     * @returns The value of the argument at [index].
     */
    fun get(index: Int): T {
        return this.arguments[index].value
    }
}