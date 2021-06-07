This plug-in implements partial meet and kernel pseudo-contractions for Protégé.

The algorithms that compute remainder and kernel sets were adapted
from
[R. F. Guimarães' OWL2DL-Change repository](https://gitlab.com/rfguimaraes/owl-change).

### Compilation

This plug-in requires Java &geq; 8 and Maven.
Run `mvn package` to generate a JAR file under the directory `target/`.

### Installation

Copy the JAR file into the `plugins/` directory of the Protégé installation.

### Execution

Open Protégé and add the tab of this plug-in (Menu `Window` > `Tabs` >
`Pseudo-contraction`).
Type the sentence to be contracted, customise the options and click `Run`.
Choose the remainders or the kernel elements on the dialogue that will be shown
and click `Execute operation`.
