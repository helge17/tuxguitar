# Contributing

Contributions to this project are welcome, and you do not need to have any development skills to contribute. Below are a few examples of what you can provide to the community.


## Test the application, suggest improvements

[Issues](https://github.com/helge17/tuxguitar/issues) are used for bug reports and feature requests. Take care to read this [guideline](ISSUES.md) before creating a new issue.
If you have questions, you need support or you want to contact the developers it's preferable to create a [discussion](https://github.com/helge17/tuxguitar/discussions).

To test the application, work-in-progress *snapshots* are published quite frequently, see [releases](https://github.com/helge17/tuxguitar/releases). Try to always use the most recent snapshot, and report the issues you may get through.


## Translate app content

TuxGuitar is available in many languages, but yours may be missing. We also regularly add new features and, therefore, new text strings to translate. Finally, not all translations are exhaustive. There's always work to do.

Translation files are available:

- [here](https://github.com/helge17/tuxguitar/tree/master/common/resources/lang) for the main application (desktop and Android);
- [here](https://github.com/helge17/tuxguitar/tree/master/desktop/TuxGuitar-cocoa-integration-swt/share/lang) for the macOS specific translations;
- there for plugins: [tuner](https://github.com/helge17/tuxguitar/tree/master/desktop/TuxGuitar-tuner/share/lang), [batch file converter](https://github.com/helge17/tuxguitar/tree/master/desktop/TuxGuitar-converter/share/lang), [jack](https://github.com/helge17/tuxguitar/tree/master/desktop/TuxGuitar-jack/share/lang) and [pdf export](https://github.com/helge17/tuxguitar/tree/master/desktop/TuxGuitar-pdf-ui/share/lang).

Other translation files specific to Android are present, these are derived from the files above.

The easiest way to update one translation is probably to open the English "messages.properties" file and the corresponding file for your language side by side. Those files can be edited with any standard text editor. Every line starting with a "#" character will be ignored and the corresponding message will be displayed in English. To translate a string: add the translated text after the "=" sign, remove the leading "# " and you're done.

To submit an updated translation, use the standard [GitHub process](https://docs.github.com/en/get-started/quickstart/contributing-to-projects).
For small fixes you can as well drop an updated (zipped) file in a [discussion](TODO).


## Contributing code

TuxGuitar provides Android and desktop applications:

- files specific to Android application are located in the [android](../android) folder;
- files specific to desktop applications are located in the [desktop](../desktop) folder;
- files common to Android and desktop applications are located in the [common](../common) folder.

Desktop application is available in different flavors:

- platform: FreeBSD, Linux (.tar.gz), Linux (.deb), macOS, Windows (standalone version), Windows (installable version);
- UI framework: SWT, JavaFX.

Note: only the SWT variants are included in the official releases, JavaFX variants support is not guaranteed.

Application is essentially developed in Java, however, some *native modules* can also include parts developed in another language (e.g. C). The application is built using Maven. For detailed build instructions please refer to [INSTALL.md](../INSTALL.md) file.

For each {platform, UI framework} couple, one Maven project is defined in a `pom.xml` file. All Maven projects are located in the [desktop/build-scripts](../desktop/build-scripts) folder. Each pom file defines all the successive build steps.

Note: it should be possible to build TuxGuitar also on some other architectures, but only x86_64 architecture is supported in this repo.

Refer to [IDEs](IDEs.md) for instructions to set up a development environment.

## Submit a contribution

If you want to implement one feature request or to fix an issue, mention it explicitly in the issue's discussion, so that others do not try to address it in parallel.
To submit a contribution, follow the GitHub [guidelines](https://docs.github.com/en/get-started/exploring-projects-on-github/contributing-to-a-project#making-a-pull-request) and create a pull request. If your pull request implements or fixes an issue, make this explicit in the pull request's description (e.g. "should fix issue #xxx").
