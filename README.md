MetricsReloaded
===============

Automated code metrics plugin for IntelliJ IDEA and IntelliJ Platform IDEs.
You can find a nice description of MetricsReloaded on the
[IntelliJ IDEA Blog][1].

Getting started
---------------

Select `Calculate Metrics...` in the `Analyze` menu or use
`Find Action...` in the `Help` menu and search for the `Calculate Metrics...`
action. Try the built in *Lines of code metrics* profile first, if you haven't
used MetricsReloaded before.

Command line
------------

Metrics can also be calculated from the command line, for integration into
build servers. The results are saved into the specified xml file for later
analysis. Enter `idea metrics -h` on the terminal for help. Make sure
IntelliJ IDEA is not running when you try to invoke MetricsReloaded from the
terminal, or it will not work. For a truly headless experience add the option
`-Djava.awt.headless=true` to the `idea.vmoptions` file in the `bin`
directory of the IntelliJ IDEA installation you are using on the build server.

Installation
------------

Find and install MetricsReloaded from IntelliJ IDEA's plugin manager.
Alternatively you can download [the zip file][2] manually and unpack it in
IntelliJ IDEA's plugin directory.

Development
-----------

You will need an installation of IntelliJ IDEA Ultimate to build
MetricsReloaded. This does not have to be the same version of IntelliJ IDEA 
used to open the project. The oldest version of IntelliJ IDEA that can be used
to build the plugin is 2019.1, i.e. currently the oldest version supported by
MetricsReloaded.

The IntelliJ IDEA used to build MetricsReloaded needs to have the Scala 
plugin installed. MetricsReloaded also depends on plugins only available in
IntelliJ IDEA Ultimate, like the CSS and the JavaScript and TypeScript plugin,
which is why IntelliJ IDEA Community cannot be used for MetricsReloaded 
development.

1. Open the MetricsReloaded project in the IntelliJ IDEA you use for
   development. For example IntelliJ IDEA Ultimate 2020.1
   
2. Define Path Variables.

   On opening the project a warning about missing path variables should pop up.
   Click the link in the popup or go to
   `Settings | Appearance & Behavior | Path Variables` and define two
   path variables: `INTELLIJ_IDEA` and `PLUGIN_DIR`. 
       
   `INTELLIJ_IDEA` should point to the directory where the IntelliJ IDEA 
   from the previously defined IntelliJ Platform Plugin SDK is installed. 
   For example: /Applications/IntelliJ IDEA 2019.1.app/Contents
   
   `PLUGIN_DIR` should point to the plugin directory of the IntelliJ IDEA
   from the previously defined IntelliJ Platform Plugin SDK. In my case:
   /Users/bas/Library/Application Support/IntelliJIdea2019.1
   
3. Create the IntelliJ Platform Plugin SDK

   Go to `File | Project structure`.
   In the left pane, under Project Settings, click Project.
   In the right pane, click `Add IntelliJ Platform Plugin SDK` or 
   `Add SDK | IntelliJ Platform Plugin SDK...` in the Project 
   SDK combo box. If you are using an older version of IntelliJ IDEA click
   `New | Intellij Platform Plugin SDK`. (This area of the project
   configuration has seen some evolution the last couple of versions of
   IntelliJ IDEA).
   In the window that appears, specify the folder where Intellij IDEA 2019.1
   (or newer) is installed. This SDK can have any name, but if you name it
   `IntelliJ IDEA 2019.1`, the project files should remain unchanged.
   
4. Build the project

   Close and reopen the project, to make sure the specified path variables
   take effect. Then you can build the project by invoking:

   `Build | Build Project`
          
5. Improve these instructions

   If you have used this "How to build" guide, please submit a pull request or
   a bug report with improvements or problems that you found.


[1]: http://blog.jetbrains.com/idea/2014/09/touring-plugins-issue-1/
[2]: http://plugins.jetbrains.com/plugin/93
