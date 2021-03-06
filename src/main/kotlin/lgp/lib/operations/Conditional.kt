package lgp.lib.operations

import lgp.core.program.instructions.BranchOperation
import lgp.core.program.registers.Arguments
import lgp.core.modules.ModuleInformation

/**
 * Performs a branch by comparing two arguments using the greater than operator.
 *
 * Instructions using this operation essentially achieve the following:
 *
 * ```
 * if (r[1] > r[2]) {
 *     return 1.0;
 * } else {
 *     return 0.0;
 * }
 * ```
 *
 * This can be used by an interpreter to determine if a branch should be taken or not
 * (by treating the return value as a boolean).
 * ```
 */
class IfGreater : BranchOperation<Double>(
        func = { args: Arguments<Double> ->
            if (args.get(0) > args.get(1)) 1.0 else 0.0
        }
) {
    override val representation = " > "

    override val information = ModuleInformation(
            description = ""
    )
}

/**
 * Performs a branch by comparing two arguments using the less than or equal to operator.
 *
 * Instructions using this operation essentially achieve the following:
 *
 * ```
 * if (r[1] <= r[2]) {
 *     return 1.0;
 * } else {
 *     return 0.0;
 * }
 * ```
 *
 * This can be used by an interpreter to determine if a branch should be taken or not
 * (by treating the return value as a boolean).
 * ```
 */
class IfLessThanOrEqualTo : BranchOperation<Double>(
        func = { args: Arguments<Double> ->
            if (args.get(0) <= args.get(1)) 1.0 else 0.0
        }
) {
    override val representation = " < "

    override val information = ModuleInformation(
            description = ""
    )
}