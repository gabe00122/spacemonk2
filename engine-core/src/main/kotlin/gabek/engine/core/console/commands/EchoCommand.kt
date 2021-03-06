package gabek.engine.core.console.commands

import com.github.salomonbrys.kodein.Kodein
import gabek.engine.core.console.Command
import gabek.engine.core.console.Console
import gabek.engine.core.console.HelpLevel

/**
 * @author Gabriel Keith
 * @date 4/18/2017
 */

class EchoCommand: Command() {
    override fun setup(kodein: Kodein) {

    }

    override fun process(args: Array<String>) {
        console.writeln(args.joinToString())
    }

    override fun help(level: HelpLevel) {

    }
}